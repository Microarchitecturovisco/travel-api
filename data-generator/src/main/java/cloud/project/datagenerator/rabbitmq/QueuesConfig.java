package cloud.project.datagenerator.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class QueuesConfig {
    public static final String EXCHANGE_HOTEL_FANOUT_UPDATE_DATA = "data.generate.hotels.exchange";

    @Bean
    public FanoutExchange updateHotelDataFanoutExchange() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_UPDATE_DATA);
    }


    public static final String EXCHANGE_TRANSPORT_FANOUT_UPDATE_DATA = "data.generate.transports.exchange";

    @Bean
    public FanoutExchange updateTransportDataFanoutExchange() {
        return new FanoutExchange(EXCHANGE_TRANSPORT_FANOUT_UPDATE_DATA);
    }


    public static final String QUEUE_HOTEL_CREATE_RESERVATION_REQ_PREFIX = "hotels.events.createHotelReservation.queue.";
    public static final String EXCHANGE_HOTEL_FANOUT_CREATE_RESERVATION = "hotels.createReservation.exchange";

    @Bean(name="fanoutExchangeCreateHotelReservation")
    public FanoutExchange fanoutExchangeCreateHotelReservation() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_CREATE_RESERVATION);
    }

    @Bean(name="handleCreateHotelReservationQueue")
    public Queue handleCreateHotelReservationQueue() {
        String uniqueQueueName = QUEUE_HOTEL_CREATE_RESERVATION_REQ_PREFIX + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, true);
    }

    @Bean
    public Binding handleCreateHotelReservationBinding(
            @Qualifier("fanoutExchangeCreateHotelReservation") FanoutExchange fanoutExchangeCreateHotelReservation,
            @Qualifier("handleCreateHotelReservationQueue") Queue handleCreateHotelReservationQueue) {
        return BindingBuilder.bind(handleCreateHotelReservationQueue).to(fanoutExchangeCreateHotelReservation);
    }


    public static final String QUEUE_HOTEL_DELETE_RESERVATION_REQ_PREFIX = "hotels.events.deleteHotelReservation.queue.";
    public static final String EXCHANGE_HOTEL_FANOUT_DELETE_RESERVATION = "hotels.deleteReservation.exchange";

    @Bean(name="fanoutExchangeDeleteHotelReservation")
    public FanoutExchange fanoutExchangeDeleteHotelReservation() {
        return new FanoutExchange(EXCHANGE_HOTEL_FANOUT_DELETE_RESERVATION);
    }

    @Bean(name="handleDeleteHotelReservationQueue")
    public Queue handleDeleteHotelReservationQueue() {
        String uniqueQueueName = QUEUE_HOTEL_DELETE_RESERVATION_REQ_PREFIX + UUID.randomUUID();
        return new Queue(uniqueQueueName, false, false, true);
    }

    @Bean
    public Binding handleDeleteHotelReservationBinding(
            @Qualifier("fanoutExchangeDeleteHotelReservation") FanoutExchange fanoutExchangeDeleteHotelReservation,
            @Qualifier("handleDeleteHotelReservationQueue") Queue handleDeleteHotelReservationQueue) {
        return BindingBuilder.bind(handleDeleteHotelReservationQueue).to(fanoutExchangeDeleteHotelReservation);
    }


}
