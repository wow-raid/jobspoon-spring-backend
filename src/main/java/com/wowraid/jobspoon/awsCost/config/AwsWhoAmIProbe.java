package com.wowraid.jobspoon.awsCost.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.sts.StsClient;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class AwsWhoAmIProbe {

    @Qualifier("adminCostExplorerClient")
    private final CostExplorerClient ce;

    @PostConstruct
    public void check() {
        try {
            // CE와 동일 Provider로 STS 생성
            var credsProv = ce.serviceClientConfiguration().credentialsProvider();
            var sts = StsClient.builder().credentialsProvider(credsProv).build();

            var me = sts.getCallerIdentity();
            log.info("[AWS WhoAmI] account={}, arn={}", me.account(), me.arn());
        } catch (Exception e) {
            log.error("[AWS WhoAmI] FAILED", e);
        }
    }
}