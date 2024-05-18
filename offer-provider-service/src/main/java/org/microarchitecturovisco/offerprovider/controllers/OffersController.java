package org.microarchitecturovisco.offerprovider.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.OfferDto;
import org.microarchitecturovisco.offerprovider.domain.dto.responses.TransportDto;
import org.microarchitecturovisco.offerprovider.domain.responses.GetOfferDetailsResponseDto;
import org.microarchitecturovisco.offerprovider.services.OffersService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offers")
public class OffersController {

    private final OffersService offersService;

    @GetMapping("/")
    public List<OfferDto> getOffersBasedOnSearchQuery(
            @RequestParam(name = "departureBus", required = false) List<String> departureBuses,
            @RequestParam(name = "departurePlane", required = false) List<String> departurePlane,
            @RequestParam(name = "arrivals") List<String> arrivals,
            @RequestParam(name = "date_from") String dateFrom,
            @RequestParam(name = "date_to") String dateTo,
            @RequestParam(name = "adults") Integer adults,
            @RequestParam(name = "infants") Integer infants,
            @RequestParam(name = "kids") Integer kids,
            @RequestParam(name = "teens") Integer teens

    ) {

        departureBuses = departureBuses != null ? departureBuses : new ArrayList<>();
        departurePlane = departurePlane != null ? departurePlane : new ArrayList<>();

        List<UUID> departureBusesWithUUIDs = departureBuses.stream().map(UUID::fromString).toList();
        List<UUID> departurePlanesWithUUIDs = departurePlane.stream().map(UUID::fromString).toList();
        List<UUID> arrivalsWithUUIDs = arrivals.stream().map(UUID::fromString).toList();


        return offersService.getOffersBasedOnSearchQuery(departureBusesWithUUIDs,
                departurePlanesWithUUIDs,
                arrivalsWithUUIDs,
                dateFrom,
                dateTo,
                adults,
                infants,
                kids,
                teens);
    }

    @GetMapping("/{idHotel}")
    public GetOfferDetailsResponseDto getOfferDetails(
            @PathVariable UUID idHotel,
            @RequestParam(name = "date_from") String dateFrom,
            @RequestParam(name = "date_to") String dateTo,
            @RequestParam(name = "departure_buses", required = false) List<UUID> departureBuses,
            @RequestParam(name = "departure_planes", required = false) List<UUID> departurePlanes,
            @RequestParam(name = "adults") Integer adults,
            @RequestParam(name = "infants") Integer infants,
            @RequestParam(name = "kids") Integer kids,
            @RequestParam(name = "teens") Integer teens
    ) {
        departureBuses = departureBuses != null ? departureBuses : new ArrayList<>();
        departurePlanes = departurePlanes != null ? departurePlanes : new ArrayList<>();

        return offersService.getOfferDetails(
                idHotel, dateFrom, dateTo, departureBuses, departurePlanes, adults, infants, kids, teens
        );
    }
}
