package com.wowraid.jobspoon.awsCost.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminAwsProps(
        String adminAwsAccessKey,
        String adminAwsSecretKey
) { }