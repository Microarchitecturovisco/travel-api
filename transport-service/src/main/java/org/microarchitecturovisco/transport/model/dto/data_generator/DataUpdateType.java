package org.microarchitecturovisco.transport.model.dto.data_generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataUpdateType {
    CREATE("CREATE"),
    UPDATE("UPDATE");

    private final String value;
}

