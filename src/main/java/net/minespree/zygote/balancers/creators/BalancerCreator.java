package net.minespree.zygote.balancers.creators;

import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.Balancer;
import net.minespree.zygote.config.BalancerConfig;
import org.json.JSONObject;

/**
 * @since 03/02/2018
 */
public interface BalancerCreator<T extends Balancer, C extends BalancerConfig<T>> {
    T create(BalancerManager manager, C config, Object... args);

    C parseConfig(JSONObject configData, Object... args);

    /**
     * Avoid using it unless it's not possible to use safe generic types.
     */
    @SuppressWarnings("unchecked")
    default T unsafeCreate(BalancerManager manager, BalancerConfig config, Object... args) {
        return create(manager, (C) config, args);
    }
}
