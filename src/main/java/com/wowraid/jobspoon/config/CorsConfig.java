package com.wowraid.jobspoon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {


   @Value("${cors.allow.origins}")
   private String[] allowedOrigins;

   @Override
   public void addCorsMappings(CorsRegistry registry) {
       registry.addMapping("/**")
               .allowedOrigins(allowedOrigins)
               .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
               .allowedHeaders("*")                   // 모든 요청 헤더 허용 (Authorization 포함)
               .exposedHeaders(                       // JS에서 읽어야 하는 응답 헤더 노출
                       "Content-Disposition",         // 파일 다운로드 이름 검증용
                       "Ebook-Id",                    // 생성된 전자책 ID
                       "Ebook-Filename",              // 서버가 정한 파일명(메타)
                       "Ebook-Count",                 // 포함 용어 수
                       "Ebook-Skipped",               // 스킵된 항목 수
                       "Ebook-Error"                  // 오류 코드/메시지
               )
               .allowCredentials(true)                // 자격증명(쿠키/Authorization 헤더) 허용
               .maxAge(3600);                         // 프리플라이트 캐시(초) → 불필요한 OPTIONS 트래픽 감소
   }
}