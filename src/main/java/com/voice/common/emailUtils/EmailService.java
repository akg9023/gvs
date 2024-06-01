package com.voice.common.emailUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Configuration config;

    public void sendEmailWithTemplate(String to, String sub, String from, String name, int membersCount, float amount) {
        Map<String, Object> templateVariablesMap = new HashMap<>();
        templateVariablesMap.put("name",name);
        templateVariablesMap.put("membersCount",membersCount);
        templateVariablesMap.put("amount",amount);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            // set mediaType
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            // add attachment
           // helper.addAttachment("logo.png", new ClassPathResource("logo.png"));

            Template t = config.getTemplate("mail_template.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, templateVariablesMap);

            helper.setTo(to);
            helper.setText(html, true);
            helper.setSubject(sub);
            helper.setFrom(from);

            mailSender.send(message);

        } catch (MessagingException | IOException | TemplateException e) {
            logger.error("Freemarker template email sender error {}",e.getMessage());
        }

    }
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
    public void sendHtmlEmail()  {
        try{
            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress("ved.srav@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, "ved.giav@gmail.com");
            message.setSubject("Test email from GVS");

            String htmlContent = "<h1>This is a test Spring Boot email</h1>" +
                    "<p>It can contain <strong>HTML</strong> content.</p>";
            message.setContent(htmlContent, "text/html; charset=utf-8");

            mailSender.send(message);
        }catch (MessagingException e){
            logger.error("Html Email sending error {}",e.getMessage());
        }

    }
}