package org.microarchitecturovisco.offerprovider;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class OfferProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OfferProviderApplication.class, args);
    }

}
