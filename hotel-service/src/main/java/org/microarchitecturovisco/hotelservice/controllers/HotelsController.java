package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.model.dto.request.GetHotelsBySearchQueryRequestDto;
import org.microarchitecturovisco.hotelservice.model.dto.response.GetHotelsBySearchQueryResponseDto;
import org.microarchitecturovisco.hotelservice.services.HotelsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelsController {

    private final HotelsService hotelsService;
    private final RabbitTemplate rabbitTemplate;


    @RabbitListener(queues = "transports.requests.getTransportsBySearchQuery")
    public void consumeGetTransportsRequest(GetHotelsBySearchQueryRequestDto requestDto) {
        GetHotelsBySearchQueryResponseDto responseDto = hotelsService.GetHotelsBySearchQuery(requestDto);

        System.out.println("Send hotels response size " + responseDto.getHotelsDtoList().size());

        rabbitTemplate.convertAndSend("hotels.responses.getHotelsBySearchQuery", responseDto);
    }
}

