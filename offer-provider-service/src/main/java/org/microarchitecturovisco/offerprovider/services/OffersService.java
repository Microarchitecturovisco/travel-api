package org.microarchitecturovisco.offerprovider.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.requests.GetTransportsMessage;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportDto;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportsBasedOnSearchQueryResponse;
import org.microarchitecturovisco.offerprovider.domain.exceptions.ServiceTimeoutException;
import org.microarchitecturovisco.offerprovider.domain.exceptions.WrongDateFormatException;
import org.microarchitecturovisco.offerprovider.utils.json.JsonConverter;
import org.microarchitecturovisco.offerprovider.utils.json.JsonReader;
import org.springframework.amqp.AmqpTimeoutException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OffersService {

    private final RabbitTemplate rabbitTemplate;

    public List<TransportDto> getAvailableTransportsBasedOnSearchQuery(
            List<Integer> departureBuses,
            List<Integer> departurePlane,
            List<Integer> arrivals,
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

    private List<TransportDto> getFilteredTransportsFromTransportModule(
            List<Integer> departureBuses,
            List<Integer> departurePlane,
            List<Integer> arrivals,
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


        rabbitTemplate.convertAndSend("transports.requests.getTransportsBySearchQuery", transportMessageJson);

        try {
            String responseMessage = (String) rabbitTemplate.receiveAndConvert("transports.responses.getTransportsBySearchQuery", 5000);

            if(responseMessage != null) {
                TransportsBasedOnSearchQueryResponse transportDtoResponse = JsonReader.readTransportsBasedOnSearchQueryResponseFromJson(responseMessage);
                return transportDtoResponse.getTransportDtoList();
            }
            else {
                throw new ServiceTimeoutException();
            }


        } catch (AmqpTimeoutException e) {
            throw new ServiceTimeoutException();
        }

    }
}
