package net.minespree.zygote;

import io.playpen.core.coordinator.network.INetworkListener;
import io.playpen.core.coordinator.network.LocalCoordinator;
import io.playpen.core.coordinator.network.Server;
import io.playpen.core.plugin.EventManager;
import io.playpen.core.plugin.IPlugin;
import lombok.RequiredArgsConstructor;
import net.minespree.zygote.util.ServerUtil;

/**
 * @since 03/02/2018
 */
@RequiredArgsConstructor
public class BalancerListener implements INetworkListener {
    private final BalancerManager manager;
    private boolean firstCoordinator = true;

    @Override
    public void onCoordinatorCreated(LocalCoordinator localCoordinator) {
        if (!firstCoordinator) {
            return;
        }

        firstCoordinator = false;
        manager.createBalancers();
    }

    @Override
    public void onProvisionResponse(LocalCoordinator localCoordinator, Server server, boolean b) {
        if (!ServerUtil.isManaged(server)) {
            return;
        }

        manager.notifyProvision(server);
    }

    @Override
    public void onServerShutdown(LocalCoordinator localCoordinator, Server server) {
        if (!ServerUtil.isManaged(server)) {
            return;
        }

        manager.notifyDeprovision(server);
    }

    @Override
    public void onNetworkStartup() {}

    @Override
    public void onNetworkShutdown() {}

    @Override
    public void onRequestShutdown(LocalCoordinator localCoordinator) {}

    @Override
    public void onCoordinatorSync(LocalCoordinator localCoordinator) {}

    @Override
    public void onRequestProvision(LocalCoordinator localCoordinator, Server server) {}

    @Override
    public void onRequestDeprovision(LocalCoordinator localCoordinator, Server server) {}

    @Override
    public void onPluginMessage(IPlugin iPlugin, String s, Object... objects) {}

    @Override
    public void onListenerRegistered(EventManager<INetworkListener> eventManager) {}

    @Override
    public void onListenerRemoved(EventManager<INetworkListener> eventManager) {}
}
