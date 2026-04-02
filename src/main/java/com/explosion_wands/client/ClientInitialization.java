package com.explosion_wands.client;

import com.explosion_wands.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.core.Registry;

public class ClientInitialization implements ClientModInitializer {
    //Needed since we need a renderer registered for the custom entities. Null otherwise, hard crashes
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(
                ModEntities.CUSTOM_TNT,
                //Renders the CustomTnt like the vanilla TNT
                TntRenderer::new);
    }
}
