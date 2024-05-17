package org.microarchitecturovisco.hotelservice.model.mappers;

import org.microarchitecturovisco.hotelservice.model.domain.Hotel;
import org.microarchitecturovisco.hotelservice.model.domain.Room;
import org.microarchitecturovisco.hotelservice.model.dto.HotelResponseDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomDto;
import org.microarchitecturovisco.hotelservice.model.dto.RoomResponseDto;

import java.util.ArrayList;
import java.util.List;

public class RoomMapper {
    public static RoomResponseDto map(Room room) {
        return RoomResponseDto.builder()
                .roomId(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .guestCapacity(room.getGuestCapacity())
                .build();
    }

    public static List<RoomResponseDto> mapList(List<Room> rooms) {
        List<RoomResponseDto> roomDtos = new ArrayList<>();
        for (int i = 0; i < rooms.size(); i++) {
            roomDtos.add(map(rooms.get(i)));
        }
        return roomDtos;
    }
}
