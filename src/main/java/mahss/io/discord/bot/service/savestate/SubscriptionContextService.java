package mahss.io.discord.bot.service.savestate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionContextService {
    private static File file;

    private Map<String, Map<String, Map<String, Collection>>> data;

    public SubscriptionContextService(final String path) {
        this.file = Paths.get(path).toFile();
        this.data = new HashMap<>();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Map<String, Map<String, Collection>>> getData() {
        return data;
    }

    public Map<String, Map<String, Map<String, Collection>>> readSubscriptionContext() {
        Map<String, Map<String, Map<String, Collection>>> data = null;
        try {
            // create object mapper instance
            ObjectMapper mapper = new ObjectMapper();

            data = mapper.readValue(file, Map.class);
            this.data = data;

        } catch (MismatchedInputException e) {
            return new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void writeSubscriptionContext(Map<String, Map<String, List<String>>> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
