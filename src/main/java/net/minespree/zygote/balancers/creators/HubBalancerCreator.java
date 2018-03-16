package net.minespree.zygote.balancers.creators;

import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.hub.HubBalancer;
import net.minespree.zygote.balancers.hub.HubBalancerConfig;
import org.json.JSONObject;

/**
 * @since 03/02/2018
 */
public class HubBalancerCreator implements BalancerCreator<HubBalancer, HubBalancerConfig> {
    private static final HubBalancerCreator INSTANCE = new HubBalancerCreator();

    public static HubBalancerCreator get() {
        return INSTANCE;
    }

    private HubBalancerCreator() {}

    @Override
    public HubBalancer create(BalancerManager manager, HubBalancerConfig config, Object... args) {
        return new HubBalancer(manager, config);
    }

    @Override
    public HubBalancerConfig parseConfig(JSONObject configData, Object... args) {
        return new HubBalancerConfig(configData);
    }
}
