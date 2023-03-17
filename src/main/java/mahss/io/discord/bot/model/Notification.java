package mahss.io.discord.bot.model;

import mahss.io.discord.bot.factory.notification.NotificationContentFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Notification {
    private String message;
    private String type;
    private NotificationContentFactory.NotType subType;
    private String id;
    private LocalDateTime expiry;
    private List<String> recipients;

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public NotificationContentFactory.NotType getSubType() {
        return subType;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setSubType(NotificationContentFactory.NotType subType) {
        this.subType = subType;
    }

    public void addRecipient(String recipient) {
        this.recipients.add(recipient);
    }

    private Notification(NotificationContentBuilder notificationContentBuilder) {
        this.message = notificationContentBuilder.message;
        this.type = notificationContentBuilder.type;
        this.subType = notificationContentBuilder.subType;
        this.id = notificationContentBuilder.id;
        this.expiry = notificationContentBuilder.expiry;
        this.recipients = notificationContentBuilder.recipients;
    }

    public static class NotificationContentBuilder {
        private String message;
        private String type;
        private NotificationContentFactory.NotType subType;
        private String id;
        private LocalDateTime expiry;
        private List<String> recipients;

        public NotificationContentBuilder() {
            this.recipients = new ArrayList<>();
        }

        public NotificationContentBuilder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public NotificationContentBuilder setType(final String type) {
            this.type = type;
            return this;
        }

        public NotificationContentBuilder setSubType(final NotificationContentFactory.NotType subType) {
            this.subType = subType;
            return this;
        }

        public NotificationContentBuilder setId(final String id) {
            this.id = id;
            return this;
        }

        public NotificationContentBuilder setExpiry(final LocalDateTime expiry) {
            this.expiry = expiry;
            return this;
        }

        public NotificationContentBuilder setRecipients(final List<String> recipients) {
            this.recipients = recipients;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }
    }
}
