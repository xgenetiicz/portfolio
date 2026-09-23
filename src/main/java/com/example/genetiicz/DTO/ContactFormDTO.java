package com.example.genetiicz.DTO;


import com.example.genetiicz.Enum.ContactTopic;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactFormDTO(

        //Need to add the enum field lists for topic

        @Enumerated ContactTopic contactTopic,

        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @NotBlank(message = "email name cannot be blank")
        @Email(message = "Please provide a valid e-mail address")
        String email,

        @NotBlank(message = "Message cannot be blank")
        @Size(min = 10, message = "Message cannot be less than 10 characters.")
        String message
        ) {

}
