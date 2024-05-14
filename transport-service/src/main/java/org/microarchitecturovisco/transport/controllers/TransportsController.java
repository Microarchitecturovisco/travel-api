package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.services.TransportsQueryService;
import org.microarchitecturovisco.transport.utils.json.JsonConverter;
import org.microarchitecturovisco.transport.utils.json.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportsController {

    private final TransportsQueryService transportsQueryService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/available")
    public AvailableTransportsDto getAvailableTransports() {
        return transportsQueryService.getAvailableTransports();
    }

    @GetMapping("/")
    public String test() {
        return "test";
    }

    @RabbitListener(queues = "transports.requests.getTransportsBySearchQuery")
    public void consumeGetTransportsRequest(String requestDtoJson) {
        long startTime = System.currentTimeMillis();

        GetTransportsBySearchQueryRequestDto requestDto = JsonReader.readGetTransportsBySearchQueryRequestFromJson(requestDtoJson);

        GetTransportsBySearchQueryResponseDto responseDto = transportsQueryService.getTransportsBySearchQuery(requestDto);

        long endTime = System.currentTimeMillis();
        System.out.println("Send transports response size " + responseDto.getTransportDtoList().size());
        System.out.println("Service call took " + (endTime - startTime) + " ms");

        rabbitTemplate.convertAndSend("transports.responses.getTransportsBySearchQuery", JsonConverter.convertGetTransportsBySearchQueryResponseDto(responseDto));
    }
}
