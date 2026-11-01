package com.snipe;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class SnipeMod implements ClientModInitializer {
    public static KeyMapping openKey;

    @Override public void onInitializeClient() {
        Config.load(); Market.load(); Purchases.load();
        KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("snipe", "main"));
        openKey = KeyMappingHelper.registerKeyMapping(
            new KeyMapping("key.snipe.open", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, cat));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.consumeClick()) Compat.open(mc, new SnipeScreen());
            Sniper.tick(mc);
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> { Config.save(); Market.save(); Purchases.save(); });
    }
}
