package circuitlord.reactivemusic.mixin;

import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public class MusicTrackerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void reactivemusic$tick(CallbackInfo ci) {

        // Reactive Music drives the soundtrack itself, so vanilla's music manager
        // never gets to queue a track.
        ci.cancel();

    }

}
