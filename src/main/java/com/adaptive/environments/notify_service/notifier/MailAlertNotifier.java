package com.adaptive.environments.notify_service.notifier;

import com.adaptive.environments.notify_service.model.AlertRecord;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(prefix = "notifier.mail", name = "enabled", havingValue = "true")
public class MailAlertNotifier implements AlertNotifier{

    private final JavaMailSender mailSender;

    @Value("${alert.mail.to}")
    private String recipient;

    @Value("${alert.mail.from}")
    private String sender;

    public MailAlertNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Override
    public void notify(AlertRecord alert) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipient);
            helper.setFrom(sender);
            helper.setSubject("IoT System Alert: " + alert.getSeverity());
            helper.setText(buildEmailBody(alert), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send alert email: " + e.getMessage());
        }
    }

    private String buildEmailBody(AlertRecord alert) {
        return "<h3>⚠️ Alert Detected</h3>" +
                "<p><strong>Device:</strong> " + alert.getDeviceId() + "</p>" +
                "<p><strong>Severity:</strong> " + alert.getSeverity() + "</p>" +
                "<p><strong>Timestamp:</strong> " + alert.getTimestamp() + "</p>" +
                "<p><strong>Description:</strong> " + alert.getDescription() + "</p>";
    }

}