package org.microarchitecturovisco.transport.model.mappers;

import org.microarchitecturovisco.transport.model.domain.TransportCourse;
import org.microarchitecturovisco.transport.model.dto.TransportCourseDto;

public class TransportCourseMapper {
    public static TransportCourseDto map(TransportCourse transportCourse) {
        return TransportCourseDto.builder()
                .idTransportCourse(transportCourse.getId())
                .type(transportCourse.getType())
                .arrivalAtLocation(LocationMapper.map(transportCourse.getArrivalAt()))
                .departureFromLocation(LocationMapper.map(transportCourse.getDepartureFrom()))
                .build();
    }

    public static TransportCourse map(TransportCourseDto dto) {
        return TransportCourse.builder()
                .id(dto.getIdTransportCourse())
                .type(dto.getType())
                .arrivalAt(LocationMapper.map(dto.getArrivalAtLocation()))
                .departureFrom(LocationMapper.map(dto.getDepartureFromLocation()))
                .build();
    }
}
