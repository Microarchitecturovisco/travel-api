package org.microarchitecturovisco.offerprovider.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.HotelDto;
import org.microarchitecturovisco.offerprovider.domain.dto.LocationDto;
import org.microarchitecturovisco.offerprovider.domain.dto.requests.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.offerprovider.domain.dto.requests.GetTransportsMessage;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportDto;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportsBasedOnSearchQueryResponse;
import org.microarchitecturovisco.offerprovider.domain.exceptions.ServiceTimeoutException;
import org.microarchitecturovisco.offerprovider.domain.exceptions.WrongDateFormatException;
import org.microarchitecturovisco.offerprovider.utils.json.JsonConverter;
import org.microarchitecturovisco.offerprovider.utils.json.JsonReader;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpTimeoutException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OffersService {

    private final RabbitTemplate rabbitTemplate;

    private final DirectExchange transportsExchange;
    private final DirectExchange hotelsExchange;

    public OffersService(RabbitTemplate rabbitTemplate, @Qualifier("getTransportsExchange") DirectExchange transportsExchange, @Qualifier("getHotelsExchange") DirectExchange hotelsExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.transportsExchange = transportsExchange;
        this.hotelsExchange = hotelsExchange;
    }

    public List<TransportDto> getAvailableTransportsBasedOnSearchQuery(
            List<UUID> departureBuses,
            List<UUID> departurePlane,
            List<UUID> arrivals,
            String dateFromString,
            String dateToString,
            Integer adults,
            Integer infants,
            Integer kids,
            Integer teens
    ) {
        Pair<LocalDateTime, LocalDateTime> tripDates = parseDates(dateFromString, dateToString);

        return getFilteredTransportsFromTransportModule(departureBuses, departurePlane, arrivals, tripDates.getFirst(), tripDates.getSecond(), adults, infants, kids, teens);
    }

    private List<HotelDto> getAvailableHotelsBasedOnSearchQuery(
            String dateFromString,
            String dateToString,
            List<Integer> arrivalLocationIds,
            Integer adults,
            Integer infants,
            Integer kids,
            Integer teens
    ) {
        Pair<LocalDateTime, LocalDateTime> tripDates = parseDates(dateFromString, dateToString);

        return getFilteredHotelsFromTransportModule(tripDates.getFirst(), tripDates.getSecond(), arrivalLocationIds, adults, infants, kids, teens);
    }


    private Pair<LocalDateTime, LocalDateTime> parseDates(String dateFromString, String dateToString) {
        LocalDateTime dateFrom;
        LocalDateTime dateTo;
        try {
            dateFrom = LocalDateTime.parse(dateFromString + "T00:00:00");
            dateTo = LocalDateTime.parse(dateToString + "T23:59:59");
        } catch (DateTimeParseException e) {
            throw new WrongDateFormatException();
        }
        return Pair.of(dateFrom, dateTo);
    }

    private List<HotelDto> getFilteredHotelsFromTransportModule(
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            List<Integer> arrivalLocationIds,
            Integer adults,
            Integer infants,
            Integer kids,
            Integer teens
    ) {
        GetHotelsBySearchQueryRequestDto message = GetHotelsBySearchQueryRequestDto.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .arrivalLocationIds(arrivalLocationIds)
                .adults(adults)
                .childrenUnderThree(infants)
                .childrenUnderTen(kids)
                .childrenUnderEighteen(teens)
                .build();

        String messageJson = JsonConverter.convert(message);

        try {
            String responseMessage = (String) rabbitTemplate.convertSendAndReceive(hotelsExchange.getName(), "hotels.requests.getHotelsBySearchQuery", messageJson);

            if(responseMessage != null) {
                GetHotelsBySearchQueryResponseDto response = JsonReader.readHotelsBySearchQueryResponseDtoFromJson(responseMessage);
                return response.getHotels();
            }
            else {
                throw new ServiceTimeoutException();
            }


        } catch (AmqpTimeoutException e) {
            throw new ServiceTimeoutException();
        }
    }

    private List<TransportDto> getFilteredTransportsFromTransportModule(
            List<UUID> departureBuses,
            List<UUID> departurePlane,
            List<UUID> arrivals,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Integer adults,
            Integer infants,
            Integer kids,
            Integer teens
    ) {
        String correlationId = java.util.UUID.randomUUID().toString();

        GetTransportsMessage transportsMessage = GetTransportsMessage.builder()
                                                .uuid(correlationId)
                                                .departureLocationIdsByBus(departureBuses)
                                                .departureLocationIdsByPlane(departurePlane)
                                                .arrivalLocationIds(arrivals)
                                                .dateFrom(dateFrom)
                                                .dateTo(dateTo)
                                                .adults(adults)
                                                .childrenUnderThree(infants)
                                                .childrenUnderTen(kids)
                                                .childrenUnderEighteen(teens)
                                                .build();

        String transportMessageJson = JsonConverter.convertGetTransportsMessage(transportsMessage);

        try {
            String responseMessage = (String) rabbitTemplate.convertSendAndReceive(transportsExchange.getName(), "transports.handleTransportsBySearchQuery", transportMessageJson);

            if(responseMessage != null) {
                TransportsBasedOnSearchQueryResponse transportDtoResponse = JsonReader.readTransportsBasedOnSearchQueryResponseFromJson(responseMessage);
                return transportDtoResponse.getTransportDtoList();
            }
            else {
                throw new ServiceTimeoutException();
            }

        } catch (AmqpException e) {
            throw new ServiceTimeoutException();
        }

    }
}
