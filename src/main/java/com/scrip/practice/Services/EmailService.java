package com.scrip.practice.Services;

import com.scrip.practice.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void SendMail(EmailDto dto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        context.setVariable("nombre", dto.getSubject());
        context.setVariable("mensaje", dto.getBody());
        context.setVariable("codigo", (int) (Math.random() * 10000));

        String htmlContent = templateEngine.process("HtmlTemplates/EmailTemplate", context);

        helper.setTo(dto.getTo());
        helper.setSubject("Notificación xddddd");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}