package edu.utdallas.csmc.web.tasks;

import edu.utdallas.csmc.web.model.mail.EmailDetails;
import edu.utdallas.csmc.web.repository.CourseRepository;
import edu.utdallas.csmc.web.repository.IEmailService;
import edu.utdallas.csmc.web.repository.RegistrationRepository;
import edu.utdallas.csmc.web.repository.SwipeRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Log
public class Task {

    @Autowired
    private IEmailService emailService;

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private SwipeRepository swipeRepository;

    // sec min hr day week month
    @Scheduled(cron = "0 0 0 * * *")
    @Async
    public void taskWithFeedbackEmailsDetails() {

        EmailDetails details = new EmailDetails();
        details.setSubject("CSMC Feedback");

        // Fetch swipe details for yesterday's sessions
        List<SwipeRepository.SwipeDetails> swipeDetails = swipeRepository.findYesterdaySwipesForReminderMails();

        //actual code

        for (SwipeRepository.SwipeDetails swipeDetail : swipeDetails) {
            String netId = swipeDetail.getNetId();
            String firstName = swipeDetail.getFirstName();

//            System.out.print("Swipe details " +netId+" "+firstName );

            log.info("Mail Content Details -- "+firstName);

            String recipientEmail = netId + "@utdallas.edu";
            String[] recipients = new String[]{"nikhil.hegde@utdallas.edu",recipientEmail};

            System.out.println("recipients"+recipients);

            details.setRecipient(recipients);

            boolean status = emailService.sendSimpleMail(details, firstName);
            if (status)
                log.info("Email sent successfully to " + netId + " :: " + LocalDateTime.now());
            else
                log.info("Email sending failed to " + netId + " :: " + LocalDateTime.now());
        }

//testing
//        List<String> recipients = new ArrayList<>();
//        recipients.add("nikhil.hegde@utdallas.edu");
//        recipients.add("smith@utdallas.edu");
//
//        details.setRecipient(recipients.toArray(new String[0]));
//        boolean status = emailService.sendSimpleMail(details, "Nikhil");
//        if(status)
//            log.info("Emails sent successfully :: " + LocalDateTime.now());
//        else
//            log.info("Emails sent failed :: " + LocalDateTime.now());
    }

    public void taskWithConfirmationMailDetails(String sessionDetails) {

        EmailDetails details = new EmailDetails();
        details.setSubject("CSMC Confirmation Mail");

        // TODO :: Pull data from DB
        List<String> recipients = new ArrayList<>();
        recipients.add("nikhil.hegde@utdallas.edu");

        String[] splitSessionDetails = sessionDetails.split("=");
        String studentEmail = splitSessionDetails[4];

        recipients.add(studentEmail);

        details.setMsgBody(sessionDetails);

        details.setRecipient(recipients.toArray(new String[0]));
        boolean status = emailService.sendConfirmationMail(details);
        if(status)
            log.info("Emails sent successfully :: " + LocalDateTime.now());
        else
            log.info("Emails sent failed :: " + LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Async
    public void taskWithReminderMailDetails() {

        EmailDetails details = new EmailDetails();
        details.setSubject("CSMC Reminder Mail");


        // Fetch registration details for tomorrow's sessions
        List<String> registrationMails = registrationRepository.findTomorrowRegistrationsForReminderMails();

        // Iterate over each registration
        for (String registrationMail : registrationMails) {
            String netId = registrationMail.trim();

            // Fetch registration details including session topic, start date, and end date for the current user
            RegistrationRepository.RegistrationDetails detail = registrationRepository.getDetailsReminderMailsWithNetId(netId);

            // Construct email message body
            String mailContentDetails = "";
                mailContentDetails = detail.getStartTime()+"="+detail.getEndTime()+"="+detail.getTopic()+"="+detail.getFirstName();

            log.info("Mail Content Details -- "+mailContentDetails);


            // Send email to the current user
            String recipientEmail = netId + "@utdallas.edu";
            String[] recipients = new String[]{"nikhil.hegde@utdallas.edu", recipientEmail};

            details.setRecipient(recipients);

            details.setMsgBody(mailContentDetails);
            boolean status = emailService.sendReminderMail(details);
            if (status)
                log.info("Email sent successfully to " + netId + " :: " + LocalDateTime.now());
            else
                log.info("Email sending failed to " + netId + " :: " + LocalDateTime.now());
        }
    }
}
