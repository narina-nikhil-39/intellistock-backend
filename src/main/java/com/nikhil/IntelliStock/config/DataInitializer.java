package com.nikhil.IntelliStock.config;

import com.nikhil.IntelliStock.entity.Role;
import com.nikhil.IntelliStock.entity.User;
import com.nikhil.IntelliStock.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminUsername = "admin";

        // Checks if an admin already exists in MySQL
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User(
                    null,
                    adminUsername,
                    passwordEncoder.encode("Admin@123"), // The default admin password
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            System.out.println(">>> [SECURITY] Default Admin account provisioned: username='admin' <<<");
        }
    }
}