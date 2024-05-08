package org.microarchitecturovisco.transport.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.transport.model.dto.transports.response.AvailableTransportsDto;
import org.microarchitecturovisco.transport.services.TransportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportsController {

    private final TransportsService transportsService;

    @GetMapping("/available")
    public AvailableTransportsDto getAvailableTransports() {
        return transportsService.getAvailableTransports();
    }

    @GetMapping("/")
    public String test() {
        return "test";
    }

}
