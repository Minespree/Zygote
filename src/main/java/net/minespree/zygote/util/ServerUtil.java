package net.minespree.zygote.util;

import com.google.common.base.Preconditions;
import io.playpen.core.coordinator.network.LocalCoordinator;
import io.playpen.core.coordinator.network.Server;
import net.minespree.zygote.BalancerManager;
import net.minespree.zygote.balancers.Balancer;
import net.minespree.zygote.config.BalancerConfig;
import net.minespree.zygote.servers.LobbyStatus;
import org.apache.commons.codec.binary.Hex;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @since 02/02/2018
 */
public class ServerUtil {
    private static final Random RANDOM = new Random();

    private static final int PORT_MIN = 25600;
    private static final int PORT_MAX = 25900;

    public static <T> Map<Server, T> mapServerIds(Map<String, T> serverIdMap, Balancer balancer) {
        return serverIdMap.entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> balancer.getServer(entry.getKey()),
                Map.Entry::getValue
            ));
    }

    public static long getStartupTime(Server server) {
        return Long.parseLong(
            server.getProperties().getOrDefault("start_time", "0")
        );
    }
    public static Set<Server> getDeprovisionCandidates(Map<Server, LobbyStatus> statusMap, int maxCandidateCount) {
        return statusMap.entrySet().stream()
                .filter(entry -> entry.getValue().isEmpty())
                .sorted((entry1, entry2) -> {
                    long startup1 = getStartupTime(entry1.getKey());
                    long startup2 = getStartupTime(entry2.getKey());

                    return Long.compare(startup1, startup2);
                })
                .limit(maxCandidateCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public static OptionalInt findFreePort(LocalCoordinator coordinator) {
        int[] usedPorts = coordinator.getServers().values().stream()
                .map(server -> server.getProperties().get("port"))
                .mapToInt(Integer::parseInt)
                .sorted()
                .toArray();

        for (int port = PORT_MIN; port <= PORT_MAX; port++) {
            if (Arrays.binarySearch(usedPorts, port) < 0) {
                return OptionalInt.of(port);
            }
        }

        return OptionalInt.empty();
    }

    public static String generateServerName(String serverPrefix) {
        byte[] randomBytes = new byte[2];
        RANDOM.nextBytes(randomBytes);

        // TODO Check for collisions, could use last bytes of System.nanoTime()

        return serverPrefix + "-" + Hex.encodeHexString(randomBytes);
    }

    public static boolean isManaged(Server server) {
        Preconditions.checkNotNull(server);

        return BalancerManager.MANAGER_ID.equals(server.getProperties().getOrDefault("managed_by", ""));
    }
}
