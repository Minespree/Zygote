package net.minespree.zygote.balancers.game;

import io.playpen.core.coordinator.network.Server;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.BalancerType;
import net.minespree.zygote.servers.LobbyStatus;

/**
 * Live instance updates based Game balancer.
 * @since 03/02/2018
 */
public class FastGameBalancer extends BaseGameBalancer {
    public FastGameBalancer(BalancerManager manager, GameBalancerConfig config) {
        super(manager, config);
    }

    @Override
    public void addServer(Server server) {
        super.addServer(server);

        // Add fake status
        if (!statusMap.containsKey(server)) {
            statusMap.put(server, LobbyStatus.FAKE_ONLINE);
        }

        attemptCull();
    }

    @Override
    public void removeServer(Server server) {
        super.removeServer(server);

        statusMap.remove(server);
        attemptProvision();
    }

    @Override
    public BalancerType getType() {
        return BalancerType.FAST_GAME;
    }
}
