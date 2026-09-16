package com.example.genetiicz.Config;

import com.example.genetiicz.Entity.UserEntity;
import com.example.genetiicz.Enum.Role;
import com.example.genetiicz.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if(!userRepository.existsByRole(Role.ADMIN)) {
            UserEntity admin = new UserEntity();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            admin.setUserName("genetiicz");
            admin.setFirstName("Genti");
            admin.setLastName("Rudi");
            admin.setUserCreated(LocalDateTime.now());
            userRepository.save(admin);

            System.out.println("Registered Admin: " + admin.getFirstName() + " "+ admin.getLastName());
        }
    }
}