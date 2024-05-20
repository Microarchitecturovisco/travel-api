package org.microarchitecturovisco.userservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.userservice.domain.User;
import org.microarchitecturovisco.userservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;

    @Override
    public void run(String... args) {
        Logger logger = Logger.getLogger("Bootstrap | User");

        List<User> users = importUsersFromCSV(resourceLoader.getResource("initData/users.csv"));

        userRepository.saveAll(users);

        logger.info("Saved " + users.size() + " users");
    }

    private File loadCSVInitFile(String filePath) {
        try {
            return new ClassPathResource(filePath).getFile();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load CSV file: " + filePath, e);
        }
    }

    private List<User> importUsersFromCSV(Resource resource) {
        List<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String email = data[0];
                String password = data[1];
                String firstName = data[2];
                String lastName = data[3];
                UUID userId = UUID.nameUUIDFromBytes((email + password + firstName + lastName).getBytes());

                User user = new User(userId, email, password, firstName, lastName);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }
}
