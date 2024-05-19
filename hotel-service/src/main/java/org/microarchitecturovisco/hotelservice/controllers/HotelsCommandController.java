package org.microarchitecturovisco.hotelservice.controllers;

import lombok.RequiredArgsConstructor;
import org.microarchitecturovisco.hotelservice.services.HotelsCommandService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelsCommandController {
    private final HotelsCommandService hotelsCommandService;

}
