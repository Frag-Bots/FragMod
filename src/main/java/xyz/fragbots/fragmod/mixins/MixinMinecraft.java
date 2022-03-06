package xyz.fragbots.fragmod.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// You have to use Java for Mixins because it's very janky in Kotlin and you will have issues.
@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = { "startGame" }, at = @At("RETURN"))
    private void startGame(CallbackInfo ci) {
        System.out.println("Successfully injected into startGame.");
    }
}
