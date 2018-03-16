package net.minespree.zygote.balancers;

import lombok.Getter;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.creators.BalancerCreator;
import net.minespree.zygote.balancers.creators.GameBalancerCreator;
import net.minespree.zygote.balancers.creators.HubBalancerCreator;
import net.minespree.zygote.config.BalancerConfig;
import org.json.JSONObject;

/**
 * @since 02/02/2018
 */
@Getter
public enum BalancerType {
    GAME("game", GameBalancerCreator.get()),
    FAST_GAME("fastGame", GameBalancerCreator.get()),
    HUB("hub", HubBalancerCreator.get());

    private final String name;
    private final BalancerCreator<?, ?> creator;
    private final Object[] creatorArgs;

    BalancerType(String name, BalancerCreator<?, ?> creator, Object... creatorArgs) {
        this.name = name;
        this.creator = creator;
        this.creatorArgs = creatorArgs;
    }

    public BalancerConfig parseConfig(JSONObject configData) {
        return creator.parseConfig(configData, creatorArgs);
    }

    public Balancer createBalancer(BalancerManager manager, BalancerConfig config) {
        return creator.unsafeCreate(manager, config, creatorArgs);
    }

    public static BalancerType byName(String name) {
        for (BalancerType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
