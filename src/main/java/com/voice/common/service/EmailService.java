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
            //            String toRecipient = "ved.giav@gmail.com";
//            String subjectEmail = "Test email from GVS";
//            String[] activeProfiles = environment.getActiveProfiles();
//            if (Arrays.asList(activeProfiles).contains(SPRING_PROFILES_ACTIVE_PROD))
//           {
//                 toRecipient = to;
//                 subjectEmail = subject;
//                 logger.info("Send Email Prod Profile active");
//            }
//            logger.info("Send Email Dev Profile active");
            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress("yatra.gaurangavedic@gmail.com"));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
           // message.setRecipients(MimeMessage.RecipientType.TO, "akg9023@gmail.com");
           // message.setRecipients(MimeMessage.RecipientType.TO, "ved.srav@gmail.com");
            message.setSubject(subject);

//            String htmlContent = "<h1>This is a test Spring Boot email</h1>" +
//                    "<p>It can contain <strong>HTML</strong> content.</p>";
            message.setContent(htmlString, "text/html; charset=utf-8");
            System.out.println("TMP Email Sent to " +to);
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
            Map<String, YatraRegEmail> tmpRegMap = member.getTmpIdRegistrationMemberMap();

            if(tmpRegMap!=null){
                for(Map.Entry<String, YatraRegEmail> tmpReg: tmpRegMap.entrySet()){
                    YatraRegEmail d = tmpReg.getValue();
                    if (d != null) {

                        builder.append("<tr>")
                                .append("<td>").append(d.getDevoteeId()).append("</td>")
                                .append("<td>").append(d.getName()).append("</td>")
                                .append("<td>").append(d.getCalculatedAge()).append("</td>")
                                .append("<td>").append(d.getGender()).append("</td>")
                                .append("<td>").append(d.getCurrentCity()).append("</td>")
                                .append("<td>").append(databaseService.getMaskedPrimaryPhone(d.getContact())).append("</td>")
                                //.append("<td>").append(registrationName).append("</td>")
                                .append("</tr>");
                        builder.append("\n");
                    }
                }
            }

            if(yatraMap!=null){
            for (Map.Entry<String, YatraRegEmail> yatra : yatraMap.entrySet()) {
                String registrationName = "Yatra Registration";
                if (dbMap!=null && !dbMap.isEmpty()) {
                    if (dbMap.containsKey(yatra.getKey())) {
                        dbMap.remove(yatra.getKey());
                        registrationName = "Yatra and Db Registration";
                    }
                }
                YatraRegEmail d = yatra.getValue();
                if (d != null) {

                    builder.append("<tr>")
                            .append("<td>").append(d.getDevoteeId()).append("</td>")
                            .append("<td>").append(d.getName()).append("</td>")
                            .append("<td>").append(d.getCalculatedAge()).append("</td>")
                            .append("<td>").append(d.getGender()).append("</td>")
                            .append("<td>").append(d.getCurrentCity()).append("</td>")
                            .append("<td>").append(databaseService.getMaskedPrimaryPhone(d.getContact())).append("</td>")
                            //.append("<td>").append(registrationName).append("</td>")
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
                                .append("<td>").append(d.getDevoteeId()).append("</td>")
                                .append("<td>").append(d.getName()).append("</td>")
                                .append("<td>").append(d.getCalculatedAge()).append("</td>")
                                .append("<td>").append(d.getGender()).append("</td>")
                                .append("<td>").append(d.getCurrentCity()).append("</td>")
                                .append("<td>").append(databaseService.getMaskedPrimaryPhone(d.getContact())).append("</td>")
                                //.append("<td>").append("DB Registration").append("</td>")
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
                if(!email.isEmpty()) {
                    String memberString = getEmailStringRegistration(emailMember);
                    String htmlContent = "";
                    String dearHead = "";
                    if (!memberString.isEmpty()) {

                        htmlContent = getHtmlEmailTemplate(memberString)


                        ;
                    }
                    sendHtmlEmail(email, "GVS Yatra Registration Details", htmlContent);
                    return;

                }});

        } catch (Exception e) {
            logger.error("Send email helper error {}", e.getMessage());
        }
    }
    public void sendTMPIdEmailHelperRegistration(Map<String, EmailMember> map)
        {
            try {
                map.forEach((email, emailMember) -> {
                    if(!email.isEmpty()) {
                        String memberString = getEmailStringRegistration(emailMember);
                        String htmlContent = "";
                        if (!memberString.isEmpty()) {

                            htmlContent = getHtmlEmailTemplateForTMPId(memberString)


                            ;
                        }
                        sendHtmlEmail(email, "GVS Yatra Registration Details", htmlContent);

                    }});

            } catch (Exception e) {
                logger.error("Send email helper error {}", e.getMessage());
            }
        }

    public String getHtmlEmailTemplate(String memberString){
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>GVS Dham Yatra Invitation</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 800px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #590099;\n" +
                "            font-size: large;\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "            border-top-left-radius: 10px;\n" +
                "            border-top-right-radius: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 10px;\n" +
                "            background-color: #f9f9f9;\n" +
                "            border-radius: 5px;\n" +
                "            overflow-x: auto;\n" +
                "        }\n" +
                "\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            background-color: #006699;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            padding: 10px 20px;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .button:hover {\n" +
                "            background-color: #005580;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: left;\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #666666;\n" +
                "            background-color: #f2f2f2;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 0 0 10px 10px;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 5px;\n" +
                "            table-layout: fixed;\n" +
                "        }\n" +
                "\n" +
                "        th, td {\n" +
                "            padding: 8px;\n" +
                "            text-align: left;\n" +
                "            font-size: 13px; /* Default font size */\n" +
                "            word-wrap: break-word;\n" +
                "        }\n" +
                "\n" +
                "        th {\n" +
                "            background-color: #006699;\n" +
                "            color: #ffffff;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "\n" +
                "        td {\n" +
                "            padding-top: 8px;\n" +
                "            padding-bottom: 8px;\n" +
                "        }\n" +
                "\n" +
                "        tr:nth-child(even) {\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "\n" +
                "        tr:nth-child(odd) {\n" +
                "            background-color: #e0e0e0;\n" +
                "        }\n" +
                "\n" +
                "       \n" +
                "        .age{\n" +
                "            width: 50px;\n" +
                "        }\n" +
                "        .gender{\n" +
                "            width: 60px;\n" +
                "        }\n" +
                "        .id{\n" +
                "            width: 100px;\n" +
                "        }\n" +
                "\n" +
                "        @media screen and (max-width: 600px) {\n" +
                "            .container {\n" +
                "                padding: 9px;\n" +
                "            }\n" +
                "\n" +
                "            th, td {\n" +
                "                font-size: 10px; /* Reduced font size for smaller screens */\n" +
                "                padding: 3px; /* Reduced padding for better mobile view */\n" +
                "            }\n" +
                "            .age{\n" +
                "                width: 25px;\n" +
                "            }\n" +
                "            .gender{\n" +
                "                width: 37px;\n" +
                "            }\n" +
                "            .id{\n" +
                "                width: 60px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h2 style=\"margin: 0;\">Gauranga Vedic Society</h2>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Hare Krishna,</p>\n" +
                "            <p>We extend our heartfelt gratitude for giving us the opportunity to serve you with your family in last year's Vrindavan yatra. If you missed it, we have got you covered.</p>\n" +
                "            <p>We are excited to announce our upcoming annual yatra in Chitrakoot Dham, scheduled from <strong>20th November 2024 to 24th November 2024</strong>. Your presence would mean a lot to us.</p>\n" +
                "            <p>For registration of previously enrolled members, please use the corresponding HLZ ids from last year. For enrolling new members, please visit our new website <a href=\"https://www.gaurangavedic.org.in/yatra\">https://www.gaurangavedic.org.in/yatra</a>.</p>\n" +
                "                <p> Here are the details of your enrolled members:</p>\n" +
                "            \n" +
                "            <table>\n" +
                "                <thead>\n" +
                "                    <tr>\n" +
                "                        <th class=\"id\">Id</th>\n" +
                "                        <th>Name</th>\n" +
                "                        <th class=\"age\">Age</th>\n" +
                "                        <th class=\"gender\">Gender</th>\n" +
                "                        <th>City</th>\n" +
                "                        <th class=\"name-column\">Contact</th>\n" +
                "                    </tr>\n" +
                "                </thead>\n" +
                "                <tbody>\n" +
                                    memberString+
                "                </tbody>\n" +
                "            </table>\n" +
                "            \n" +
                "            <p style=\"margin-top: 20px;\">If you have any queries, please WhatsApp the registration desk <a href=\"https://wa.me/918986472757\">here</a>.</p><p>We look forward to seeing you in Chitrakoot Dham.</p>\n" +
                "            <p>In your service,<br>\n" +
                "                GVS Dham Yatra Committee\n" +
                "            </p>\n" +
                "            <p></p>\n" +
                "            <p><a href=\"https://gaurangavedic.org.in\" style=\"color: #006699; text-decoration: none;\" target=\"_blank\">https://gaurangavedic.org.in</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
    }
    public String getHtmlEmailTemplateForTMPId(String memberString){
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>GVS Dham Yatra Invitation</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 800px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            border-radius: 10px;\n" +
                "            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "\n" +
                "        .header {\n" +
                "            background-color: #590099;\n" +
                "            font-size: large;\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "            border-top-left-radius: 10px;\n" +
                "            border-top-right-radius: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            padding: 10px;\n" +
                "            background-color: #f9f9f9;\n" +
                "            border-radius: 5px;\n" +
                "            overflow-x: auto;\n" +
                "        }\n" +
                "\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            background-color: #006699;\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            padding: 10px 20px;\n" +
                "            border-radius: 5px;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .button:hover {\n" +
                "            background-color: #005580;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            text-align: left;\n" +
                "            margin-top: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #666666;\n" +
                "            background-color: #f2f2f2;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 0 0 10px 10px;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 5px;\n" +
                "            table-layout: fixed;\n" +
                "        }\n" +
                "\n" +
                "        th, td {\n" +
                "            padding: 8px;\n" +
                "            text-align: left;\n" +
                "            font-size: 13px; /* Default font size */\n" +
                "            word-wrap: break-word;\n" +
                "        }\n" +
                "\n" +
                "        th {\n" +
                "            background-color: #006699;\n" +
                "            color: #ffffff;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "\n" +
                "        td {\n" +
                "            padding-top: 8px;\n" +
                "            padding-bottom: 8px;\n" +
                "        }\n" +
                "\n" +
                "        tr:nth-child(even) {\n" +
                "            background-color: #f2f2f2;\n" +
                "        }\n" +
                "\n" +
                "        tr:nth-child(odd) {\n" +
                "            background-color: #e0e0e0;\n" +
                "        }\n" +
                "\n" +
                "       \n" +
                "        .age{\n" +
                "            width: 50px;\n" +
                "        }\n" +
                "        .gender{\n" +
                "            width: 60px;\n" +
                "        }\n" +
                "        .id{\n" +
                "            width: 100px;\n" +
                "        }\n" +
                "\n" +
                "        @media screen and (max-width: 600px) {\n" +
                "            .container {\n" +
                "                padding: 9px;\n" +
                "            }\n" +
                "\n" +
                "            th, td {\n" +
                "                font-size: 10px; /* Reduced font size for smaller screens */\n" +
                "                padding: 3px; /* Reduced padding for better mobile view */\n" +
                "            }\n" +
                "            .age{\n" +
                "                width: 25px;\n" +
                "            }\n" +
                "            .gender{\n" +
                "                width: 37px;\n" +
                "            }\n" +
                "            .id{\n" +
                "                width: 60px;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h2 style=\"margin: 0;\">Gauranga Vedic Society</h2>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Hare Krishna,</p>\n" +
                "            <p>We extend our heartfelt gratitude for giving us the opportunity to serve you with your family in last year's Vrindavan yatra. If you missed it, we have got you covered.</p>\n" +
                "            <p>We are excited to announce our upcoming annual yatra in Chitrakoot Dham, scheduled from <strong>20th November 2024 to 24th November 2024</strong>. Your presence would mean a lot to us.</p>\n" +
                "            <p>All members with TMP ID are invalid from this year and won't be allowed to register for the camp. Therefore we request you to enroll them as new members. </p>" +
                "<p>For enrolling new members, please visit our new website <a href=\"https://www.gaurangavedic.org.in/yatra\">https://www.gaurangavedic.org.in/yatra</a>.</p>\n" +
                "<p>Go to Database Entry fill the registration form and get HLZ ID. With this HLZ Id of members you will be able to do camp registration.  </p>"+
                "                <p> Here are the details of your TMP Id members:</p>\n" +
                "            \n" +
                "            <table>\n" +
                "                <thead>\n" +
                "                    <tr>\n" +
                "                        <th class=\"id\">Id</th>\n" +
                "                        <th>Name</th>\n" +
                "                        <th class=\"age\">Age</th>\n" +
                "                        <th class=\"gender\">Gender</th>\n" +
                "                        <th>City</th>\n" +
                "                        <th class=\"name-column\">Contact</th>\n" +
                "                    </tr>\n" +
                "                </thead>\n" +
                "                <tbody>\n" +
                memberString+
                "                </tbody>\n" +
                "            </table>\n" +
                "            \n" +
                "            <p style=\"margin-top: 20px;\">If you have any queries, please WhatsApp the registration desk <a href=\"https://wa.me/918986472757\">here</a>.</p><p>We look forward to seeing you in Chitrakoot Dham.</p>\n" +
                "            <p>In your service,<br>\n" +
                "                GVS Dham Yatra Committee\n" +
                "            </p>\n" +
                "            <p></p>\n" +
                "            <p><a href=\"https://gaurangavedic.org.in\" style=\"color: #006699; text-decoration: none;\" target=\"_blank\">https://gaurangavedic.org.in</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
    }
}