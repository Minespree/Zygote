package net.minespree.zygote.balancers.hub;

import io.playpen.core.coordinator.network.Server;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.BalancerType;
import net.minespree.zygote.balancers.redis.RedisBalancer;
import net.minespree.zygote.util.ServerUtil;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @since 03/02/2018
 */
public class HubBalancer extends RedisBalancer<HubBalancerConfig> {
    public HubBalancer(BalancerManager manager, HubBalancerConfig config) {
        super(manager, config);
    }

    @Override
    protected void update(Jedis jedis) {
        HubBalanceData data = createSnapshot();
        int amount = data.getServerDiff(this);

        if (amount == 0) {
            // No need to balance
            return;
        }

        if (amount < 0) {
            Set<Server> toDeprovision = ServerUtil.getDeprovisionCandidates(statusMap, amount);

            toDeprovision.forEach(this::deprovisionServer);
        } else {
            provisionServers(amount);
        }
    }

    private HubBalanceData createSnapshot() {
        return new HubBalanceData(this);
    }

    @Override
    public BalancerType getType() {
        return BalancerType.HUB;
    }
}
