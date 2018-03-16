package net.minespree.zygote.balancers.game;

import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.BalancerType;
import redis.clients.jedis.Jedis;

/**
 * Redis poll based Game balancer
 * @since 02/02/2018
 */
public class GameBalancer extends BaseGameBalancer {
    public GameBalancer(BalancerManager manager, GameBalancerConfig config) {
        super(manager, config);
    }

    @Override
    protected void update(Jedis jedis) {
        GameBalancerStatus status = getBalancerStatus();

        boolean cull = attemptCull(status);

        if (!cull) {
            attemptProvision(status);
        }
    }

    @Override
    public BalancerType getType() {
        return BalancerType.GAME;
    }
}
