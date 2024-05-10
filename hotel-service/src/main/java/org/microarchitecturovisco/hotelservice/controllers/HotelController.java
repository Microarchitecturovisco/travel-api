package org.microarchitecturovisco.hotelservice.controllers;

import org.microarchitecturovisco.hotelservice.domain.Hotel;
import org.microarchitecturovisco.hotelservice.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable int id) {
        return hotelService.getHotelById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id " + id));
    }

    @PostMapping
    public Hotel createHotel(@RequestBody Hotel hotel) {
        return hotelService.saveHotel(hotel);
    }

    @DeleteMapping("/{id}")
    public void deleteHotel(@PathVariable int id) {
        hotelService.deleteHotel(id);
    }
}
