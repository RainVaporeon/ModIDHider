package com.spiritlight.modidhider.mixins;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Mixin(value = FMLHandshakeMessage.ModList.class, remap = false)
public class MixinModList extends FMLHandshakeMessage {
    /**
     * Our forge's modTags, this contains a KV pair of all mod-id.
     * They are reserved as a pair of ModID:ModName, so key=id which is
     * what we wish to remove.
     */
    @Shadow(remap = false)
    private Map<String, String> modTags;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"), remap = false)
    public void ModList(List<ModContainer> container, CallbackInfo ci) {
        if(!Minecraft.getMinecraft().isIntegratedServerRunning()) {
            // Wiping all mods off, probably not good at some cases
            final List<String> commonMods = Arrays.asList("fml", "forge", "mcp");
            // InvGui, RMT, MythicDrops, HICHunt, [Reserved]
            final List<String> spirits = Arrays.asList("origasm", "rmt", "mifik", "orihunt", "examplemod");

            // Use "!commonMods" to remove all that is *not* in the commonMods list
            // Use "spirits" to remove only ori-related mods
            modTags.entrySet().removeIf(modid -> spirits.stream().anyMatch(id -> id.equalsIgnoreCase(modid.getKey())));
        }
    }
}