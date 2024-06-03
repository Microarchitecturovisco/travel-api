package cloud.project.datagenerator.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelsDataGenerator {

    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void generateHotelsData() {
        System.out.println("Generating Hotel Data...");

        read_hotels_data();

    }

    private void read_hotels_data() {
        System.out.println("Reading Hotel Data...");


    }
}

