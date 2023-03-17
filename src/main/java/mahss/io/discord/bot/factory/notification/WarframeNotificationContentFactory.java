package mahss.io.discord.bot.factory.notification;

import com.fasterxml.jackson.databind.JsonNode;
import mahss.io.discord.bot.model.Notification;
import mahss.io.discord.bot.model.NotificationData;
import mahss.io.discord.bot.model.warframe.Bounty;
import mahss.io.discord.bot.model.warframe.Fissure;
import mahss.io.discord.bot.model.warframe.Mission;
import mahss.io.discord.bot.model.warframe.Syndicate;
import mahss.io.discord.bot.service.WarframeDataService;
import mahss.io.discord.bot.util.ParsingUtils;

import java.time.LocalDateTime;
import java.util.*;

public class WarframeNotificationContentFactory implements NotificationContentFactory {
    private static final String SERVICE_NAME = "Warframe";
    private static final String SORITE_MAIN_MESSAGE =
            "**Faction**: %s\n" +
                    "**Missions**: \n" +
                    "%s";

    private static final String ALERT_MAIN_MESSAGE =
            "**Faction**: %s\n" +
                    "**Mission**: \n" +
                    "%s";

    private static final String FISSURE_MAIN_MESSAGE =
            "**Faction**: %s\n" +
                    "%s" +
                    "**Mission**: \n" +
                    "%s";

    private static final String CETUS_NIGHT_MESSAGE =
            "It is now Night time on the Plains of Cetus!";

    private static final String STEEL_PATH_MAIN_MESSAGE =
            "**Reward**: %s\n" +
                    "**Cost**: %s Steel Essence\n";

    private static final String VOID_TRADER_MAIN_MESSAGE =
            "The wait is over Tenno, Baro Ki'Teer has arrived.\n" +
                    "**Items**: \n" +
                    "%s";

    private static final String INVASION_MAIN_MESSAGE =
            "%s vs. %s\n" +
                    "**Possible Items**: \n" +
                    "%s";

    private static final String BOUNTY_MAIN_MESSAGE =
            "**%s\n**" +
                    "%s\n" +
                    "Stages: %d | Levels: %d -%d\n" +
                    "**Possible Items**: \n" +
                    "%s";
    private static final String MISSION_STRING_4 = "> %s - %s - %s - %s\n";
    private static final String MISSION_STRING_3 = "> %s - %s - %s\n";
    private static final String MISSION_STRING_2 = "> %s - %s\n";

    private WarframeDataService warframeDataService;

    public WarframeNotificationContentFactory(WarframeDataService warframeDataService, Map<String, List<Object>> requests) {
        this.warframeDataService = warframeDataService;
    }

    @Override
    public List<Notification> getNotificationsContent() {
        List<Notification> notificationsContent = new ArrayList<>();
        Notification sortieNotification = sortieToNotificationContent();
        if (sortieNotification != null) {
            notificationsContent.add(sortieNotification);
        }
        Notification cetusNotification = cetusCycleToNotificationContent();
        if (cetusNotification != null) {
            notificationsContent.add(cetusNotification);
        }
        Notification steelPathNotification = steelPathToNotificationContent();
        if (steelPathNotification != null) {
            notificationsContent.add(steelPathNotification);
        }
        Notification voidTraderToNotification = voidTraderToNotificationContent();
        if (voidTraderToNotification != null) {
            notificationsContent.add(voidTraderToNotification);
        }

        notificationsContent.addAll(alertToNotificationContent());

        Map<NotificationData, Fissure> allFissures = getFissureMissions();
        notificationsContent.addAll(fissureToNotificationContent(allFissures, Mission.Type.SURVIVAL));

        Map<NotificationData, Mission[]> allInvasions = getInvasions();
        notificationsContent.addAll(getInvasionsWithReward(allInvasions, "mass"));

        List<Bounty> bounties = getBounties();
        notificationsContent.addAll(getBountyNotificationWithReward(bounties, "endo"));

        return notificationsContent;
    }

    private Collection<? extends Notification> getBountyNotificationWithReward(List<Bounty> bounties, String targetReward) {
        List<Notification> bountyNotifications = new ArrayList<>();
        bounties.forEach(bounty -> {
            if (bounty.getRewards().stream().anyMatch(reward -> reward.toLowerCase().contains(targetReward))) {
                String rewards = rewardStringBuilder(bounty.getRewards());

                bountyNotifications.add(new Notification.NotificationContentBuilder()
                        .setMessage(String.format(BOUNTY_MAIN_MESSAGE,
                                bounty.getSyndicate().getName(),
                                bounty.getName(),
                                bounty.getStages(),
                                bounty.getLevels()[0],
                                bounty.getLevels()[1],
                                rewards
                        ))
                        .setType(SERVICE_NAME)
                        .setSubType(SubType.BOUNTIES)
                        .setId(bounty.getId())
                        .setExpiry(bounty.getExpiry())
                        .build()
                );
            }
        });

        return bountyNotifications;
    }


    private Collection<? extends Notification> getInvasionsWithReward(Map<NotificationData, Mission[]> invasions, String targetReward) {
        List<Notification> invasionNotifications = new ArrayList<>();
        invasions.forEach((notificationData, missions) -> {
            String aReward = missions[0].getReward();
            String dReward = missions[1].getReward();
            if (aReward.toLowerCase().contains(targetReward) || dReward.toLowerCase().contains(targetReward)) {
                String rewards = rewardStringBuilder(new ArrayList<>() {{
                    if (aReward != null && !(aReward.isEmpty() || aReward.isBlank())) {
                        add(aReward);
                    }
                    if (dReward != null && !(dReward.isEmpty() || dReward.isBlank())) {
                        add(dReward);
                    }
                }});

                invasionNotifications.add(new Notification.NotificationContentBuilder()
                        .setMessage(String.format(INVASION_MAIN_MESSAGE,
                                missions[0].getFaction(),
                                missions[1].getFaction(),
                                rewards))
                        .setType(SERVICE_NAME)
                        .setSubType(SubType.INVASIONS)
                        .setId(notificationData.getId())
                        .setExpiry(notificationData.getExpire())
                        .build()
                );
            }
        });

        return invasionNotifications;
    }

    private Notification cetusCycleToNotificationContent() {
        JsonNode cetusData = warframeDataService.getData().get("cetusCycle");
        if (cetusData != null) {
            String id = cetusData.get("id").asText();
            LocalDateTime expiry = ParsingUtils.stringToLocalDateTime(cetusData.get("expiry").asText());
            boolean isDay = cetusData.get("isDay").asBoolean();

            LocalDateTime now = LocalDateTime.now();
            if (isDay && now.isAfter(expiry)) {
                return new Notification.NotificationContentBuilder()
                        .setMessage(CETUS_NIGHT_MESSAGE)
                        .setType(SERVICE_NAME)
                        .setSubType(SubType.CETUS)
                        .setId(id)
                        .setExpiry(expiry)
                        .build();
            }
        }
        return null;
    }

    private List<Notification> alertToNotificationContent() {
        List<Notification> alertNotifications = new ArrayList<>();
        JsonNode alerts = warframeDataService.getData().get("alerts");
        if (alerts != null) {
            alerts.forEach(alert -> {
                String id = alert.get("id").asText();
                LocalDateTime expiry = ParsingUtils.stringToLocalDateTime(alert.get("expiry").asText());
                JsonNode alertMission = alert.get("mission");

                alertNotifications.add(new Notification.NotificationContentBuilder()
                        .setMessage(String.format(
                                ALERT_MAIN_MESSAGE,
                                Mission.Faction.valueOf(alertMission.get("faction").asText().toUpperCase(Locale.ROOT)),
                                String.format(MISSION_STRING_2,
                                        alertMission.get("type").asText(),
                                        ParsingUtils.nodeToNodeAndCelestialBody(alertMission.get("node").asText()).get(0)
                                )
                        ))
                        .setType(SERVICE_NAME)
                        .setSubType(SubType.ALERT)
                        .setId(id)
                        .setExpiry(expiry)
                        .build()
                );
            });
        }
        return alertNotifications;
    }

    private Notification sortieToNotificationContent() {
        ArrayList<Mission> sorties = new ArrayList<>();
        JsonNode jsonNode = warframeDataService.getData().get("sortie");
        if (jsonNode != null) {
            String id = jsonNode.get("id").asText();
            String stringFaction = jsonNode.get("faction").asText().toUpperCase(Locale.ROOT);
            Mission.Faction faction = stringFaction.equals("INFESTATION") ? Mission.Faction.INFESTED : Mission.Faction.valueOf(stringFaction);

            LocalDateTime expiry = ParsingUtils.stringToLocalDateTime(jsonNode.get("expiry").asText());

            JsonNode variants = jsonNode.get("variants");
            variants.forEach(mission -> {
                List<String> cBodyAndNode = ParsingUtils.nodeToNodeAndCelestialBody(mission.get("node").asText());
                sorties.add(new Mission(
                                faction,
                                "completion",
                                Mission.Type.valueOf(ParsingUtils.missionTypeToType(mission.get("missionType").asText())),
                                mission.get("modifier").asText(),
                                cBodyAndNode.get(0),
                                cBodyAndNode.get(1),
                                expiry
                        )
                );
            });

            List<String> missionLines = new ArrayList<>();
            sorties.stream().forEach(mission -> missionLines.add(String.format(MISSION_STRING_3, mission.getType().toString(), mission.getCBody(), mission.getConstraint())));
            String missionsString = String.join("", missionLines);

            String message = String.format(SORITE_MAIN_MESSAGE, ParsingUtils.enumToDisplayText(faction.toString()), missionsString);
            return new Notification.NotificationContentBuilder()
                    .setMessage(message)
                    .setType(SERVICE_NAME)
                    .setSubType(SubType.SORTIE)
                    .setId(id)
                    .setExpiry(expiry)
                    .build();
        }
        return null;
    }

    private List<Notification> fissureToNotificationContent(Map<NotificationData, Fissure> fissureMissions, Mission.Type missionType) {
        List<Notification> fissureNotifications = new ArrayList<>();

        fissureMissions.entrySet().stream().forEach((fissureEntry -> {
            NotificationData notificationData = fissureEntry.getKey();
            Fissure fissure = fissureEntry.getValue();

            if (fissure.getType().equals(missionType)) {
                fissureNotifications.add(new Notification.NotificationContentBuilder()
                        .setMessage(String.format(
                                FISSURE_MAIN_MESSAGE,
                                fissure.getFaction(),
                                fissure.isSteelPath() || fissure.isRailjack() ? ((fissure.isSteelPath() ? "Steel Path" : "") + " " + (fissure.isRailjack() ? "Railjack" : "") + "\n") : "",
                                String.format(MISSION_STRING_4,
                                        fissure.getType().toString(),
                                        fissure.getCBody(),
                                        fissure.getNode(),
                                        fissure.getRelicType().toString()
                                )
                        ))
                        .setType(SERVICE_NAME)
                        .setSubType(SubType.FISSURES)
                        .setId(notificationData.getId())
                        .setExpiry(notificationData.getExpire())
                        .build()
                );
            }
        }));
        return fissureNotifications;
    }

    private Notification steelPathToNotificationContent() {
        JsonNode steelPath = warframeDataService.getData().get("steelPath");
        if (steelPath != null) {
            JsonNode reward = steelPath.get("currentReward");
            String expiryString = steelPath.get("expiry").asText();
            LocalDateTime expiry = ParsingUtils.stringToLocalDateTime(expiryString);
            String id = String.valueOf(("steelPath" + reward.get("name").asText() + expiryString).hashCode());

            return new Notification.NotificationContentBuilder()
                    .setMessage(String.format(STEEL_PATH_MAIN_MESSAGE,
                            reward.get("name").asText(),
                            reward.get("cost").asText()
                    ))
                    .setType(SERVICE_NAME)
                    .setSubType(SubType.STEEL_PATH_HONOR)
                    .setId(id)
                    .setExpiry(expiry)
                    .build();
        }
        return null;
    }

    private Notification voidTraderToNotificationContent() {
        JsonNode voidTrader = warframeDataService.getData().get("voidTrader");
        if (voidTrader != null) {
            String id = voidTrader.get("id").asText();
            boolean isActive = voidTrader.get("active").asBoolean();
            JsonNode inventory = voidTrader.get("currentReward");
            LocalDateTime expiry = ParsingUtils.stringToLocalDateTime(voidTrader.get("expiry").asText());

            if (isActive) {
                String inventoryMessage = "";
                for (JsonNode item : inventory) {
                    inventoryMessage += ("> " + item.get("name").asText() + "\n");
                }
                return new Notification.NotificationContentBuilder()
                        .setMessage(String.format(VOID_TRADER_MAIN_MESSAGE,
                                inventoryMessage
                        ))
                        .setType(SERVICE_NAME)
                        .setSubType(SubType.VOID_TRADER)
                        .setId(id)
                        .setExpiry(expiry)
                        .build();
            }
        }
        return null;
    }

    private Notification emptyNotificationContent() {
        return new Notification.NotificationContentBuilder()
                .setMessage("Error")
                .setType(SERVICE_NAME)
                .setSubType(SubType.UNKNOWN)
                .setId(UUID.randomUUID().toString())
                .build();
    }


    private String rewardStringBuilder(List<String> rewards) {
        String rewardString = "";
        for (String reward : rewards) {
            if (rewardString.isEmpty() || rewardString.isBlank()) {
                rewardString += "> " + reward;
            } else {
                rewardString += "\n> " + reward;
            }
        }
        return rewardString;
    }

    private Map<NotificationData, Fissure> getFissureMissions() {
        Map<NotificationData, Fissure> allFissures = new HashMap<>();
        JsonNode fissures = warframeDataService.getData().get("fissures");
        if (fissures != null) {
            fissures.forEach(fissure -> {
                String id = fissure.get("id").asText();
                LocalDateTime expiry = ParsingUtils.stringToLocalDateTime(fissure.get("expiry").asText());

                String type = fissure.get("missionKey").asText();
                String faction = fissure.get("enemyKey").asText();
                ArrayList<String> node = ParsingUtils.nodeToNodeAndCelestialBody(fissure.get("nodeKey").asText());
                String tier = fissure.get("tier").asText();
                boolean isHard = fissure.get("isHard").asBoolean();
                boolean isStorm = fissure.get("isStorm").asBoolean();

                allFissures.put(new NotificationData(id, expiry),
                        new Fissure(Mission.Faction.valueOf(faction.toUpperCase(Locale.ROOT)),
                                "",
                                Mission.Type.valueOf(type.replace(" ", "_").toUpperCase(Locale.ROOT)),
                                "",
                                node.get(0),
                                node.get(1),
                                expiry,
                                Fissure.RelicType.valueOf(tier.toUpperCase(Locale.ROOT)),
                                isHard,
                                isStorm));
            });
        }
        return allFissures;
    }

    private List<Bounty> getBounties() {
        List<Bounty> allBounties = new ArrayList();
        JsonNode syndicateMissions = warframeDataService.getData().get("syndicateMissions");
        if (syndicateMissions != null) {
            syndicateMissions.forEach(syndicate -> {
                Syndicate syndicateKey = Syndicate.fromText(syndicate.get("syndicateKey").asText());
                if (syndicateKey.isHasJobs()) {
                    JsonNode jobs = syndicate.get("jobs");
                    if (jobs != null) {
                        jobs.forEach(job -> {
                            String id = job.get("id").asText();
                            String name = job.get("type").asText();
                            LocalDateTime expiry = ParsingUtils.stringToLocalDateTime(job.get("expiry").asText());
                            int[] levels = {job.get("enemyLevels").get(0).asInt(), job.get("enemyLevels").get(1).asInt()};
                            int stages = job.get("standingStages").size();
                            List<String> rewards = new ArrayList<>() {{
                                for (JsonNode reward : job.get("rewardPool")) {
                                    add(reward.asText());
                                }
                            }};

                            allBounties.add(
                                    new Bounty(
                                            id,
                                            name,
                                            expiry,
                                            syndicateKey,
                                            levels,
                                            stages,
                                            rewards
                                    )
                            );
                        });
                    }
                }
            });
        }
        return allBounties;
    }

    private Map<NotificationData, Mission[]> getInvasions() {
        Map<NotificationData, Mission[]> allInvasions = new HashMap<>();
        JsonNode invasions = warframeDataService.getData().get("invasions");
        if (invasions != null) {
            invasions.forEach(invasion -> {
                boolean completed = invasion.get("completed").asBoolean();
                if (!completed) {
                    String id = invasion.get("id").asText();
                    String eta = invasion.get("eta").asText();
                    LocalDateTime startTime = ParsingUtils.stringToLocalDateTime(invasion.get("activation").asText());

                    LocalDateTime expiry = ParsingUtils.getExpireFromStartAndEta(startTime, eta);

                    ArrayList<String> node = ParsingUtils.nodeToNodeAndCelestialBody(invasion.get("nodeKey").asText());
                    JsonNode attacker = invasion.get("attacker");
                    JsonNode defender = invasion.get("defender");
                    if (attacker != null && defender != null) {
                        NotificationData notificationData = new NotificationData(id, expiry);

                        String aFaction = attacker.get("factionKey").asText();
                        JsonNode aReward = attacker.get("reward");
                        String dFaction = defender.get("factionKey").asText();
                        JsonNode dReward = defender.get("reward");

                        Mission[] missions = new Mission[2];
                        missions[0] = new Mission(Mission.Faction.valueOf(aFaction.toUpperCase(Locale.ROOT)),
                                aReward.get("itemString").asText(),
                                Mission.Type.CROSSFIRE,
                                "",
                                node.get(0),
                                node.get(1),
                                expiry);

                        missions[1] = new Mission(Mission.Faction.valueOf(dFaction.toUpperCase(Locale.ROOT)),
                                dReward.get("itemString").asText(),
                                Mission.Type.CROSSFIRE,
                                "",
                                node.get(0),
                                node.get(1),
                                expiry);
                        allInvasions.put(notificationData, missions);
                    }
                }
            });
        }
        return allInvasions;
    }

    enum SubType implements NotificationContentFactory.SubType {
        SORTIE(null),
        ALERT(null),
        CETUS(null),
        STEEL_PATH_HONOR(null),
        VOID_TRADER(null),
        FISSURES(new ArrayList<>() {{
            add(Mission.Type.EXCAVATION);
            add(Mission.Type.CAPTURE);
            add(Mission.Type.DEFENSE);
            add(Mission.Type.RESCUE);
            add(Mission.Type.SABOTAGE);
            add(Mission.Type.VOLATILE);
            add(Mission.Type.ORPHIX);
            add(Mission.Type.SPY);
            add(Mission.Type.SURVIVAL);
            add(Mission.Type.SKIRMISH);
            add(Mission.Type.KUVA_SURVIVAL);
            add(Mission.Type.MOBILE_DEFENSE);
            add(Mission.Type.INTERCEPTION);
            add(Mission.Type.EXTERMINATION);
            add(Mission.Type.ASSAULT);
            add(Mission.Type.DISRUPTION);
        }}),
        BOUNTIES(null),
        INVASIONS(null),
        UNKNOWN(null);

        List<Object> subTypes;

        SubType(List<Object> subTypes) {
            this.subTypes = subTypes;
        }

        public List<Object> getSubTypes() {
            return subTypes;
        }
    }
}
