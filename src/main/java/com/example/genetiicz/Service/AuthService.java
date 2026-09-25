package com.example.genetiicz.Service;

import com.example.genetiicz.DTO.*;
import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Enum.Role;
import com.example.genetiicz.Repository.UserRepository;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import javax.security.auth.login.AccountNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    //Injecting .env values into application.yaml and adding the value here.
    @Value("${MAIL_USERNAME}")
    private String emailUsername;

    private EmailService emailService;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;


    public AuthService(EmailService emailService,
                       UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder)
    {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /*
       TODO:
        AuthService handles all authentication logic:
        1. registerUser - creates the user, generates a verification code, sets it on the user and sends it via email.
        2. verifyUser - finds the user by email, checks if the code matches and is not expired, then sets enabled = true.
        3. authenticate - checks if the user is enabled, then authenticates and generates a JWT token.
        UserService only handles CRUD operations on users.
        this makes sense because it is easier to implement auth and do the logic here.

        Everything is fetched from:
        https://github.com/Erik-Cupsa/Spring-Security-Tutorial/blob/main/demo/src/main/java/com/example/demo/service/AuthenticationService.java

     */

    //userId is by primitive datatype that is Long.

    public boolean registerAdmin(UserDTO userDTO, VerifyUserDto verifyUserDto) throws SecurityException{
        /*
        I want to check if the method is initialized at all.
        Because right now, NOTHING IS HAPPENING, NOR EXCEPTION IS CALLED OUT.
        so there is something failing silently and I don't know what is happening.
         */

        //Add now the hashedPassword and declare it before we set it in the statements:
        String hashedPass = passwordEncoder.encode(userDTO.getPassword());

        //parsedData
        DateTimeFormatter dayMonthYear = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate parsedData = LocalDate.parse(userDTO.getBirthDate(), dayMonthYear);

        //Lager et nytt objekt i heap memory av user
        //Så bruker vi DTO for validering og input fra brukeren.
        //Dette er en egen klasse i ../DTO/UserDTO ved
        //bare bruk av getters/setters med Lombok.
        UserEntity admin = new UserEntity();
        //Optional<UserEntity> doesAdminExists = userRepository.findByRoleAndEmail(Role.ADMIN,userDTO.getEmail());

        //I need to have an admin logic where this actually checks for TOTP; Time Based One - Time Password
        //With google authentication - so need to configure the authentication in google cloud also.



        //Set the values for the first admin registration
        // The idea is still to check the statement with boolean methods for true or false,
        //so both needs to be true -> by this where the Admin does not exists by mail or as the correct Role.
        if(!userRepository.existsByEmail(userDTO.getEmail()) && !userRepository.existsByRole(Role.ADMIN)) { //if there doesn't exists any admin, we set the values first and create one.
            try {
                System.out.println("It's actually trying to set values for the new admin");
                admin.setFirstName(userDTO.getFirstName());
                admin.setLastName(userDTO.getLastName());
                admin.setUserName(userDTO.getUserName());
                admin.setPassword(hashedPass); //and we set it here. this is declared on the start of the method and the passwordEncoder object is retrieved with @Bean.
                admin.setEmail(userDTO.getEmail());
                admin.setBirthDate(parsedData);//We change this also for birthdate.
                admin.setUserCreated(LocalDateTime.now()); //this will set the values for timestamp og @CreationTimestamp - and defined as properties in application.yaml
                admin.setRole(Role.ADMIN); // This method will only include the admin role
                admin.setVerificationCode(generateCode());
                admin.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
                admin.setEnabled(false);
                sendVerificationEmail(admin); //important
                userRepository.save(admin);
                System.out.println("Added admin with full name as: " + admin.getFirstName() + " " + admin.getLastName());
                System.out.println("Username: " + admin.getUserName());
                return true;
            } catch (Exception exception) {
                throw new IllegalStateException("User is not authorized as Admin",exception);
            }
        }
        System.out.println("The method did return false, not an exception");
        return false;
    }

    public boolean registerUser (UserDTO userDTO, VerifyUserDto verifyUserDto) {
        //We store Bcrypt encoding in a String as hashedPassword
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
        UserEntity addUser = new UserEntity();
        //format the data self with making a local variable within the method and store
        //this in dayMonthYear.
        DateTimeFormatter dayMonthYear = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        /*So through parsing and wrapping the object, i could parse it one parameter,
        //since dayMonthYear couldnt be set since lombok expects one parameter, not two.
        //and the reference object of dayMonthYear get sets as an another variable, to do this
        // i needed too store the LocalDate in a new reference where this get the parsed format of
        current object....
        */
        LocalDate parsedData = LocalDate.parse(userDTO.getBirthDate(), dayMonthYear);


        //I need to get the method to actually run in this method. By creating a object of the method.


        if(userRepository.existsByEmail(userDTO.getEmail()) || userRepository.existsByUsername(userDTO.getUserName()) && userRepository.existsByRole(Role.USERS)) { //Check if the user exists first always.
            System.out.println("You exists already as an user, try to log in or recover password with mail");
            return false;

        } else {
            addUser.setFirstName(userDTO.getFirstName());
            addUser.setLastName(userDTO.getLastName());
            addUser.setUserName(userDTO.getUserName());
            addUser.setEmail(userDTO.getEmail());
            addUser.setPassword(hashedPassword);
            addUser.setBirthDate(parsedData); //setting birthdate instead because it makes sense.
            addUser.setRole(Role.USERS);
            addUser.setVerificationCode(generateCode()); // by two methods the value is set for the user
            addUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15)); // verificationcode should expire every 15 minutes.
            addUser.setEnabled(false);
            sendVerificationEmail(addUser); //sendVerification email is the method.
            userRepository.save(addUser);
            System.out.print("User: " + addUser.getUserName() + " | birthdate: " + addUser.getBirthDate());
            return true;
        }
    }

    //Login Method - and this should be set with the otp session now.

    /*
    Okay, IntelliJ is referring to that I should use SneakyThrows, where this gives me a possibility to throw exceptions without explicitly declaring them in my
    method signature, as "throws theNameException"
    I like this, but it bypasses the strict level of the java compiler, so I shouldn't practice this a lot... because of security measures.
     */
    @SneakyThrows
    public String authenticate(LoginUserDTO loginUserDTO)  {
        Optional<UserEntity> authenticateUser = userRepository.findByEmail(loginUserDTO.getEmail()); //so i need to find it with optional first

        UserEntity user;
        if (authenticateUser.isPresent()) { //and then check if the user is present, when it is, check it for if it is enabled, but by this i need to create an new object where this is referred to the reference user
            user = authenticateUser.get();
            System.out.println("Enabled: " + user.isEnabled());
            if (user.isEnabled()) { //here I check with the boolean statement in UserRepository if the email is verified, it has the String email stored as parameter
                //We use this.authenticationManager and store this with a new value that has a new literal value of UsernamePassword and etc with the new values of the loginUserDTO values.
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDTO.getEmail(), loginUserDTO.getPassword()));

                //Now the otp values should be set here after setting the new values to the instance
                try {
                    user.setOtpCode(generateCode());
                    user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(5)); //I want the code to be valid for 5 minutes
                    userRepository.save(user);
                    sendOneTimePasswordEmail(user);
                } catch (AuthenticationFailedException e) {
                    throw new AuthenticationFailedException("*CRUCIAL: OTP authentication IS not working*",e);
                }
                return "OTP sent to your email"; //IF THIS IS TRUE, I WANT TO RETURN IT HERE AND STOP THE CODE FROM GOING FURTHER ON THIS METHOD.
            } else {
                throw new AccountStatusException("Account not verified. Please verify your account") {
                };
            }
        } else {
            throw new RuntimeException("Check authenticate, there are no authentication going on Login in AuthService." + authenticateUser);
        }
    }

    //the method that explicitly checks the generated code by verifying it.
    //it is set to void since this is just something that should be processed in a way
    //to find out if the user is actually verified or not.
    public void checkVerification(VerifyUserDto verifyUserDto) {
        Optional<UserEntity> checkVerifiedUser = userRepository.findByEmail(verifyUserDto.getEmail());
        // Check it with if statement if the user is still verified.
        if(checkVerifiedUser.isPresent()) {
            UserEntity user = checkVerifiedUser.get();// i retrieve the email from the UserEntity and store this in user
            if(user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) { // if the code is not valid anymore and before the date now
                //should throw exception as runtime with string message
                throw new RuntimeException("Verification code has expired");
            }


            if(user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
                user.setEnabled(true);//there is set an boolean value in UserEntity.
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificatonCode(String email) {
        Optional<UserEntity> checkVerifiedUser = userRepository.findByEmail(email);
        if(checkVerifiedUser.isPresent()) {
            UserEntity user = checkVerifiedUser.get();
            if(userRepository.isEnabled(user.getEmail())) {
                throw new RuntimeException("Account is already registered and verified.");
            }
            user.setVerificationCode(generateCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
        }
    }


    private void sendVerificationEmail(UserEntity user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE: " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to genetiicz.no!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(emailUsername,user.getEmail(), subject, htmlMessage); //I inject the fourth or first parameter with my emailUsername that contains the mail i am sending from.
            // Because when i setFrom(from) helper with MimeMessageHelper, i need also a parameter here, and this method actually builds and send the verificationEmail. So I am explicitly telling
            // that
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    //This method should be where this actually check for if the oneTimePassword is still available
    public UserEntity checkOneTimePassword(OtpDTO otpDTO, String email) throws AccountNotFoundException {

        //I check if there are any user that is registered already with the current email
        Optional <UserEntity> userCheck = userRepository.findByEmail(email);

        //if the user is present
        if(userCheck.isPresent() && userRepository.isEnabled(otpDTO.getEmail())) {
            //I want to store this user in a new variable where I can check if this user has confirmed their verification already,
            //If not, the user should not get access to login.
            UserEntity userOtp = userCheck.get();

            //If the users verifcationcode is befre the local time that is now, in other words the one that got sent is not available anymore
            if(userOtp.getOtpExpiresAt().isBefore(LocalDateTime.now())) {

                //I want to throw an exception on it, where this explicitly tells the user that the password is expired/Denied
                throw new SessionAuthenticationException("Expired OTP code, request a new one");
            }

            //If the otp is not expired, I should check that the OtpCode that has been sent, is the otp code that is supposed to verify the user's login method.
            //so the other object i am comparing to should be equals in terms object similarity, since they are represented in Strings.
            if(userOtp.getOtpCode().equals(otpDTO.getOtpCode())) {
                userOtp.setEnabled(true);
                userOtp.setOtpCode(null);
                userOtp.setOtpExpiresAt(null);
                userRepository.save(userOtp);

                //I want to return this so the user get's authenticated
                return userOtp;
            } else {
                //Because this should show an invalid state of authenticating, where this
                // will show with a proper message to user
                throw new IllegalStateException("Invalid code");
            }
            // I think it is important to have an else if statement with an inverted logic to check if the user is not enabled by verification
            //so it throw an exception to AccessDenied
        } else if (!userRepository.isEnabled(otpDTO.getEmail())) {
            throw new AccessDeniedException("User is not verified - cannot log in with OTP!");

        } else { //I also want to throw an exception if the user does NOT EXIST AT ALL.
            throw new AccountNotFoundException("You do not have an registered account, please register an account first.");
        }
    }

    //Here should the new method for OneTimePassword be for authentication.
    private void sendOneTimePasswordEmail(UserEntity user) throws MessagingException{
        String subject = "Login OTP Password";
        String loginCode = user.getOtpCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Genti's BuildHubs!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the OTP code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + loginCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try { //so with the build up, we set the emailService, where gets the javamail sender to run.
            //with the correct parameters such as from, to, subject and the message itself.
            emailService.sendOneTimePasswordEmail(emailUsername,user.getEmail(),subject,htmlMessage);
        } catch (MessagingException messagingException) {
            throw new MessagingException("The javaMailSender for otp verification does not work properly.",messagingException);
        }
    }

    /*
        Im going to reuse the sendVerificationEmail() method here but adapt it to sendPasswordResetEmail()
     */
    private void sendPasswordResetEmail(UserEntity user) { //TODO: Update with company logo
        String subject = "Account Password Reset";
        String resetPasswordCode = "RESET CODE: " + user.getOtpPassword();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to Genti's BuildHubs!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the reset code below to continue for changing password:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + resetPasswordCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendPasswordResetEmail(emailUsername,user.getEmail(), subject, htmlMessage); //I inject the fourth or first parameter with my emailUsername that contains the mail i am sending from.
            // Because when i setFrom(from) helper with MimeMessageHelper, i need also a parameter here, and this method actually builds and send the verificationEmail. So I am explicitly telling
            // that
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    public String forgotPassword(String email) throws UsernameNotFoundException{
        Optional<UserEntity> findUserByEmail =  userRepository.findByEmail(email);
        UserEntity user;
        if(findUserByEmail.isPresent()) {
                user = findUserByEmail.get();
                user.setOtpPassword(generateCode());
                user.setOtpPasswordExpiresAt(LocalDateTime.now().plusMinutes(5));
                userRepository.save(user);
                sendPasswordResetEmail(user);
        }
        return "If an account exists for: " + email + ", a reset link has been sent.";
    }

    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Optional<UserEntity> findUser = userRepository.findByEmail(resetPasswordDTO.email()); // referring to dto email

        if(findUser.isPresent()) {
            UserEntity user = findUser.get();
            if(user.getOtpPasswordExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Reset code has expired, request a new one");
            }
            if(user.getOtpPassword().equals(resetPasswordDTO.otpCode())){
                user.setPassword(passwordEncoder.encode(resetPasswordDTO.password()));
                user.setOtpPassword(null);
                user.setOtpPasswordExpiresAt(null);
                userRepository.save(user);
                return "Password reset successfully";
            } else {
                throw new RuntimeException("Invalid reset code");
            }
        } else {
         throw new RuntimeException("User not found");
        }
    }

    //The method that explicitly generate code, this will be now for verification but also user otp
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
