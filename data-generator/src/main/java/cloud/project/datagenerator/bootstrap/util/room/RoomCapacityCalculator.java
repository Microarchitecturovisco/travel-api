package cloud.project.datagenerator.bootstrap.util.room;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomCapacityCalculator {

    public int calculateGuestCapacity(String roomName) {
        int basicCapacity = extractBasicCapacity(roomName);

        if (roomName.contains("dostawka")) {
            basicCapacity++;
        }

        if (roomName.contains("dostawki")) {
            basicCapacity += 2;
        }

        return basicCapacity;
    }

    private int extractBasicCapacity(String roomName) {
        int basicCapacity = 0;

        String regex = "\\d+(?= os\\.)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(roomName);

        if (matcher.find()) {
            basicCapacity = Integer.parseInt(matcher.group());
        }

        return basicCapacity;
    }
}