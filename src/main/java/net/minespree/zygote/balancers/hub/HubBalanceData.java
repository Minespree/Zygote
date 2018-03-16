package net.minespree.zygote.balancers.hub;

import lombok.Getter;
import lombok.Value;
import net.minespree.zygote.servers.LobbyStatus;

/**
 * @since 03/02/2018
 */
@Getter
public class HubBalanceData {
    private double ratio = 0;
    private int serverCount = 0;
    private int totalPlayers = 0;
    private int totalCapacity = 0;
    private double averageCapacity;

    public HubBalanceData(HubBalancer balancer) {
        for (LobbyStatus status : balancer.getStatuses()) {
            totalPlayers += status.getPlayersOnline();
            totalCapacity += status.getPlayersMax();
            serverCount++;
        }

        if (serverCount != 0 && totalPlayers != 0) {
            // We need max capacity average per server since we may have servers with different capacities
            // due to running different versions of the same package.
            averageCapacity = (double) totalCapacity / serverCount;

            ratio = (double) totalPlayers / (double) totalCapacity;
        }
    }

    /**
     * How many servers do we need to add/subtract to get a nice ratio?
     * 0 means no balancing is needed
     * + numbers mean to provision x servers
     * - numbers mean to deprovision x servers
     */
    public int getServerDiff(HubBalancer balancer) {
        HubBalancerConfig config = balancer.getConfig();
        double targetRatio = config.getTargetRatio();
        int amount = config.getMinimumInstances();

        if (serverCount != 0 && totalPlayers != 0) {
            /*
            idealRatio = totalPlayers / (totalCapacity + averageCapacity * amount)
            totalCapacity + averageCapacity * amount = totalPlayers / idealRatio
            amount = (totalPlayers / idealRatio - totalCapacity) / averageCapacity
             */

            amount = (int) Math.round((totalPlayers / targetRatio - totalCapacity) / averageCapacity);
        }

        int target = Math.max(config.getMinimumInstances(), Math.min(config.getMaximumInstances(), serverCount + amount));

        return target - serverCount;
    }
}
