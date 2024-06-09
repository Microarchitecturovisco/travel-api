package cloud.project.datagenerator.rabbitmq.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataUpdateType {
    CREATE("CREATE"),
    UPDATE("UPDATE");

    private final String value;
}
