package cloud.project.datagenerator.hotels.rabbitmq;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueuesConfig {
    public static final String EXCHANGE_HOTEL_FANOUT_UPDATE_DATA = "data.generate.hotels.exchange";

    @Bean
    public FanoutExchange updateHotelDataFanoutExchange() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_UPDATE_DATA);
    }


}
