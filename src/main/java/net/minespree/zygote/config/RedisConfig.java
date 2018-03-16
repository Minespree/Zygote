package net.minespree.zygote.config;

import lombok.Value;
import org.json.JSONObject;

/**
 * @since 02/02/2018
 */
@Value
public class RedisConfig {
    private final String host;
    private final int port;
    private final String password;
    private final int maxConnections;

    public RedisConfig(JSONObject object) {
        this.host = object.getString("host");
        this.port = object.getInt("port");
        String redisPassword = object.optString("password");

        this.password = redisPassword == null || redisPassword.isEmpty() ? null : redisPassword;
        this.maxConnections = object.optInt("maxConnections", 8);
    }
}
