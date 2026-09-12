package circuitlord.reactivemusic.quilt;

import circuitlord.reactivemusic.platform.PlatformHelper;
import net.fabricmc.api.EnvType;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.loader.api.minecraft.MinecraftQuiltLoader;

import java.nio.file.Path;

public class QuiltPlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return QuiltLoader.isModLoaded(modId);
    }

    @Override
    public Path getGameDir() {
        return QuiltLoader.getGameDir();
    }

    @Override
    public Path getConfigDir() {
        return QuiltLoader.getConfigDir();
    }

    @Override
    public boolean isDedicatedServer() {
        return MinecraftQuiltLoader.getEnvironmentType() == EnvType.SERVER;
    }
}
