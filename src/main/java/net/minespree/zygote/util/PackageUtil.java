package net.minespree.zygote.util;

import com.google.common.base.Preconditions;
import io.playpen.core.coordinator.network.LocalCoordinator;
import io.playpen.core.coordinator.network.Network;
import io.playpen.core.p3.P3Package;
import net.minespree.zygote.config.ServerTemplate;

/**
 * @since 02/02/2018
 */
public class PackageUtil {
    public static P3Package getPackage(String packageId) {
        return Network.get().getPackageManager().resolve(packageId, "promoted");
    }

    public static boolean isValidPackage(P3Package p3Package) {
        return p3Package != null && p3Package.isResolved();
    }

    public static void checkValid(P3Package p3Package, String errorMessage) {
        Preconditions.checkArgument(isValidPackage(p3Package), errorMessage);
    }

    public static LocalCoordinator findCoordinator(P3Package p3Package) {
        Preconditions.checkNotNull(p3Package);

        return Network.get().selectCoordinator(p3Package);
    }
}
