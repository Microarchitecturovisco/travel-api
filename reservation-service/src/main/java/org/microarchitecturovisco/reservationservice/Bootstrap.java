package org.microarchitecturovisco.reservationservice;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Bootstrap implements CommandLineRunner {

    @Override
    public void run(String... args) {
        // Your initialization logic here
    }
}
