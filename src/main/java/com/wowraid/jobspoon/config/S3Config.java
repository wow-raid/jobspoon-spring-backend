package com.wowraid.jobspoon.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${PROFILE_AWS_ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${PROFILE_AWS_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Bean
    @Qualifier("s3Client")
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

        // --- 신고 파일용 S3 설정 ---
        @Value("${REPORT_AWS_ACCESS_KEY_ID}")
        private String reportAccessKey;

        @Value("${REPORT_AWS_SECRET_ACCESS_KEY}")
        private String reportSecretKey;

        @Bean
        @Qualifier("reportS3Client")
        public S3Client reportS3Client() {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(reportAccessKey, reportSecretKey)
                            )
                    )
                    .build();
        }
}