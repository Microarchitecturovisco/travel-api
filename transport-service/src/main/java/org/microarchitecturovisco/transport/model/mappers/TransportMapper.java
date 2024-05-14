package org.microarchitecturovisco.transport.model.mappers;

import org.microarchitecturovisco.transport.model.domain.Transport;
import org.microarchitecturovisco.transport.model.dto.TransportDto;

import java.util.List;

public class TransportMapper {
    public static TransportDto map(Transport transport) {
        return TransportDto.builder()
                .idTransport(transport.getId())
                .transportCourse(TransportCourseMapper.map(transport.getCourse()))
                .departureDate(transport.getDepartureDate())
                .pricePerAdult(transport.getPricePerAdult())
                .capacity(transport.getCapacity())
                .build();
    }

    public static List<TransportDto> mapList(List<Transport> transports) {
        return transports.stream().map(TransportMapper::map).toList();
    }

    public static Transport map(TransportDto dto) {
        return Transport.builder()
                .id(dto.getIdTransport())
                .course(TransportCourseMapper.map(dto.getTransportCourse()))
                .departureDate(dto.getDepartureDate())
                .capacity(dto.getCapacity())
                .pricePerAdult(dto.getPricePerAdult())
                .build();
    }
}
