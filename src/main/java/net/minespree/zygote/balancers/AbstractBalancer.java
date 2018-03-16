package net.minespree.zygote.balancers;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import io.playpen.core.coordinator.network.Server;
import io.playpen.core.p3.P3Package;
import lombok.Getter;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.config.BalancerConfig;
import net.minespree.zygote.config.ServerTemplate;
import net.minespree.zygote.util.PackageUtil;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * @since 02/02/2018
 */
@Getter
public abstract class AbstractBalancer<T extends BalancerConfig> implements Balancer<T> {
    protected final BalancerManager manager;
    protected final T config;

    protected final Set<Server> servers;
    protected final Set<String> provisioning;
    protected final Multiset<Server> deprovisionAttempts;

    public AbstractBalancer(BalancerManager manager, T config) {
        Preconditions.checkNotNull(manager);
        Preconditions.checkNotNull(config);

        this.manager = manager;
        this.config = config;
        this.servers = Sets.newConcurrentHashSet();
        this.provisioning = Sets.newConcurrentHashSet();
        this.deprovisionAttempts = HashMultiset.create();
    }

    protected ServerTemplate provisionServer() {
        return manager.provision(this);
    }

    protected Set<ServerTemplate> provisionServers(int count) {
        Set<ServerTemplate> templates = Sets.newLinkedHashSet();

        for (int i = 0; i < count; i++) {
            templates.add(provisionServer());
        }

        return templates;
    }

    protected void deprovisionServer(Server server) {
        boolean firstTry = hasAttemptedDeprovision(server);

        // Attempt first deprovision
        if (firstTry) {
            manager.sendDeprovision(this, server);
            return;
        }

        int count = getDeprovisionAttempts(server);

        if (count >= BalancerManager.MAX_DEPROVISION_TRIES) {
            manager.sendDeprovision(this, server, true);
        }
    }

    @Override
    public void initialProvision() {
        provisionServers(config.getMinimumInstances());
    }

    @Override
    public boolean isManagingServer(Server server) {
        return servers.contains(server);
    }

    @Override
    public Server getServer(String serverId) {
        return servers.stream()
                .filter(server -> server.getName().equals(serverId))
                .findAny()
                .orElse(null);
    }

    @Override
    public void addProvisioning(String serverId) {
        provisioning.add(serverId);
    }

    @Override
    public void addServer(Server server) {
        servers.add(server);
        provisioning.remove(server.getName());
    }

    @Override
    public void removeServer(Server server) {
        servers.remove(server);
        deprovisionAttempts.remove(server);
    }

    protected Logger getLogger() {
        return BalancerManager.LOGGER;
    }

    @Override
    public String getPackageId() {
        return config.getPackageId();
    }
}
