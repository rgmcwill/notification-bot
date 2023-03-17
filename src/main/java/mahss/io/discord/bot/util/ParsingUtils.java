package mahss.io.discord.bot.util;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ParsingUtils {
    public static LocalDateTime getExpireFromStartAndEta(LocalDateTime startTime, String durationString) {
        if (!Character.isDigit(durationString.charAt(0))) {
            return startTime.plusWeeks(1);
        }
        String[] parts = durationString.split(" ");
        int days = 0, hours = 0, minutes = 0, seconds = 0;
        for (String part : parts) {
            int amount = Integer.parseInt(part.substring(0, part.length() - 1));
            if (part.endsWith("d")) {
                days = amount;
            } else if (part.endsWith("h")) {
                hours = amount;
            } else if (part.endsWith("m")) {
                minutes = amount;
            } else if (part.endsWith("s")) {
                seconds = amount;
            }
        }
        return startTime.plusDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    public static String enumToDisplayText(final String enumString) {
        String lowerCaseSubType = enumString.toLowerCase();
        return lowerCaseSubType.substring(0, 1).toUpperCase() + lowerCaseSubType.substring(1);
    }


    public static LocalDateTime stringToLocalDateTime(final String expiry) {
        return Instant.parse(expiry).atZone(ZoneId.of("America/Chicago")).toLocalDateTime();
    }

    public static String missionTypeToType(final String missionType) {
        return missionType.toUpperCase(Locale.ROOT)
                .replace(" ", "_")
                .replace("(ARCHWING)", "ARCHWING");
    }

    public static ArrayList<String> nodeToNodeAndCelestialBody(final String node) {
        String cBody = StringUtils.substringBetween(node, "(", ")");
        String newNode = node.replaceAll("\\(.*\\)", "").trim();
        return new ArrayList<>(Arrays.asList(cBody, newNode));
    }
}
