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
    public Queue handlePaymentRequest() {
        return new Queue("payments.requests.handle", false);
    }

    @Bean
    public DirectExchange handlePaymentResponseExchange() {
        return new DirectExchange("payments.responses.handle");
    }

    @Bean
    public Binding handlePaymentRequestBinding(DirectExchange handlePaymentResponseExchange, Queue handlePaymentRequest) {
        return BindingBuilder.bind(handlePaymentRequest).to(handlePaymentResponseExchange).with("payments.handlePayment");
    }
}
