package edu.utdallas.csmc.web.service;


import edu.utdallas.csmc.web.model.mail.EmailDetails;
import edu.utdallas.csmc.web.repository.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import lombok.extern.java.Log;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Log
public class EmailService implements IEmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public boolean sendSimpleMail(EmailDetails details, String firstName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true, "utf-8");
            String htmlMsg = getEmailBody(firstName);
            helper.setText(htmlMsg, true);
            helper.setFrom(sender);
            helper.setBcc(details.getRecipient());
            helper.setSubject(details.getSubject());

            ClassPathResource qrCodeResource = new ClassPathResource("assets/images/CSMC_Survey_QR.png");
            helper.addInline("qrCodeImage", qrCodeResource);

            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            log.info("EMAIL TEST - EXCEPTION MESSAGE ======> "+e.getMessage());
            return false;
        }
    }

    private String getEmailBody(String firstName) {
        String feedbackUrl = "https://utdallas.qualtrics.com/jfe/form/SV_5mtLA5ozJGOWn5k"; // Feedback Form URL

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Feedback Request</title>\n" +
                "    <style>\n" +
                "        body, p { margin: 0; padding: 0; }\n" +
                "        body { background-color: #f4f4f4; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border-radius: 5px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); }\n" +
                "        .spacer { height: 20px; }\n" +
                "        .header { background-color: #00853E; color: #ffffff; text-align: center; padding: 30px; border-radius: 5px 5px 0 0; }\n" +
                "        .content { padding: 30px; text-align: left; }\n" +
                "        .qr-code { display: block; width: 200px; height: 200px; object-fit: cover; margin:0 auto;}\n" +
                "         .content-text {font-weight: normal;}\n" +
                "        .cta-button { display: inline-block; padding: 10px 20px; background-color: #C3501B; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 15px; }\n" +
                "        .footer { background-color: #00853E; color: #ffffff; text-align: center; padding: 10px; border-radius: 0 0 5px 5px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Computer Science Mentor Center - UTD</h1>\n" +
                "            <p>Feedback Request</p>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <h2>Hello " + firstName + ",</h2>\n" +
                "            <h3>Can you spare 20 seconds to tell us about your last visit to the CSMC?\n" +
                "            <br/>\n" +
                "            <br/>\n" +
                "            <img class=\"qr-code\" src=\"cid:qrCodeImage\" alt=\"Scan QR Code to Give Feedback\" width=\"200\" height=\"200\"/>\n" +
                "            <br/>\n" +
                "            <br/>\n" +
                "            <p class=\"content-text\">We hope you had a great experience at our mentor center. Your feedback is important to us and will help us improve our services.</p>\n" +
                "            <br/>\n" +
                "            <br/>\n" +
                "            <p><a class=\"cta-button\" href=\"" + feedbackUrl + "\">Give Feedback</a></p>\n" +
                "        </div>\n" +
                "        <div class=\"spacer\"></div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2024 CSMC @ UTD. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public String
    sendMailWithAttachment(EmailDetails details) {

        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());

            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);

            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        } catch (Exception e) {

            return "Error while sending mail!!!";
        }
    }

    @Override
    public boolean sendConfirmationMail(EmailDetails details) {
        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            String htmlMsg = getConfirmationEmailBody(details.getMsgBody());
            helper.setText(htmlMsg, true);
            helper.setFrom(sender);
            helper.setBcc(details.getRecipient());
            helper.setSubject(details.getSubject());

            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            log.info("EMAIL TEST - EXCEPTION MESSAGE ======> "+e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendReminderMail(EmailDetails details) {
        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            String htmlMsg = getReminderEmailBody(details.getMsgBody());
            helper.setText(htmlMsg, true);
            helper.setFrom(sender);
            helper.setBcc(details.getRecipient());
            helper.setSubject(details.getSubject());

            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            log.info("EMAIL TEST - EXCEPTION MESSAGE ======> "+e.getMessage());
            return false;
        }
    }

    private String getReminderEmailBody(String mailContentDetails) {
        String[] splitMailContentDetails = mailContentDetails.split("=");
        String sessionTopic = splitMailContentDetails[2];
        String sessionStartTime = splitMailContentDetails[0];
        String sessionEndTime = splitMailContentDetails[1];

        String formattedDate = null;
        String startTime = null;
        String endTime = null;

        // Formatting date to "MM/dd/yyyy" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(sessionStartTime);
            formattedDate = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Formatting time to "h:mm a" format
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(sessionStartTime);
            startTime = timeFormat.format(startDate);
            Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(sessionEndTime);
            endTime = timeFormat.format(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String studentName = splitMailContentDetails[3];
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reminder Mail</title>\n" +
                "    <style>\n" +
                "        /* Reset some default styles */\n" +
                "        body, p {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        /* Set a background color */\n" +
                "        body {\n" +
                "            background-color: #f4f4f4;\n" +
                "        }\n" +
                "        /* Center the email content */\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        /* Add some spacing */\n" +
                "        .spacer {\n" +
                "            height: 20px;\n" +
                "        }\n" +
                "        /* Style the header */\n" +
                "        .header {\n" +
                "            background-color: #00853E; /* Comet Green */\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "            padding: 30px;\n" +
                "            border-radius: 5px 5px 0 0;\n" +
                "        }\n" +
                "        /* Style the main content */\n" +
                "        .content {\n" +
                "            padding: 30px;\n" +
                "        }\n" +
                "        /* Style the call to action button */\n" +
                "        .cta-button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #C3501B; /* Comet Orange */\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        /* Style the footer */\n" +
                "        .footer {\n" +
                "            background-color: #00853E; /* Comet Green */\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 0 0 5px 5px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Computer Science Mentor Center - UTD</h1>\n" +
                "            <p>Reminder Mail</p>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <h2>Hello "+studentName+",</h2>\n" +
                "            <p>This email is to remind you about your registration for the upcoming session - "+sessionTopic+"</p>\n" +
                "            <br/>\n" +
                "            <p>Session Details:</p>\n" +
                "            <br/>\n" +
                "            <p>Session Name: "+ sessionTopic+"</p>\n" +
                "            <p>Date & Time: "+ formattedDate+" "+startTime+" - "+endTime+"</p>\n" +
                "            <br/>\n" +
                "        </div>\n" +
                "        <div class=\"spacer\"></div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2024 CSMC @ UTD. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }

    private String getConfirmationEmailBody(String sessionDetails) {
        String[] splitSessionDetails = sessionDetails.split("=");
        String sessionTopic = splitSessionDetails[0];
        String sessionStartTime = splitSessionDetails[1];
        String sessionEndTime = splitSessionDetails[2];

        String formattedDate = null;
        String startTime = null;
        String endTime = null;

        // Formatting date to "MM/dd/yyyy" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(sessionStartTime);
            formattedDate = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Formatting time to "h:mm a" format
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(sessionStartTime);
            startTime = timeFormat.format(startDate);
            Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(sessionEndTime);
            endTime = timeFormat.format(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sessionLocation = splitSessionDetails[3];
        String studentNetId = splitSessionDetails[5];
        String studentName = splitSessionDetails[6];
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Confirmation Mail</title>\n" +
                "    <style>\n" +
                "        /* Reset some default styles */\n" +
                "        body, p {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        /* Set a background color */\n" +
                "        body {\n" +
                "            background-color: #f4f4f4;\n" +
                "        }\n" +
                "        /* Center the email content */\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            padding: 20px;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 5px;\n" +
                "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        /* Add some spacing */\n" +
                "        .spacer {\n" +
                "            height: 20px;\n" +
                "        }\n" +
                "        /* Style the header */\n" +
                "        .header {\n" +
                "            background-color: #00853E; /* Comet Green */\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "            padding: 30px;\n" +
                "            border-radius: 5px 5px 0 0;\n" +
                "        }\n" +
                "        /* Style the main content */\n" +
                "        .content {\n" +
                "            padding: 30px;\n" +
                "        }\n" +
                "        /* Style the call to action button */\n" +
                "        .cta-button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            background-color: #C3501B; /* Comet Orange */\n" +
                "            color: #ffffff;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        /* Style the footer */\n" +
                "        .footer {\n" +
                "            background-color: #00853E; /* Comet Green */\n" +
                "            color: #ffffff;\n" +
                "            text-align: center;\n" +
                "            padding: 10px;\n" +
                "            border-radius: 0 0 5px 5px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Computer Science Mentor Center - UTD</h1>\n" +
                "            <p>Confirmation Mail</p>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <h2>Hello "+studentName+",</h2>\n" +
                "            <p>This mail is to confirm your registration for the upcoming session - "+sessionTopic+"</p>\n" +
                "            <br/>\n" +
                "            <p>Session Details:</p>\n" +
                "            <br/>\n" +
                "            <p>Student NetID: "+ studentNetId+"</p>\n" +
                "            <p>Session Name: "+ sessionTopic+"</p>\n" +
                "            <p>Location: "+ sessionLocation+"</p>\n" +
                "            <p>Date & Time: "+ formattedDate+" "+startTime+" - "+endTime+"</p>\n" +
                "            <br/>\n" +
                "        </div>\n" +
                "        <div class=\"spacer\"></div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2024 CSMC @ UTD. All rights reserved.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
    }
}
