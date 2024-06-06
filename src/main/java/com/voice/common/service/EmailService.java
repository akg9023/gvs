package com.voice.common.service;

import com.voice.common.model.YatraRegEmail;
import com.voice.dbRegistration.model.DevoteeInfo;
import com.voice.dbRegistration.service.DatabaseService;
import com.voice.yatraRegistration.memberReg.model.EmailMember;
//import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    private static final String SPRING_PROFILES_ACTIVE_PROD="prod";
    Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private Configuration config;

    @Autowired
    private Environment environment;

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
    public void sendHtmlEmail(String to, String subject, String htmlString)  {
        try{
            String toRecipient = "ved.giav@gmail.com";
            String subjectEmail = "Test email from GVS";
            String[] activeProfiles = environment.getActiveProfiles();
            if (Arrays.asList(activeProfiles).contains(SPRING_PROFILES_ACTIVE_PROD))
           {
                 toRecipient = to;
                 subjectEmail = subject;
                 logger.info("Send Email Prod Profile active");
            }
            logger.info("Send Email Dev Profile active");
            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress("ved.srav@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, toRecipient);
            message.setSubject(subjectEmail);

//            String htmlContent = "<h1>This is a test Spring Boot email</h1>" +
//                    "<p>It can contain <strong>HTML</strong> content.</p>";
            message.setContent(htmlString, "text/html; charset=utf-8");

            mailSender.send(message);
        }catch (MessagingException e){
            logger.error("Html Email sending error {}",e.getMessage());
        }

    }

    public String getEmailStringRegistration(EmailMember member){
        StringBuilder builder = new StringBuilder();
        if(member!=null) {
            Map<String, YatraRegEmail> dbMap = member.getDbRegistrationMemberMap();
            Map<String, YatraRegEmail> yatraMap = member.getYatraRegistrationMemberMap();

            if(yatraMap!=null){
            for (Map.Entry<String, YatraRegEmail> yatra : yatraMap.entrySet()) {
                String registrationName = "Yatra Registration";
                if (!dbMap.isEmpty()) {
                    if (dbMap.containsKey(yatra.getKey())) {
                        dbMap.remove(yatra.getKey());
                        registrationName = "Yatra and Db Registration";
                    }
                }
                YatraRegEmail d = yatra.getValue();
                if (d != null) {

                    builder.append("<tr>")
                            .append("<td>").append(d.getMemberId()).append("</td>")
                            .append("<td>").append(d.getFname()).append(" ").append(d.getLname()).append("</td>")
                            .append("<td>").append(d.getCalculatedAge()).append("</td>")
                            .append("<td>").append(d.getGender()).append("</td>")
                            .append("<td>").append(d.getCity()).append("</td>")
                            .append("<td>").append(databaseService.getMaskedPrimaryPhone(d.getPrimaryPhone())).append("</td>")
                            .append(registrationName)
                            .append("</tr>");
                    builder.append("\n");
                }

            }
        }
            if(dbMap!=null){
                for(Map.Entry<String, YatraRegEmail> db: dbMap.entrySet()){
                    YatraRegEmail d = db.getValue();
                    if(d!=null){
                        builder.append("<tr>")
                                .append("<td>").append(d.getMemberId()).append("</td>")
                                .append("<td>").append(d.getFname()).append(" ").append(d.getLname()).append("</td>")
                                .append("<td>").append(d.getCalculatedAge()).append("</td>")
                                .append("<td>").append(d.getGender()).append("</td>")
                                .append("<td>").append(d.getCity()).append("</td>")
                                .append("<td>").append(databaseService.getMaskedPrimaryPhone(d.getPrimaryPhone())).append("</td>")
                                .append("DB Registration")
                                .append("</tr>");
                        builder.append("\n");
                    }

                }
            }
        }


        return builder.toString();
    }
    public void sendEmailHelperRegistration(Map<String, EmailMember> map) {
        try {
            map.forEach((email, emailMember) -> {
                String memberString = getEmailStringRegistration(emailMember);
                String htmlContent = "";
                String dearHead = "";
                if (!memberString.isEmpty()) {

                    htmlContent =
                            "<html>" +
                                    "<head>" +
                                    "<style>\n" +
                                    "        table {\n" +
                                    "            border-collapse: collapse;\n" +
                                    "        }\n" +
                                    "        th, td {\n" +
                                    "            border: 1px solid black;\n" +
                                    "            padding: 10px;\n" +
                                    "        }\n" +
                                    "    </style>"+
                                    "</head>" +
                                    "<body>"+
                                    "<h3>Dear " + emailMember.getUserFname()+ " " + emailMember.getUserLname() + "</h3>" +
                            "<p><h4>" + "Last Year Yatra Registration Details" + "</h4></p>" +
                            "<p>Your Id:  " + emailMember.getId() + "</p>" +
                            "<p><h4>" + "Member Registration Details" + "</h4></p>" +
                            "<table>\n" +
                            "    <thead>\n" +
                            "        <tr>\n" +
                            "            <th>Id</th>\n" +
                            "            <th>Name</th>\n" +
                            "            <th>Age</th>\n" +
                            "            <th>Gender</th>\n" +
                            "            <th>City</th>\n" +
                            "            <th>Contact</th>\n" +
                                    "            <th>Remarks</th>\n" +
                            "        </tr>\n" +
                            "    </thead>\n" +
                            "    <tbody>\n" +
                                    memberString +
                            "    </tbody>\n" +
                            "</table>\n" +
//                            "<p>"+memberString+ "</p><br>"+
                            "<p><h2>Yatra Team</h2></p>" +
                            "<p>Contact: 1234</p>"+
                                    "</body>"+
                                    "</html>"

                    ;
                } else {
                    htmlContent = "<h3>Dear Devotee " + "</h3>" +
                            "<p><h4>" + "Last Year Yatra Registration Details" + "</h4></p>" +
                            "<p>Your Id" + emailMember.getId() + "</p>" +
                            "<p><h3>Yatra Team</h3></p>" +
                            "<p>Contact: 1234</p>"
                    ;
                }
                sendHtmlEmail(email, "Yatra Registration Details", htmlContent);

            });

        } catch (Exception e) {
            logger.error("Send email helper error {}", e.getMessage());
        }
    }
}