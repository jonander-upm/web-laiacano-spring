package com.laiacano.core.services;

import com.laiacano.core.data.entities.User;
import com.laiacano.core.utils.RoutingUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailingService {
    private static final String RESET_PASSWORD_TEMPLATE = "forgot-password-email.html";
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final RoutingUtils routingUtils;

    public MailingService(JavaMailSender emailSender, SpringTemplateEngine templateEngine, RoutingUtils routingUtils) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.routingUtils = routingUtils;
    }

    public void sendResetPasswordMessage(User user) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getUsername());
        variables.put("resetUrl", routingUtils.getFrontUrl() + "/reset-password?username=" + user.getUsername()
                + "&token=" + user.getResetPasswordToken());
        context.setVariables(variables);
        helper.setTo(user.getEmail());
        helper.setSubject("Laia Cano - Reset Password");
        String html = templateEngine.process(RESET_PASSWORD_TEMPLATE, context);
        helper.setText(html, true);

        emailSender.send(message);
    }
}
