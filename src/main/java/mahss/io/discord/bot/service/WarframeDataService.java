package mahss.io.discord.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class WarframeDataService implements DataService {

    private final String warframeUrl;

    private ObjectMapper objectMapper;
    private JsonNode rawData;

    private LocalDateTime lastCheck;

    public WarframeDataService(final String warframeUrl) {
        this.warframeUrl = warframeUrl;
        this.objectMapper = new ObjectMapper();
        this.rawData = objectMapper.createObjectNode();
        this.lastCheck = LocalDateTime.MIN;
    }

    @Override
    public JsonNode getData() {
        LocalDateTime now = LocalDateTime.now();
        if ((now.getHour() != lastCheck.getHour() || now.getDayOfMonth() != lastCheck.getDayOfMonth() || now.getMonth() != lastCheck.getMonth() || now.getYear() != lastCheck.getYear()) ||
                now.isAfter(getNextHalfHour(lastCheck)) ||
                now.isAfter(getNextQuarterHour(lastCheck)) ||
                now.isAfter(getNextThreeQuarterHour(lastCheck)))
            try {
                URL url = new URL(warframeUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("accept", "application/json");
                InputStream responseStream = connection.getInputStream();
                rawData = objectMapper.readTree(responseStream);
                lastCheck = now;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return rawData;
    }

    private LocalDateTime getNextHalfHour(final LocalDateTime originalTime) {
        if (originalTime.getMinute() < 30) {
            return originalTime.withMinute(30);
        } else {
            return originalTime.withMinute(30).withHour(originalTime.getHour() + 1 >= 24 ? 0 : originalTime.getHour() + 1);
        }
    }

    private LocalDateTime getNextQuarterHour(final LocalDateTime originalTime) {
        if (originalTime.getMinute() < 15) {
            return originalTime.withMinute(15);
        } else {
            return originalTime.withMinute(15).withHour(originalTime.getHour() + 1 >= 24 ? 0 : originalTime.getHour() + 1);
        }
    }

    private LocalDateTime getNextThreeQuarterHour(final LocalDateTime originalTime) {
        if (originalTime.getMinute() < 45) {
            return originalTime.withMinute(45);
        } else {
            return originalTime.withMinute(45).withHour(originalTime.getHour() + 1 >= 24 ? 0 : originalTime.getHour() + 1);
        }
    }
}
