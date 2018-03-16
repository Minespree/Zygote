package net.minespree.zygote.balancers.game;

import lombok.Value;

/**
 * @since 03/02/2018
 */
@Value
public class GameBalancerStatus {
    private int active;
    private int total;

    public int getSpare() {
        return total - active;
    }
}
