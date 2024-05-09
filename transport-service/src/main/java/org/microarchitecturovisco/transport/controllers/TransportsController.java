package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.dto.request.GetTransportsBySearchQueryRequestDto;
import org.microarchitecturovisco.transport.model.dto.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.model.dto.response.GetTransportsBySearchQueryResponseDto;
import org.microarchitecturovisco.transport.services.TransportsService;
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
    public void consumeGetTransportsRequest(GetTransportsBySearchQueryRequestDto requestDto) {
        GetTransportsBySearchQueryResponseDto responseDto = transportsService.getTransportsBySearchQuery(requestDto);

        rabbitTemplate.convertAndSend("transports.responses.getTransportsBySearchQuery", responseDto);
    }
}
