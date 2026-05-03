package eu.macsworks.projectnhm.games.nhmGames.api;

import eu.macsworks.projectnhm.games.nhmGames.NHMGames;

import java.util.logging.Logger;

public interface NHMLifecycledObject {

    default String getId() {
        return getClass().getSimpleName();
    }

    default void init(){
        Logger logger = NHMGames.getInstance().getLogger();

        long timeInitStart = System.currentTimeMillis();
        logger.info(String.format("Initializing: %s", getId()));

        onInit();

        logger.info(String.format("Initialized: %s, took %sms", getId(),  System.currentTimeMillis() - timeInitStart));
    }

    default void destroy(){
        Logger logger = NHMGames.getInstance().getLogger();

        long timeDestroyStart = System.currentTimeMillis();
        logger.info(String.format("Destroyed: %s", getId()));

        onDestroy();

        logger.info(String.format("Destroyed: %s, took %sms", getId(),  System.currentTimeMillis() - timeDestroyStart));
    }

    void onInit();
    void onDestroy();

}
