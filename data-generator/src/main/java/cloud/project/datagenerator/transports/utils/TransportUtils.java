package cloud.project.datagenerator.transports.utils;

import cloud.project.datagenerator.transports.domain.Transport;
import cloud.project.datagenerator.transports.domain.TransportCourse;
import cloud.project.datagenerator.transports.repositories.TransportCourseRepository;
import cloud.project.datagenerator.transports.repositories.TransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class TransportUtils {

    private final Random random = new Random();
    private final TransportRepository transportRepository;
    private final TransportCourseRepository transportCourseRepository;
    Logger logger = Logger.getLogger("DataGenerator | Transports");


    public TransportCourse getRandomTransportCourse() {
        List<TransportCourse> transportCourses = transportCourseRepository.findAll();

        if (transportCourses.isEmpty()) {
            logger.info("No transport courses found.");
            return null;
        }

        return transportCourses.get(random.nextInt(10));
    }

    public Transport getRandomTransport() {
        List<Transport> transports = transportRepository.findAll();

        if (transports.isEmpty()) {
            logger.info("No transports found.");
            return null;
        }

        return transports.get(random.nextInt(transports.size()));
    }

}
