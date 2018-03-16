package net.minespree.zygote;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import io.playpen.core.coordinator.network.LocalCoordinator;
import io.playpen.core.coordinator.network.Network;
import io.playpen.core.coordinator.network.ProvisionResult;
import io.playpen.core.coordinator.network.Server;
import io.playpen.core.p3.P3Package;
import lombok.Getter;
import net.minespree.zygote.balancers.Balancer;
import net.minespree.zygote.config.ServerTemplate;
import net.minespree.zygote.util.PackageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @since 02/02/2018
 */
@Getter
public class BalancerManager implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger(BalancerManager.class);
    public static final Gson GSON = new Gson();

    public static final String MANAGER_ID = "zygote";
    public static final int MAX_DEPROVISION_TRIES = 3;
    public static final int MAX_TOLERATE_PROVISIONING = 3;

    private final ZygoteConfig config;

    private final RedisManager redisManager;
    private final Set<Balancer> balancers;

    public BalancerManager(JSONObject configData) {
        this.config = new ZygoteConfig(configData);
        this.balancers = Sets.newHashSet();
        this.redisManager = new RedisManager(config.getRedisConfig());
    }

    public void createBalancers() {
        config.getBalancerConfigs().forEach(config -> {
            Balancer balancer = config.getType().createBalancer(this, config);

            if (balancer == null) {
                return;
            }

            balancer.initialProvision();

            balancers.add(balancer);
        });
    }

    @Override
    public void run() {
        balancers.forEach(Runnable::run);
    }

    public void close() {
        redisManager.close();
        balancers.clear();
    }

    public ServerTemplate provision(Balancer balancer) {
        Preconditions.checkNotNull(balancer);

        P3Package p3Package = PackageUtil.getPackage(balancer.getPackageId());
        LocalCoordinator coordinator = PackageUtil.findCoordinator(p3Package);

        if (coordinator == null) {
            LOGGER.error("Unable to find an acceptable LC for {}", balancer.getPackageId());
            return null;
        }

        ServerTemplate template = new ServerTemplate(balancer, coordinator);

        Map<String, String> propertyMap = template.toMap();
        String serverId = template.getServerId();

        ProvisionResult result = Network.get().provision(p3Package, serverId, propertyMap, coordinator.getName());

        // PlayPen failed to provision the server
        if (result == null) {
            return null;
        }

        balancer.addProvisioning(serverId);

        return template;
    }

    public void notifyProvision(Server server) {
        Balancer balancer = getBalancer(server);

        if (balancer == null) {
            return;
        }

        balancer.addServer(server);
    }

    public boolean sendDeprovision(Balancer balancer, Server server, boolean force) {
        Preconditions.checkNotNull(balancer);
        Preconditions.checkArgument(server != null && server.isActive(), "Deprovision requests can only be sent to active servers");

        boolean sent = Network.get().deprovision(server.getCoordinator().getName(), server.getName(), force);

        if (!sent) {
            return false;
        }

        balancer.addDeprovisionAttempt(server);

        return true;
    }

    public boolean sendDeprovision(Balancer balancer, Server server) {
        return sendDeprovision(balancer, server, false);
    }

    public void notifyDeprovision(Server server) {
        Balancer balancer = getActiveBalancer(server);

        if (balancer == null) {
            return;
        }

        balancer.removeServer(server);
    }

    public Balancer getBalancer(Server server) {
        return balancers.stream()
                .filter(balancer -> balancer.shouldBalanceServer(server))
                .findAny()
                .orElse(null);
    }

    public Balancer getActiveBalancer(Server server) {
        return balancers.stream()
                .filter(balancer -> balancer.isManagingServer(server))
                .findAny()
                .orElse(null);
    }
}
