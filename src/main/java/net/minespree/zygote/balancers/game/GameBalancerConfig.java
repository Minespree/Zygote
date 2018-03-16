package net.minespree.zygote.balancers.game;

import lombok.Getter;
import net.minespree.zygote.balancers.BalancerType;
import net.minespree.zygote.config.BalancerConfig;
import org.json.JSONObject;

/**
 * @since 02/02/2018
 */
@Getter
public class GameBalancerConfig extends BalancerConfig<BaseGameBalancer> {
    private final int minimumSpare;
    private final int maximumSpare;

    public GameBalancerConfig(JSONObject data, BalancerType type) {
        super(data, type);

        this.minimumSpare = data.getInt("minimumSpare");
        this.maximumSpare = data.getInt("maximumSpare");
    }
}
