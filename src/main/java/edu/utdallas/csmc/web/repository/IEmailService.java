package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.mail.EmailDetails;

public interface IEmailService {

    boolean sendSimpleMail(EmailDetails details, String firstName);
    String sendMailWithAttachment(EmailDetails details);

    boolean sendConfirmationMail(EmailDetails details);

    boolean sendReminderMail(EmailDetails details);
}

