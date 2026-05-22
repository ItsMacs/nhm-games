package eu.macsworks.projectnhm.games.nhmGames.redis.pubsub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
@Getter
public class NHMJedisPubSub extends JedisPubSub {

    private final String channel;

}
