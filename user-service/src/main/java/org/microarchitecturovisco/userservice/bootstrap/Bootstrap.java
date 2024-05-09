package org.microarchitecturovisco.userservice.bootstrap;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.userservice.domain.User;
import org.microarchitecturovisco.userservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        Logger logger = Logger.getLogger("Bootstrap");

        List<User> users = new ArrayList<>();
        
        User user1 = new User("john0@gmail.com", "password1", "John", "Johnson");
        User user2 = new User("emma1@gmail.com", "password2", "Emma", "Washington");
        User user3 = new User("michael2@gmail.com", "password3", "Michael", "Smith");
        User user4 = new User("sophia3@gmail.com", "password4", "Sophia", "Williams");
        User user5 = new User("william4@gmail.com", "password5", "William", "Jones");
        User user6 = new User("olivia5@gmail.com", "password6", "Olivia", "Brown");
        User user7 = new User("james6@gmail.com", "password7", "James", "Davis");
        User user8 = new User("ava7@gmail.com", "password8", "Ava", "Miller");
        User user9 = new User("alexander8@gmail.com", "password9", "Alexander", "Jackson");
        User user10 = new User("isabella9@gmail.com", "password10", "Isabella", "Harris");

        // Save the users
        users.add(userRepository.save(user1));
        users.add(userRepository.save(user2));
        users.add(userRepository.save(user3));
        users.add(userRepository.save(user4));
        users.add(userRepository.save(user5));
        users.add(userRepository.save(user6));
        users.add(userRepository.save(user7));
        users.add(userRepository.save(user8));
        users.add(userRepository.save(user9));
        users.add(userRepository.save(user10));

        // Log the saved users
        logger.info("Saved users: " + users);
    }
}
