package net.minespree.zygote.balancers.creators;

import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.BalancerType;
import net.minespree.zygote.balancers.game.BaseGameBalancer;
import net.minespree.zygote.balancers.game.FastGameBalancer;
import net.minespree.zygote.balancers.game.GameBalancer;
import net.minespree.zygote.balancers.game.GameBalancerConfig;
import org.json.JSONObject;

/**
 * @since 03/02/2018
 */
public class GameBalancerCreator implements BalancerCreator<BaseGameBalancer, GameBalancerConfig> {
    private static final GameBalancerCreator INSTANCE = new GameBalancerCreator();

    public static GameBalancerCreator get() {
        return INSTANCE;
    }

    private GameBalancerCreator() {}

    @Override
    public BaseGameBalancer create(BalancerManager manager, GameBalancerConfig config, Object... args) {
        if (isFast(args)) {
            return new FastGameBalancer(manager, config);
        }

        return new GameBalancer(manager, config);
    }

    @Override
    public GameBalancerConfig parseConfig(JSONObject configData, Object... args) {
        return new GameBalancerConfig(configData, getType(args));
    }

    private static boolean isFast(Object[] args) {
        return args.length != 0 && args[0].equals(true);
    }

    private static BalancerType getType(Object[] args) {
        return isFast(args) ? BalancerType.FAST_GAME : BalancerType.GAME;
    }
}
