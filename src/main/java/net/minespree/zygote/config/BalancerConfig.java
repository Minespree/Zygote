package net.minespree.zygote.config;

import com.google.common.base.Preconditions;
import io.playpen.core.p3.P3Package;
import lombok.Getter;
import net.minespree.zygote.balancers.Balancer;
import net.minespree.zygote.balancers.BalancerType;
import net.minespree.zygote.util.PackageUtil;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * @since 02/02/2018
 */
@Getter
public class BalancerConfig<T extends Balancer> {
    private static final long DEFAULT_DNR_TIMEOUT = TimeUnit.SECONDS.toMillis(60);

    private final String packageId;
    private final int minimumInstances;
    private final int maximumInstances;
    private final long dnrTimeout;
    private final String serverPrefix;
    private final BalancerType type;

    public BalancerConfig(JSONObject data, BalancerType type) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(type);

        this.packageId = data.getString("package");
        this.minimumInstances = data.getInt("minimum");
        this.maximumInstances = data.getInt("maximum");
        this.dnrTimeout = TimeUnit.SECONDS.toMillis(
            data.optLong("dnrTimeout", DEFAULT_DNR_TIMEOUT)
        );
        this.serverPrefix = data.getString("serverPrefix");
        this.type = type;
    }
}
