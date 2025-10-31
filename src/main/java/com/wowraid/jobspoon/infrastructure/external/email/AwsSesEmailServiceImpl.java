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

    @Override
    public void sendSignupWelcomeEmail(String to, String nickname) {
        try {
            String subject = "[잡스푼] 회원가입을 환영합니다";
            String htmlBody = buildSignupEmail(nickname);

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(Destination.builder().toAddresses(to).build())
                    .message(Message.builder()
                            .subject(Content.builder().charset("UTF-8").data(subject).build())
                            .body(Body.builder()
                                    .html(Content.builder().charset("UTF-8").data(htmlBody).build())
                                    .build())
                            .build())
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("✅ 회원가입 환영 메일 발송 성공: {} (MessageId: {})", to, response.messageId());
        } catch (SesException e) {
            log.error("❌ 회원가입 환영 메일 발송 실패: {}", e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public void sendWithdrawalConfirmationEmail(String to, String nickname) {
        try {
            String subject = "[잡스푼] 회원 탈퇴가 완료되었습니다";
            String htmlBody = buildWithdrawalEmail(nickname);

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(fromEmail)
                    .destination(Destination.builder().toAddresses(to).build())
                    .message(Message.builder()
                            .subject(Content.builder().charset("UTF-8").data(subject).build())
                            .body(Body.builder()
                                    .html(Content.builder().charset("UTF-8").data(htmlBody).build())
                                    .build())
                            .build())
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("✅ 회원탈퇴 완료 메일 발송 성공: {} (MessageId: {})", to, response.messageId());
        } catch (SesException e) {
            log.error("❌ 회원탈퇴 완료 메일 발송 실패: {}", e.awsErrorDetails().errorMessage());
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

    private String buildSignupEmail(String nickname) {
        return """
    <!DOCTYPE html>
    <html lang="ko">
    <head>
        <meta charset="UTF-8" />
        <title>JobSpoon 회원가입 환영</title>
    </head>
    <body style="font-family: 'Arial', sans-serif; background-color:#f3f4f6; margin:0; padding:40px;">
        <div style="max-width:640px; margin:0 auto; background:#f9fafb; border-radius:12px; border:1px solid #e5e7eb; padding:24px;">
            <h3 style="font-size:18px; font-weight:700; color:#111827; text-align:center; margin-bottom:16px;">
                회원가입 완료 메일
            </h3>
            <div style="background:white; border-radius:10px; padding:32px 28px; color:#374151; line-height:1.7; text-align:center;">
                <h4 style="font-size:16px; font-weight:600; color:#111827;">
                    <span style="color:#2563eb; font-weight:700;">JobSpoon</span>에 오신 것을 진심으로 환영합니다!
                </h4>
                <p style="font-size:14px;">안녕하세요, %s님 😊</p>
                <p style="font-size:14px;">지금 바로 아래 가이드와 함께 시작해보세요.</p>
                <ul style="list-style:none; padding-left:0; margin:16px 0; color:#1f2937;">
                    <li><b>마이페이지 설정하기</b> — 프로필과 관심 분야를 등록해보세요.</li>
                    <li><b>AI 면접 체험하기</b> — 실전 대비 모의 면접으로 연습하세요.</li>
                    <li><b>신뢰점수 살펴보기</b> — 활동에 따라 성장하는 나의 신뢰 지수를 확인해보세요.</li>
                </ul>
                <a href="https://job-spoon.com/mypage"
                   style="display:inline-block; background:#2563eb; color:white; font-weight:600; padding:10px 22px;
                          border-radius:8px; text-decoration:none; margin-top:16px;">시작하기</a>
                <p style="font-size:12px; color:#9ca3af; margin-top:32px;">
                    본 메일은 발신 전용입니다. 문의사항은 support@job-spoon.com 으로 보내주세요.<br/>
                    © 2025 JobSpoon. All rights reserved.
                </p>
            </div>
        </div>
    </body>
    </html>
    """.formatted(nickname);
    }

    private String buildWithdrawalEmail(String nickname) {
        return """
    <!DOCTYPE html>
    <html lang="ko">
    <head>
        <meta charset="UTF-8" />
        <title>JobSpoon 회원탈퇴 확인</title>
    </head>
    <body style="font-family: 'Arial', sans-serif; background-color:#f3f4f6; margin:0; padding:40px;">
        <div style="max-width:640px; margin:0 auto; background:#f9fafb; border-radius:12px; border:1px solid #e5e7eb; padding:24px;">
            <h3 style="font-size:18px; font-weight:700; color:#111827; text-align:center; margin-bottom:16px;">
                회원탈퇴 확인 메일
            </h3>
            <div style="background:white; border-radius:10px; padding:32px 28px; color:#374151; line-height:1.7; text-align:center;">
                <h4 style="font-size:16px; font-weight:600; color:#111827;">
                    그동안 <span style="color:#2563eb; font-weight:700;">JobSpoon</span>을 이용해주셔서 감사합니다.
                </h4>
                <p style="font-size:14px;">회원 탈퇴가 정상적으로 처리되었습니다.</p>
                <p style="font-size:14px;"><b>계정 정보 및 이용 기록은 7일간 보관 후 완전히 삭제</b>됩니다.</p>
                <hr style="border:none; border-top:1px solid #e5e7eb; margin:20px 0; width:80%;" />
                <p style="font-size:14px;">언제든 다시 돌아오신다면, 이전보다 더 나은 JobSpoon으로 맞이하겠습니다 💚</p>
                <a href="https://job-spoon.com/signup"
                   style="display:inline-block; background:#2563eb; color:white; font-weight:600; padding:10px 22px;
                          border-radius:8px; text-decoration:none; margin-top:16px;">다시 가입하기</a>
                <p style="font-size:12px; color:#9ca3af; margin-top:32px;">
                    본 메일은 발신 전용입니다.<br/>
                    재가입 문의: support@job-spoon.com
                </p>
            </div>
        </div>
    </body>
    </html>
    """.formatted(nickname);
    }
}