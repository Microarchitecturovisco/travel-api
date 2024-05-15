package org.microarchitecturovisco.offerprovider.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.OfferDto;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportDto;
import org.microarchitecturovisco.offerprovider.services.OffersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offers")
public class OffersController {

    private final OffersService offersService;

    @GetMapping("/transports")
    public List<OfferDto> getOffersBasedOnSearchQuery(
            @RequestParam(name = "departureBus") List<String> departureBuses,
            @RequestParam(name = "departurePlane") List<String> departurePlane,
            @RequestParam(name = "arrivals") List<String> arrivals,
            @RequestParam(name = "date_from") String dateFrom,
            @RequestParam(name = "date_to") String dateTo,
            @RequestParam(name = "adults") Integer adults,
            @RequestParam(name = "infants") Integer infants,
            @RequestParam(name = "kids") Integer kids,
            @RequestParam(name = "teens") Integer teens

    ) {

        List<UUID> bds = departureBuses.stream().map(UUID::fromString).toList();
        List<UUID> bps = departureBuses.stream().map(UUID::fromString).toList();
        List<UUID> ars = departureBuses.stream().map(UUID::fromString).toList();


        offersService.getAvailableTransportsBasedOnSearchQuery(
                bds,
                bps,
                ars,
                dateFrom,
                dateTo,
                adults,
                infants,
                kids,
                teens);
        return List.of();
    }
}
