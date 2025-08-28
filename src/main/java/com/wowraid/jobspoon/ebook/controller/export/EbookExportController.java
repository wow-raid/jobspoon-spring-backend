package com.wowraid.jobspoon.ebook.controller.export;

import com.wowraid.jobspoon.ebook.controller.export.request_form.TermsPdfGenerateRequestForm;
import com.wowraid.jobspoon.ebook.controller.export.response_form.TermsPdfGenerateResponseForm;
import com.wowraid.jobspoon.ebook.service.export.dto.PdfExportService;
import com.wowraid.jobspoon.ebook.service.export.dto.request.PdfGenerateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EbookExportController {

    private final PdfExportService pdfExportService;

    @PostMapping(value = "/pdf/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<StreamingResponseBody> generate(
            @Valid @RequestBody TermsPdfGenerateRequestForm form,
            @RequestHeader("X-Account-Id") Long accountId) {

        PdfGenerateRequest request = PdfGenerateRequest.builder()
                .accountId(accountId)
                .termIds(form.getTermIds())
                .title(form.getTitle())
                .build();

        var result = pdfExportService.generate(request);

        TermsPdfGenerateResponseForm responseForm = TermsPdfGenerateResponseForm.builder()
                .ebookId(result.meta().getEbookId())
                .filename(result.meta().getFilename())
                .count(result.meta().getCount())
                .build();

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


        String filename = (responseForm.getFilename() != null && !responseForm.getFilename().isBlank())
                ? responseForm.getFilename()
                : "jobspoon_terms_" + LocalDate.now() + ".pdf";

        ContentDisposition cd = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8) // filename & filename* 자동 처리
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(body);
    }
}