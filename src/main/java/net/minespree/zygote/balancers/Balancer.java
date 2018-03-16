package net.minespree.zygote.balancers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multiset;
import io.playpen.core.coordinator.network.Server;
import io.playpen.core.p3.P3Package;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.config.BalancerConfig;

import java.util.Set;

/**
 * @since 02/02/2018
 */
public interface Balancer<T extends BalancerConfig> extends Runnable {
    /**
     * Gets the package name (P3) this balancer handles
     */
    String getPackageId();

    /**
     * Called when Zygote starts up
     */
    void initialProvision();

    Set<Server> getServers();

    /**
     * Will only return {@code true} if the server is active and being
     * managed by this balancer.
     */
    boolean isManagingServer(Server server);

    /**
     * Checks if the server package ID is the same as {@link #getPackageId()}
     */
    default boolean shouldBalanceServer(Server server) {
        Preconditions.checkNotNull(server);

        return server.getP3().getId().equals(getPackageId());
    }

    /**
     * @return A currently managed and provisioned server
     */
    Server getServer(String serverId);

    default int getServerCount() {
        return getServers().size();
    }

    /**
     * Retrieve the currently being provisioned servers IDs
     */
    Set<String> getProvisioning();

    void addProvisioning(String serverId);

    default int getProvisioningCount() {
        return getProvisioning().size();
    }

    Multiset<Server> getDeprovisionAttempts();

    default void addDeprovisionAttempt(Server server) {
        getDeprovisionAttempts().add(server);
    }

    default int getDeprovisionAttempts(Server server) {
        return getDeprovisionAttempts().count(server);
    }

    default boolean hasAttemptedDeprovision(Server server) {
        return getDeprovisionAttempts().contains(server);
    }

    void addServer(Server server);

    void removeServer(Server server);

    BalancerManager getManager();

    BalancerType getType();

    T getConfig();
}
