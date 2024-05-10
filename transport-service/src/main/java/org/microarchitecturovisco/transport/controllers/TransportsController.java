package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.services.TransportsService;
import org.microarchitecturovisco.transport.utils.json.JsonConverter;
import org.microarchitecturovisco.transport.utils.json.JsonReader;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportsController {

    private final TransportsService transportsService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/available")
    public AvailableTransportsDto getAvailableTransports() {
        return transportsService.getAvailableTransports();
    }

    @GetMapping("/")
    public String test() {
        return "test";
    }

    @RabbitListener(queues = "transports.requests.getTransportsBySearchQuery")
    @RabbitHandler
    public void consumeGetTransportsRequest(String requestDtoJson) {
        GetTransportsBySearchQueryRequestDto requestDto = JsonReader.readGetTransportsBySearchQueryRequestFromJson(requestDtoJson);
        GetTransportsBySearchQueryResponseDto responseDto = transportsService.getTransportsBySearchQuery(requestDto);

        System.out.println("Send transports response size " + responseDto.getTransportDtoList().size());

        rabbitTemplate.convertAndSend("transports.responses.getTransportsBySearchQuery", JsonConverter.convertGetTransportsBySearchQueryResponseDto(responseDto));
    }
}
