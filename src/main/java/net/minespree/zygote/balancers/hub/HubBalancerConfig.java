package net.minespree.zygote.balancers.hub;

import lombok.Getter;
import net.minespree.zygote.balancers.BalancerType;
import net.minespree.zygote.config.BalancerConfig;
import org.json.JSONObject;

/**
 * @since 03/02/2018
 */
@Getter
public class HubBalancerConfig extends BalancerConfig<HubBalancer> {
    private final double targetRatio;

    public HubBalancerConfig(JSONObject data) {
        super(data, BalancerType.HUB);

        this.targetRatio = data.getDouble("targetRatio");
    }
}
