package eu.macsworks.projectnhm.games.nhmGames.redis.pubsub.impl;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;
import eu.macsworks.projectnhm.games.nhmGames.managers.impl.GameManager;
import eu.macsworks.projectnhm.games.nhmGames.redis.pubsub.NHMJedisPubSub;
import eu.macsworks.projectnhm.games.nhmGames.utils.SignatureUtils;

import java.util.UUID;

public class PlayerServersPubSub extends NHMJedisPubSub {

    private static final long MAX_MESSAGE_AGE_MILLIS = 30_000L;

    private final NHMGames mainInstance;
    private final GameManager gameManager;

    public PlayerServersPubSub(NHMGames mainInstance) {
        super("nhm-games:player-servers");

        this.mainInstance = mainInstance;
        this.gameManager = mainInstance.getManager(GameManager.class);
    }


    @Override
    public void onMessage(String channel, String message) {
        if(!channel.equals(getChannel())) return;

        try {
            handle(message);
        } catch (Exception e) {
            NHMGames.LOGGER.warn("Failed to handle Redis message on channel '{}': {}", channel, message, e);
        }
    }

    private void handle(String message) {
        //Message specification:
        //UUID:ORIGIN-SERVER:DEST-SERVER:EPOCH:ARGS:SIGNATURE
        String[] split = message.split(":");
        if(split.length != 6) return;

        //If the signature fails the message has been tampered with or is the result of an
        //intrusion - disregard
        String signature = split[5];

        if(!SignatureUtils.isSignatureValid(message.substring(0, message.lastIndexOf(":")), signature)){
            NHMGames.LOGGER.error("Received a Redis message with an invalid signature: {}", message);
            return;
        }

        UUID playerUUID;
        long epoch;
        try {
            playerUUID = UUID.fromString(split[0]);
            epoch = Long.parseLong(split[3]);
        } catch (IllegalArgumentException e) {
            NHMGames.LOGGER.warn("Malformed (but signed) Redis message dropped: {}", message);
            return;
        }

        //Reject stale or future-dated messages
        long age = System.currentTimeMillis() - epoch;
        if (age < 0 || age > MAX_MESSAGE_AGE_MILLIS) {
            NHMGames.LOGGER.warn("Discarding Redis message outside freshness window (age={}ms): {}", age, message);
            return;
        }

        String destServerName = split[2];
        String args = split[4]; //ARGS: GAMEID;PARTYINFO (party not implemented yet)

        //This message isn't meant for this server
        if(!destServerName.equals(mainInstance.getLoadedConfig().getServerName())) return;

        String[] argParts = args.split(";");
        if (argParts.length < 1 || argParts[0].isEmpty()) {
            NHMGames.LOGGER.warn("Redis message missing GAMEID in args: {}", message);
            return;

        }
        String destinationGameID = argParts[0];

        gameManager.joinGame(playerUUID, destinationGameID);
    }
}
