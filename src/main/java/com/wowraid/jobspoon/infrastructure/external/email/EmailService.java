package com.wowraid.jobspoon.infrastructure.external.email;

public interface EmailService {
    void sendInterviewResultNotification(String to, String userToken);
    void sendErrorNotification(String to, String userToken);
}