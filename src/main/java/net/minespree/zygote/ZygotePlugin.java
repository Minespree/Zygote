package net.minespree.zygote;

import io.playpen.core.coordinator.CoordinatorMode;
import io.playpen.core.coordinator.PlayPen;
import io.playpen.core.coordinator.network.INetworkListener;
import io.playpen.core.coordinator.network.Network;
import io.playpen.core.plugin.AbstractPlugin;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ZygotePlugin extends AbstractPlugin {
    private BalancerManager manager;

    @Override
    public boolean onStart() {
        if (PlayPen.get().getCoordinatorMode() != CoordinatorMode.NETWORK) {
            log.fatal("Only network coordinators are supported");
            return false;
        }

        manager = new BalancerManager(getConfig());

        long tickRate = manager.getConfig().getTickRate();
        schedule(manager, tickRate, TimeUnit.SECONDS);

        BalancerListener listener = new BalancerListener(manager);
        registerListener(listener);

        return true;
    }

    @Override
    public void onStop() {
        manager.close();
    }

    public static void runAfter(Runnable runnable, long wait, TimeUnit timeUnit) {
        Network.get().getScheduler().schedule(runnable, wait, timeUnit);
    }

    public static void schedule(Runnable runnable, long period, TimeUnit timeUnit) {
        Network.get().getScheduler().scheduleAtFixedRate(runnable, period, period, timeUnit);
    }

    private static void registerListener(INetworkListener listener) {
        Network.get().getEventManager().registerListener(listener);
    }
}
