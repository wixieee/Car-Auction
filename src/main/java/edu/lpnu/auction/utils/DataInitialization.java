package edu.lpnu.auction.utils;

import edu.lpnu.auction.model.User;
import edu.lpnu.auction.model.enums.AuthProvider;
import edu.lpnu.auction.model.enums.Role;
import edu.lpnu.auction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.admin.email}")
    private String adminEmail;

    @Value("${application.admin.password}")
    private String adminPassword;

    @Value("${application.admin.firstname}")
    private String adminFirstName;

    @Value("${application.admin.lastname}")
    private String adminLastName;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.existsByEmail(adminEmail)){
            return;
        }

        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.addRole(Role.ROLE_ADMIN);
        admin.addRole(Role.ROLE_USER);
        admin.setProvider(AuthProvider.LOCAL);

        userRepository.save(admin);

    }
}
