package mahss.io.discord.bot.service.savestate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import mahss.io.discord.bot.model.ContextData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationContextServiceImpl implements NotificationContextService {

    private List<ContextData> state;
    private static File file;
    private ObjectMapper objectMapper;

    public NotificationContextServiceImpl(final String path) {
        this.objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        module.addSerializer(LocalDateTime.class, localDateTimeSerializer);
        this.objectMapper.registerModule(module);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.findAndRegisterModules();

        this.file = Paths.get(path).toFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ContextData> getState() {
        return state;
    }

    @Override
    public List<ContextData> readNotificationContext() {
        List<ContextData> data = null;
        try {
            data = objectMapper.readValue(file, new TypeReference<List<ContextData>>() {
            });

            for (ContextData context : data) {
                System.out.println(context.id() + "=" + context.timestamp());
            }

        } catch (MismatchedInputException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        state = data;
        return data;
    }

    @Override
    public void writeNotificationContext(List<ContextData> data) {
        try {
            objectMapper.writeValue(file, cleanOldNotifications(data));
            state = data;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<ContextData> cleanOldNotifications(List<ContextData> data) {
        return data.stream().filter(e -> !(ChronoUnit.WEEKS.between(e.timestamp(), LocalDateTime.now()) >= 1)).collect(Collectors.toList());
    }
}
