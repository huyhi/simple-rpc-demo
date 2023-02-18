package rpc.core.hooks;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CleanShutdownHook {

    // singleton
    private static final CleanShutdownHook CLEAN_SHUTDOWN_HOOK = new CleanShutdownHook();

    public static CleanShutdownHook getCleanShutdownHook() {
        return CLEAN_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("register shutdown hook.");
        Runtime.getRuntime().addShutdownHook(new Thread(new CleanAction()));
    }
}
