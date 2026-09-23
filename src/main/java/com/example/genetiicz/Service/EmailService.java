package com.example.genetiicz.Service;

import com.example.genetiicz.DTO.ContactFormDTO;
import com.example.genetiicz.DTO.ResetPasswordDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    //Value annotation here for receiving emails on the requested email for interests.
    @Value("${MAIL_CONTACT}")
    private String myPersonalEmail;

    //Injecting .env values into application.yaml and adding the value here.
    @Value("${MAIL_USERNAME}")
    private String emailUsername;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationEmail(String from, String to, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setFrom(from); //this line solved the issue with determination of local address issue!!
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text,true);

        javaMailSender.send(message);
    }

    public void sendOneTimePasswordEmail(String from, String to, String subject, String text) throws MessagingException {
        /*
        The next step with the javaMailSender is to send the actual otp code for authentication
        https://docs.spring.io/spring-security/reference/servlet/authentication/onetimetoken.html this reference is goated.
         */

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text,true);

        /*
        so this message will be send now instead with an otp for authentication.
        and I also need a message in the AuthService that explicitly does the business logic for checking if the verification code
        is valid or invalid for the users session.

         */
        javaMailSender.send(message);
    }


    //So today i need to implement a mail method that sends the mail to the user after requesting to change password.
    public void sendPasswordResetEmail (String from, String to,String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text,true);

        javaMailSender.send(message);
    }

    public String contactFormWithTopic(ContactFormDTO contactFormDTO,String subject) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();

        //this is for debugging, but the values are added, checked at the terminal log.
        // I will still keep this here for reassurance later if something bricks up.
        if(getMyPersonalEmailAndOtherEmail().isBlank()) {
            return getMyPersonalEmailAndOtherEmail();
        }

        //I declare this reference variable with a switch case that contains the literal values of contactForm.DTO, and also the literal value of enumerated list of contactTopic.
        String destinationEmail = switch (contactFormDTO.contactTopic()) {
            //so for each case, I want the user to pick the correct case, and then send the email from with the correct case.

            // I think that the best way is to part them, one for tickets and requests, and other mail is for collab, ideas or other requests such as offers for example and et cetera.
            case Offers,Request,Ideas,justSayHi-> myPersonalEmail.trim();
            case Issues -> emailUsername; //this is the email smtp is configured to
        };

        //Now I need to set message.set values for the requested ticket.

        /*
        So google smtp mail ask for correct google account, since a mailbox needs always to have a mail to respond from.
        So I want to implement the actual business logic in here instead, since this doesn't require a user to be on an authenticated stage to send a contact schema.
         */
        message.setFrom(emailUsername); // this is the .env variable from the google smtp mail.
        message.setTo(destinationEmail); // the message with the topic should go to me, and the cases will switch based on picked topic and sent to myPersonalEmail, and stored in destinationEmail
        message.setReplyTo(contactFormDTO.email()); //the users dto email.
        message.setSubject(String.valueOf(contactFormDTO.contactTopic())); //So the Subject is not longer 'subject' but it is the String value of the contactformDTO that has the literal topic chosen.
        String emailBody = "From: " + contactFormDTO.firstName() + " " + contactFormDTO.lastName()
                + " (" + contactFormDTO.email() + ")\n\n"
                + contactFormDTO.message();
        message.setText(emailBody);

        javaMailSender.send(message);
        return destinationEmail;
    }

    //Method to help me find out why smtp is throwing off on simple message.
    public String getMyPersonalEmailAndOtherEmail() throws MessagingException {
        if(!myPersonalEmail.isBlank() || !emailUsername.isBlank()) {
            System.out.println("The local email is added as: " + myPersonalEmail + "\nAnd also SMTP mail: " + emailUsername);
        } else {
            throw new MessagingException("The PERSONAL email address is not to be found!!");

        }
        return myPersonalEmail + "/" + emailUsername;
    }
}
