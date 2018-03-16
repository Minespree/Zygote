package net.minespree.zygote;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.minespree.zygote.balancers.BalancerType;
import net.minespree.zygote.config.BalancerConfig;
import net.minespree.zygote.config.RedisConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

@Getter
public class ZygoteConfig {
    private final Set<BalancerConfig> balancerConfigs;
    private final RedisConfig redisConfig;
    private final int tickRate;

    public ZygoteConfig(JSONObject data) {
        this.balancerConfigs = Sets.newHashSet();
        parseBalancers(data.getJSONArray("balancers"));

        this.redisConfig = new RedisConfig(data.getJSONObject("redis"));
        this.tickRate = data.optInt("tickRate", 20);
    }

    private void parseBalancers(JSONArray array) {
        // It's a shame that JSONArray class is so primitive
        for (int i = 0; i < array.length(); i++) {
            JSONObject balancerConfig = array.getJSONObject(i);
            String typeName = balancerConfig.getString("type");

            BalancerType type = BalancerType.byName(typeName);

            if (type == null) {
                throw new IllegalArgumentException("Invalid balancer type " + typeName + ", please check your config");
            }

            BalancerConfig config = type.parseConfig(balancerConfig);
            balancerConfigs.add(config);
        }
    }
}
