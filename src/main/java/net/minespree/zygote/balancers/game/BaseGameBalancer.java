package net.minespree.zygote.balancers.game;

import io.playpen.core.coordinator.network.Server;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.redis.RedisBalancer;
import net.minespree.zygote.servers.LobbyStatus;
import net.minespree.zygote.util.ServerUtil;

import java.util.Set;

/**
 * @since 03/02/2018
 */
public abstract class BaseGameBalancer extends RedisBalancer<GameBalancerConfig> {
    public BaseGameBalancer(BalancerManager manager, GameBalancerConfig config) {
        super(manager, config);
    }

    protected GameBalancerStatus getBalancerStatus() {
        int active = 0;
        int total = 0;

        for (LobbyStatus status : statusMap.values()) {
            total++;

            if (status.getPhase().isActive()) {
                active++;
            }
        }

        return new GameBalancerStatus(active, total);
    }

    protected boolean attemptCull(GameBalancerStatus status) {
        int spare = status.getSpare();

        if (spare <= config.getMaximumSpare()) {
            return false;
        }

        int mayDeprovision = spare - config.getMaximumSpare();
        Set<Server> toDeprovision = ServerUtil.getDeprovisionCandidates(statusMap, mayDeprovision);

        toDeprovision.forEach(this::deprovisionServer);

        return true;
    }

    protected boolean attemptCull() {
        return attemptCull(getBalancerStatus());
    }

    protected boolean attemptProvision(GameBalancerStatus status) {
        int spare = status.getSpare();

        if (spare > config.getMaximumSpare()) {
            return false;
        }

        int provisioning = getProvisioningCount();

        if (provisioning > BalancerManager.MAX_TOLERATE_PROVISIONING) {
            // Awaiting server provisioning
            return false;
        }

        int total = status.getTotal() + provisioning;

        if (total >= config.getMaximumInstances()) {
            return false;
        }

        int totalSpare = status.getSpare() + provisioning;

        if (totalSpare < config.getMinimumSpare()) {
            int mustProvision = config.getMinimumSpare() - totalSpare;

            provisionServers(mustProvision);
        } else if (totalSpare < config.getMaximumSpare()) {
            // Provision one server at a time, there's no demand
            provisionServer();
        }

        return true;
    }

    protected boolean attemptProvision() {
        return attemptCull(getBalancerStatus());
    }
}
