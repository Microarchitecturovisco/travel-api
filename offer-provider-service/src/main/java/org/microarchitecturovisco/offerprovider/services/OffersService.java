package org.microarchitecturovisco.offerprovider.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.OfferDto;
import org.microarchitecturovisco.offerprovider.domain.dto.requests.GetTransportsMessage;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportDto;
import org.microarchitecturovisco.offerprovider.domain.exceptions.WrongDateFormatException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OffersService {

    private final RabbitTemplate rabbitTemplate;

    public List<OfferDto> getOffersBasedOnSearchQuery(
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

        return List.of();
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

    private void getFilteredTransportsFromTransportModule(
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
        rabbitTemplate.convertAndSend("transports.responses.getTransportsBySearchQuery",
                GetTransportsMessage.builder()
                        .departureLocationIdsByBus(departureBuses)
                        .departureLocationIdsByPlane(departurePlane)
                        .arrivalLocationIds(arrivals)
                        .dateFrom(dateFrom)
                        .dateTo(dateTo)
                        .adults(adults)
                        .childrenUnderThree(infants)
                        .childrenUnderTen(kids)
                        .childrenUnderEighteen(teens)
                        .build()
        );
    }
}
