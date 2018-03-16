package net.minespree.zygote.servers;

import lombok.Value;
import net.minespree.zygote.BalancerManager;

// TODO Move to commons class
@Value
public class LobbyStatus {
    public static final LobbyStatus FAKE_ONLINE = new LobbyStatus(GamePhase.STARTING, "", 0, 0, System.currentTimeMillis());

    private final GamePhase phase;
    private final String mapName;
    private final int playersOnline;
    private final int playersMax;
    private final long timestamp;

    public boolean isEmpty() {
        return playersOnline <= 0;
    }

    public boolean isDnr(long now, long timeout) {
        if (isDnrDisabled(timeout)) {
            return false;
        }

        return now >= timestamp + timeout;
    }

    public boolean isDnr(int timeout) {
        return isDnr(System.currentTimeMillis(), timeout);
    }

    private static boolean isDnrDisabled(long timeout) {
        return timeout <= 0;
    }

    public static LobbyStatus fromJson(String data) {
        return BalancerManager.GSON.fromJson(data, LobbyStatus.class);
    }
}