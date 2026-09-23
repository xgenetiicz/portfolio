package com.example.genetiicz.Controller;

import com.example.genetiicz.DTO.ContactFormDTO;
import com.example.genetiicz.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class ContactController {

    @Autowired
    private EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    public ResponseEntity <String> contactFormWithTopic(@Valid @RequestBody ContactFormDTO contactFormDTO, String subject) throws MessagingException {
        String emailSent = emailService.contactFormWithTopic(contactFormDTO,subject);

        if(emailSent.isBlank()) {
            return ResponseEntity.status(404).body("Email is not sent, and cannot reach the destination email");
        } else {
            return ResponseEntity.status(201).body("Email sent to: " + emailSent);
        }
    }

}
