package com.wowraid.jobspoon.infrastructure.external.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsSesEmailServiceImpl implements EmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    @Override
    public void sendInterviewResultNotification(String to, Long interviewId) {
        try {
            String subject = "[잡스푼] AI 면접 평가가 완료되었습니다";
            String htmlBody = buildNotificationEmail(interviewId);

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(Destination.builder()
                            .toAddresses(to)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .charset("UTF-8")
                                    .data(subject)
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .charset("UTF-8")
                                            .data(htmlBody)
                                            .build())
                                    .build())
                            .build())
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("✅ SES 이메일 발송 성공: {} (MessageId: {})", to, response.messageId());

        } catch (SesException e) {
            log.error("❌ SES 이메일 발송 실패: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    @Override
    public void sendErrorNotification(String to, Long interviewId) {
        try {
            String subject = "[잡스푼] AI 면접 평가 중 오류 발생";
            String htmlBody = buildErrorEmail(interviewId);

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(Destination.builder()
                            .toAddresses(to)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .charset("UTF-8")
                                    .data(subject)
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .charset("UTF-8")
                                            .data(htmlBody)
                                            .build())
                                    .build())
                            .build())
                    .build();

            sesClient.sendEmail(request);
            log.info("✅ SES 오류 알림 발송 성공: {}", to);

        } catch (SesException e) {
            log.error("❌ SES 이메일 발송 실패: {}", e.awsErrorDetails().errorMessage());
        }
    }

    private String buildNotificationEmail(Long interviewId) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +

                "<div style='text-align: center; padding: 20px 0;'>" +
                "<h1 style='color: #4CAF50; margin: 0;'>🎉 AI 면접 평가 완료!</h1>" +
                "</div>" +

                "<div style='background-color: #f9f9f9; padding: 30px; border-radius: 10px; margin: 20px 0;'>" +
                "<p style='font-size: 16px; margin-bottom: 20px;'>안녕하세요,</p>" +
                "<p style='font-size: 16px; margin-bottom: 20px;'>" +
                "방금 완료하신 <strong>AI 면접 평가 결과</strong>가 준비되었습니다! 🎊" +
                "</p>" +
                "<p style='font-size: 16px; margin-bottom: 20px;'>" +
                "아래 버튼을 클릭하여 상세한 평가 결과를 확인해보세요." +
                "</p>" +
                "</div>" +

                "<div style='text-align: center; margin: 40px 0;'>" +
                "<a href='https://job-spoon.com/vue-ai-interview/ai-interview/result/" + interviewId + "' " +
                "style='display: inline-block; padding: 15px 40px; background-color: #4CAF50; color: white; " +
                "text-decoration: none; border-radius: 8px; font-size: 18px; font-weight: bold;'>" +
                "📊 결과 확인하기" +
                "</a>" +
                "</div>" +

                "<div style='background-color: #e3f2fd; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<p style='margin: 0; font-size: 14px; color: #1976d2;'>" +
                "💡 <strong>결과 페이지에서 확인할 수 있는 내용:</strong><br>" +
                "• 질문별 상세 피드백 및 첨삭<br>" +
                "• 육각형 역량 차트<br>" +
                "• 전체 면접 총평" +
                "</p>" +
                "</div>" +

                "<hr style='border: none; border-top: 1px solid #ddd; margin: 30px 0;'>" +
                "<p style='font-size: 12px; color: #999; text-align: center;'>" +
                "이 이메일은 잡스푼 AI 면접 서비스에서 자동으로 발송되었습니다.<br>" +
                "문의사항: support@job-spoon.com" +
                "</p>" +

                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildErrorEmail(Long interviewId) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'></head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h1 style='color: #f44336;'>⚠️ 평가 처리 중 오류 발생</h1>" +
                "<p>죄송합니다. AI 면접 평가 처리 중 일시적인 오류가 발생했습니다.</p>" +
                "<p>고객센터로 문의해주세요: support@job-spoon.com</p>" +
                "<p>참조 코드: " + interviewId + "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}