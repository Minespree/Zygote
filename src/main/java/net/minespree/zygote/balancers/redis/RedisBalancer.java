package net.minespree.zygote.balancers.redis;

import com.google.common.collect.Maps;
import io.playpen.core.coordinator.network.Server;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.AbstractBalancer;
import net.minespree.zygote.config.BalancerConfig;
import net.minespree.zygote.servers.LobbyStatus;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.Map;

/**
 * @since 02/02/2018
 */
public abstract class RedisBalancer<T extends BalancerConfig> extends AbstractBalancer<T> {
    protected Map<Server, LobbyStatus> statusMap;

    public RedisBalancer(BalancerManager manager, T config) {
        super(manager, config);
        statusMap = Maps.newConcurrentMap();
    }

    @Override
    public final void run() {
        try (Jedis jedis = getJedis()) {
            updateStatuses(jedis);
            checkDNRServers();

            update(jedis);
        }
    }

    /**
     * To be overrided by the balancer class
     * @param jedis An open Redis resource from the pool
     */
    protected void update(Jedis jedis) {}

    public void checkDNRServers() {
        long now = System.currentTimeMillis();

        statusMap.entrySet().removeIf(entry -> {
            Server server = entry.getKey();
            LobbyStatus status = entry.getValue();

            boolean dnr = status.isDnr(now, config.getDnrTimeout());

            if (dnr) {
                // Force deprovision
                manager.sendDeprovision(this, server, true);
            }

            return dnr;
        });
    }

    private void updateStatuses(Jedis jedis) {
        statusMap.clear();

        jedis.hgetAll("instance-statuses:" + getPackageId()).forEach((serverId, statusData) -> {
            Server server = getServer(serverId);

            if (server == null) {
                return;
            }

            LobbyStatus status = LobbyStatus.fromJson(statusData);

            if (status == null) {
                return;
            }

            statusMap.put(server, status);
        });
    }

    public Collection<LobbyStatus> getStatuses() {
        return statusMap.values();
    }

    protected Jedis getJedis() {
        return manager.getRedisManager().getResource();
    }
}
