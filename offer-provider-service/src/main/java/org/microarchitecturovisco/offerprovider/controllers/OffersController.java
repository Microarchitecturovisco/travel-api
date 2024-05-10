package org.microarchitecturovisco.offerprovider.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.OfferDto;
import org.microarchitecturovisco.offerprovider.domain.dto.requests.GetTransportsMessage;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportDto;
import org.microarchitecturovisco.offerprovider.services.OffersService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OffersController {

    private final OffersService offersService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/offers")
    public List<OfferDto> getOffersBasedOnSearchQuery(
            @RequestParam(name = "departureBus") List<Integer> departureBuses,
            @RequestParam(name = "departurePlane") List<Integer> departurePlane,
            @RequestParam(name = "arrivals") List<Integer> arrivals,
            @RequestParam(name = "date_from") String dateFrom,
            @RequestParam(name = "date_to") String dateTo,
            @RequestParam(name = "adults") Integer adults,
            @RequestParam(name = "infants") Integer infants,
            @RequestParam(name = "kids") Integer kids,
            @RequestParam(name = "teens") Integer teens

    ) {

        return offersService.getOffersBasedOnSearchQuery(departureBuses, departurePlane,
                arrivals, dateFrom, dateTo, adults, infants, kids, teens);
    }

    @RabbitListener(queues = "transports.responses.getTransportsBySearchQuery")
    public void consumeGetTransportsRequest(TransportDto transportDto) {


    }
}
