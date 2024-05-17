package org.microarchitecturovisco.offerprovider.services;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.offerprovider.domain.dto.HotelDto;
import org.microarchitecturovisco.offerprovider.domain.dto.LocationDto;
import org.microarchitecturovisco.offerprovider.domain.dto.OfferDto;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

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

    public List<OfferDto> getOffersBasedOnSearchQuery(List<UUID> departureBuses,
                                                      List<UUID> departurePlane,
                                                      List<UUID> arrivals,
                                                      String dateFromString,
                                                      String dateToString,
                                                      Integer adults,
                                                      Integer infants,
                                                      Integer kids,
                                                      Integer teens) {
        List<List<TransportDto>> availableTransports = getAvailableTransportsBasedOnSearchQuery(
                departureBuses,
                departurePlane,
                arrivals,
                dateFromString,
                dateToString,
                adults,
                infants,
                kids,
                teens);

        System.out.println("Available transports: " + availableTransports);

        List<HotelDto> availableHotels = getAvailableHotelsBasedOnSearchQuery(
                dateFromString,
                dateToString,
                arrivals,
                adults,
                infants,
                kids,
                teens);

        System.out.println("Available hotels: " + availableHotels);

        Pair<LocalDateTime, LocalDateTime> tripDates = parseDates(dateFromString, dateToString);

        List<OfferDto> offers = new ArrayList<>();

        availableHotels.forEach(hotel -> {
            UUID locationId = hotel.getLocation().getIdLocation();
            for(List<TransportDto> pair : availableTransports) {
                if(Objects.equals(pair.getFirst().getTransportCourse().getArrivalAtLocation().getIdLocation(), locationId)) {
                    long numberOfTripDays = ChronoUnit.DAYS.between(tripDates.getFirst(), tripDates.getSecond());
                    long numberOfDaysToTrip = ChronoUnit.DAYS.between(tripDates.getFirst(), LocalDateTime.now());
                    float priceForTransport = pair.getFirst().getPricePerAdult() + pair.getLast().getPricePerAdult();
                    float price = calculatePrice((int) numberOfTripDays, adults, infants, kids, teens, hotel.getPricePerAdult(),
                            0, (int) numberOfDaysToTrip, priceForTransport);

                    OfferDto offer = OfferDto.builder()
                            .idHotel(hotel.getHotelId().toString())
                            .hotelName(hotel.getName())
                            .destination(hotel.getLocation().getRegion() + ", " + hotel.getLocation().getCountry())
                            .rating(hotel.getRating())
                            .imageUrl(hotel.getPhotos().getFirst())
                            .price(price)
                            .build();
                    offers.add(offer);

                    break;
                }
            }
        });

        return offers;
    }

    public List<List<TransportDto>> getAvailableTransportsBasedOnSearchQuery(
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

    public Float calculatePrice(int numberOfDays, int adults, int infants, int kids, int teens,
                                float pricePerAdultPerRoomPerDay, float cateringPrice, int daysToTripStartsFromToday,
                                float transportPricePerAdult) {
        final float INFANT_DISCOUNT_FACTOR = 0.1f;
        final float KID_DISCOUNT_FACTOR = 0.6f;
        final float TEEN_DISCOUNT_FACTOR = 0.7f;
        final float LAST_MINUTE_DISCOUNT_FACTOR = 0.7f;
        final float HIGH_PRICE_FACTOR = 1.2f;

        float adultHotelPrice = (pricePerAdultPerRoomPerDay + cateringPrice);

        float priceForAdultsPerDayPerRoom = adults * adultHotelPrice;
        float priceForInfantPerDayPerRoom = infants * adultHotelPrice * INFANT_DISCOUNT_FACTOR;
        float priceForKidPerDayPerRoom = kids * adultHotelPrice * KID_DISCOUNT_FACTOR;
        float priceForTeenPerDayPerRoom = teens * adultHotelPrice * TEEN_DISCOUNT_FACTOR;


        float fullHotelPrice = numberOfDays * (priceForAdultsPerDayPerRoom + priceForInfantPerDayPerRoom +
                priceForKidPerDayPerRoom + priceForTeenPerDayPerRoom);

        float transportPrice = transportPricePerAdult * (adults + teens + kids);

        float basePrice = fullHotelPrice + transportPrice;

        return daysToTripStartsFromToday <= 3 ? basePrice * LAST_MINUTE_DISCOUNT_FACTOR :
                (daysToTripStartsFromToday <= 30 ? basePrice * HIGH_PRICE_FACTOR : basePrice);
    }

    private List<HotelDto> getAvailableHotelsBasedOnSearchQuery(
            String dateFromString,
            String dateToString,
            List<UUID> arrivalLocationIds,
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
            List<UUID> arrivalLocationIds,
            Integer adults,
            Integer infants,
            Integer kids,
            Integer teens
    ) {
        GetHotelsBySearchQueryRequestDto message = GetHotelsBySearchQueryRequestDto.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .arrivalLocationIds(arrivalLocationIds.stream().map(UUID::toString).toList())
                .adults(adults)
                .childrenUnderThree(infants)
                .childrenUnderTen(kids)
                .childrenUnderEighteen(teens)
                .build();

        String messageJson = JsonConverter.convert(message);

        try {
//            String responseMessage = (String) rabbitTemplate.convertSendAndReceive(hotelsExchange.getName(), "hotels.requests.hotelsBySearchQuery", messageJson);
            byte[] responseMessageB = (byte[]) rabbitTemplate.convertSendAndReceive(hotelsExchange.getName(), "hotels.requests.hotelsBySearchQuery", messageJson);

            String responseMessage = (new String(responseMessageB)).replace("\\", "");
            responseMessage = responseMessage.substring(1, responseMessage.length() - 1);
            System.out.println(responseMessage);

            if(responseMessage != null) {
                System.out.println("Nonull message: " + responseMessage);
                GetHotelsBySearchQueryResponseDto response = JsonReader.readHotelsBySearchQueryResponseDtoFromJson(responseMessage);
                return response.getHotels();
            }
            else {
                System.out.println("Null message at: getFilteredHotelsFromTransportModule()");
                throw new ServiceTimeoutException();
            }


        } catch (AmqpTimeoutException e) {
            e.printStackTrace();
            throw new ServiceTimeoutException();
        }
    }

    private List<List<TransportDto>> getFilteredTransportsFromTransportModule(
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

//        GetTransportsMessage transportsMessage = GetTransportsMessage.builder()
//                                                .uuid(correlationId)
//                                                .departureLocationIdsByBus(departureBuses)
//                                                .departureLocationIdsByPlane(departurePlane)
//                                                .arrivalLocationIds(arrivals)
//                                                .dateFrom(dateFrom)
//                                                .dateTo(dateTo)
//                                                .adults(adults)
//                                                .childrenUnderThree(infants)
//                                                .childrenUnderTen(kids)
//                                                .childrenUnderEighteen(teens)
//                                                .build();
        GetTransportsMessage transportsMessage = GetTransportsMessage.builder()
                                                .uuid(correlationId)
                                                .departureLocationIds(Stream.concat(departureBuses.stream(), departurePlane.stream()).toList())
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
            String responseMessage = (String) rabbitTemplate.convertSendAndReceive("transports.requests.getTransportsBetweenMultipleLocations", "transports.getTransportsBetweenMultipleLocations", transportMessageJson);

            if(responseMessage != null) {

                System.out.println("Transports message: " + responseMessage);

                TransportsBasedOnSearchQueryResponse transportDtoResponse = JsonReader.readTransportsBasedOnSearchQueryResponseFromJson(responseMessage);
                return transportDtoResponse.getTransportPairs();
            }
            else {
                System.out.println("Null message at: getFilteredTransportsFromTransportModule()");
                throw new ServiceTimeoutException();
            }

        } catch (AmqpException e) {
            e.printStackTrace();
            throw new ServiceTimeoutException();
        }

    }
}
