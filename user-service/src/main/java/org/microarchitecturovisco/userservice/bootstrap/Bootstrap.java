package org.microarchitecturovisco.userservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.userservice.domain.User;
import org.microarchitecturovisco.userservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserRepository userRepository;

    // Predefined lists of names and emails
    private final List<String> names = List.of("John", "Emma", "Michael", "Sophia", "William", "Olivia", "James", "Ava", "Alexander", "Isabella");
    private final List<String> domains = List.of("gmail.com", "yahoo.com", "hotmail.com", "outlook.com");

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap");

        List<User> users = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // Choose random name and email
            String name = names.get(ThreadLocalRandom.current().nextInt(names.size()));
            String domain = domains.get(ThreadLocalRandom.current().nextInt(domains.size()));
            String email = name.toLowerCase() + i + "@" + domain;

            // Generate random password
            String password = generateRandomPassword();

            // Create a new User instance
            User user = User.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .surname("Surname" + i)
                    .build();

            // Save the user
            users.add(userRepository.save(user));
        }

        // Log the saved users
        logger.info("Saved users: " + users);
    }

    // Method to generate a random password
    private String generateRandomPassword() {
        // Define characters for generating password
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = upperAlphabet.toLowerCase();
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_+{}[]<>?";

        String allChars = upperAlphabet + lowerAlphabet + numbers + specialChars;

        // Generate random password of length 8
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(allChars.length());
            password.append(allChars.charAt(randomIndex));
        }
        return password.toString();
    }
}
