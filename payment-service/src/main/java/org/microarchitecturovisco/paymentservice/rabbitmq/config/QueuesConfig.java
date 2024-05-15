package org.microarchitecturovisco.paymentservice.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    @Bean
    public Queue handlePayment() {
        return new Queue("payments.requests.handle", false);
    }
}
