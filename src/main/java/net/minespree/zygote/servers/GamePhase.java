package net.minespree.zygote.servers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Denotes the phase the current game is in.
 */
@Getter
@RequiredArgsConstructor
public enum GamePhase {
    /**
     * The server is starting up. No players can be accepted at this time.
     */
    STARTING(false),
    /**
     * The server is selecting and initializing a map. No players can be accepted at this time.
     */
    SETUP(false),
    /**
     * The server is awaiting new players.
     */
    WAITING(false),
    /**
     * The game on this server is now in progress.
     */
    PLAYING(true),
    /**
     * The game on this server has finished. We will rotate to the setup phase once all players have been kicked.
     */
    ENDGAME(true);

    public final boolean active;
}