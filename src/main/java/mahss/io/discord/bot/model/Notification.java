package mahss.io.discord.bot.model;

import mahss.io.discord.bot.factory.notification.NotificationContentFactory;

import java.time.LocalDateTime;

public class Notification {
    private String message;
    private String type;
    private NotificationContentFactory.SubType subType;
    private String id;
    private LocalDateTime expiry;

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public NotificationContentFactory.SubType getSubType() {
        return subType;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setSubType(NotificationContentFactory.SubType subType) {
        this.subType = subType;
    }

    private Notification(NotificationContentBuilder notificationContentBuilder) {
        this.message = notificationContentBuilder.message;
        this.type = notificationContentBuilder.type;
        this.subType = notificationContentBuilder.subType;
        this.id = notificationContentBuilder.id;
        this.expiry = notificationContentBuilder.expiry;
    }

    public static class NotificationContentBuilder {
        private String message;
        private String type;
        private NotificationContentFactory.SubType subType;
        private String id;
        private LocalDateTime expiry;

        public NotificationContentBuilder() {
        }

        public NotificationContentBuilder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public NotificationContentBuilder setType(final String type) {
            this.type = type;
            return this;
        }

        public NotificationContentBuilder setSubType(final NotificationContentFactory.SubType subType) {
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

        public Notification build() {
            return new Notification(this);
        }
    }
}
