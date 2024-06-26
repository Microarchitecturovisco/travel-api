package org.microarchitecturovisco.offerprovider.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.OfferDto;
import org.microarchitecturovisco.offerprovider.domain.requests.GetOfferDetailsRequestDto;
import org.microarchitecturovisco.offerprovider.domain.requests.GetOfferPriceRequestDto;
import org.microarchitecturovisco.offerprovider.domain.responses.GetOfferDetailsResponseDto;
import org.microarchitecturovisco.offerprovider.services.OffersService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

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

        Logger logger = Logger.getLogger("getOffersBasedOnSearchQuery");
        logger.info("Request for arrivals: " + arrivals);

        departureBuses = departureBuses != null ? departureBuses : new ArrayList<>();
        departurePlane = departurePlane != null ? departurePlane : new ArrayList<>();

        List<UUID> departureBusesWithUUIDs = departureBuses.stream().map(UUID::fromString).toList();
        List<UUID> departurePlanesWithUUIDs = departurePlane.stream().map(UUID::fromString).toList();
        List<UUID> arrivalsWithUUIDs = arrivals.stream().map(UUID::fromString).toList();


        List<OfferDto> offerDtos = offersService.getOffersBasedOnSearchQuery(departureBusesWithUUIDs,
                departurePlanesWithUUIDs,
                arrivalsWithUUIDs,
                dateFrom,
                dateTo,
                adults,
                infants,
                kids,
                teens);

        logger.info("Response size: " + offerDtos.size());
        return offerDtos;
    }

    public GetOfferDetailsResponseDto getOfferDetails(GetOfferDetailsRequestDto requestDto) {
        Logger logger = Logger.getLogger("getOfferDetails");
        logger.info("Request for hotel ID: " + requestDto.getIdHotel());

        requestDto.setDepartureBuses(requestDto.getDepartureBuses() != null ? requestDto.getDepartureBuses() : new ArrayList<>());
        requestDto.setDeparturePlanes(requestDto.getDeparturePlanes() != null ? requestDto.getDeparturePlanes() : new ArrayList<>());

        GetOfferDetailsResponseDto responseDto = offersService.getOfferDetails(requestDto);
        logger.info("Response: " + responseDto);

        return responseDto;
    }

    public Float getOfferPrice(GetOfferPriceRequestDto requestDto) {
        return offersService.getOfferPrice(requestDto);
    }
}
