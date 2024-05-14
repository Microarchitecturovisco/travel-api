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
}
