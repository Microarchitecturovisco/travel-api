package org.microarchitecturovisco.paymentservice.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {

    @Bean
    public Queue handlePaymentQueue() {
        return new Queue("payments.requests.handle", false);
    }

    @Bean
    public DirectExchange handlePaymentExchange() {
        return new DirectExchange("payments.requests.handle");
    }

    @Bean
    public Binding handlePaymentRequestBinding(DirectExchange handlePaymentExchange, Queue handlePaymentQueue) {
        return BindingBuilder.bind(handlePaymentQueue).to(handlePaymentExchange).with("payments.handlePayment");
    }
}
