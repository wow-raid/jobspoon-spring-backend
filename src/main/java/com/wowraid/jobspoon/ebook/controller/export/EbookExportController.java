package com.wowraid.jobspoon.ebook.controller.export;

import com.wowraid.jobspoon.ebook.controller.export.request_form.TermsPdfGenerateRequestForm;
import com.wowraid.jobspoon.ebook.service.export.dto.PdfExportService;
import com.wowraid.jobspoon.ebook.service.export.dto.request.PdfGenerateRequest;
import com.wowraid.jobspoon.redis_cache.RedisCacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EbookExportController {

    private final PdfExportService pdfExportService;
    private final RedisCacheService redisCacheService;

    // Authorization 헤더에서 accountId 복원
    private Long accountIdFromAuth(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            log.warn("[auth] Authorization header missing/blank");
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        final String token = authorizationHeader.startsWith("Bearer") ? authorizationHeader.substring(7).trim() : authorizationHeader.trim();
        final String tokenPrefix = token.length() >= 8 ? token.substring(0, 8) : token;
        log.debug("[auth] Bearer token received. len={}, prefix={}...", token.length(), tokenPrefix);

        try {
            Long accountId = redisCacheService.getValueByKey(token, Long.class);
            if(accountId == null){
                log.warn("[auth] Redis map not found for tokenPrefix={}..., returning 401", tokenPrefix);
                throw new ResponseStatusException(UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            }
            log.info("[auth] tokenPrefix={}... -> accountId={}", tokenPrefix, accountId);
            return accountId;
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            // Redis 인증/연결 문제 등 모든 예외를 401로 반환하고 로그 남김
            log.error("[auth] Redis access failed for tokenPrefix={}... : {}", tokenPrefix, e.toString(), e);
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }

    @PostMapping(value = "/pdf/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<StreamingResponseBody> generate(
            @Valid @RequestBody TermsPdfGenerateRequestForm form,
            @RequestHeader("Authorization") String authorizationHeader) {

        final Long accountId = accountIdFromAuth(authorizationHeader);

        PdfGenerateRequest request = PdfGenerateRequest.builder()
                .accountId(accountId)
                .termIds(form.getTermIds())
                .title(form.getTitle())
                .build();

        var result = pdfExportService.generate(request);

        final String metaFilename = result.meta().getFilename();
        final String raw = (metaFilename != null && !metaFilename.isBlank())
                ? metaFilename
                : "jobspoon_terms_" + LocalDate.now() + ".pdf";

        // 1) ASCII fallback (브라우저 호환용)
        String asciiFallback = Normalizer.normalize(raw, Normalizer.Form.NFKD)
                .replaceAll("[^\\p{ASCII}]", "_"); // 한글 등 비ASCII → '_'
        if (!asciiFallback.endsWith(".pdf")) asciiFallback += ".pdf";

        // 2) RFC 5987 percent-encoding (공백은 %20)
        String encoded = URLEncoder.encode(raw, StandardCharsets.UTF_8)
                .replace("+", "%20");

        // 3) Content-Disposition 직접 구성
        String contentDisposition =
                "attachment; filename=\"" + asciiFallback + "\"; filename*=UTF-8''" + encoded;

        StreamingResponseBody body = os -> {
            try {
                result.stream().writeTo(os);
            } catch (IOException e) {
                log.warn("PDF streaming failed: {}", e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                log.error("Unexpected render error", e);
                throw new IOException("Render failed", e);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .header("Ebook-Id", String.valueOf(result.meta().getEbookId()))
                .header("Ebook-Filename", raw)
                .header("Ebook-Count", String.valueOf(result.meta().getCount()))
                .header("Ebook-Skipped", "0")
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
}