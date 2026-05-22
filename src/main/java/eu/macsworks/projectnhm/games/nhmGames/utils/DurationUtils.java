package eu.macsworks.projectnhm.games.nhmGames.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class DurationUtils {

    public String format(long millis) {
        return format(Duration.ofMillis(millis));
    }

    public String format(Duration duration) {
        long days = duration.toDays();
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder(48);
        if (days > 0) sb.append(String.format("%02d days", days));
        if (hours > 0) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(String.format("%02d hours", hours));
        }

        if (minutes > 0) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(String.format("%02d minutes", minutes));
        }

        if (!sb.isEmpty()) sb.append(", ");
        sb.append(String.format("%02d seconds", seconds));

        return sb.toString();
    }

}