package net.minespree.zygote.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.playpen.core.coordinator.network.LocalCoordinator;
import io.playpen.core.p3.P3Package;
import lombok.Getter;
import lombok.Setter;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.Balancer;
import net.minespree.zygote.util.PackageUtil;
import net.minespree.zygote.util.ServerUtil;

import java.util.Map;
import java.util.OptionalInt;

/**
 * @since 02/02/2018
 */
@Getter
@Setter
public class ServerTemplate {
    private final String serverId;
    private int port;
    private long startTime;
    private final P3Package p3Package;

    /**
     * Automatically creates a server template based on the
     * balancer settings.
     */
    public ServerTemplate(Balancer balancer, LocalCoordinator coordinator) {
        Preconditions.checkNotNull(balancer);

        String serverPrefix = balancer.getConfig().getServerPrefix();
        this.serverId = ServerUtil.generateServerName(serverPrefix);

        OptionalInt optPort = ServerUtil.findFreePort(coordinator);

        if (!optPort.isPresent()) {
            throw new RuntimeException("Couldn't find an available port on " + coordinator.getName());
        }

        this.port = optPort.getAsInt();
        this.startTime = System.currentTimeMillis();
        this.p3Package = PackageUtil.getPackage(balancer.getPackageId());

        Preconditions.checkArgument(p3Package != null, "Unable to find a promoted version of " + balancer.getPackageId());
    }

    public Map<String, String> toMap() {
        PackageUtil.checkValid(p3Package, "Invalid package staged for promotion");

        return new ImmutableMap.Builder<String, String>()
            .put("instance", serverId)
            .put("port", String.valueOf(port))
            .put("start_time", String.valueOf(startTime))
            .put("managed_by", BalancerManager.MANAGER_ID)
            .put("myers_expose", "true")
            .put("myers_package", p3Package.getId())
            .build();
    }
}
