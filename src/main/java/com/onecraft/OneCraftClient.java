package com.onecraft;

import com.onecraft.client.hud.NpcDialogueHud;
import com.onecraft.networking.ModMessages;
import com.onecraft.networking.packet.ShowItemDialogueS2CPacket;
import com.onecraft.networking.packet.ShowNpcDialogueS2CPacket;
import com.onecraft.screen.ComputerScreen;
import com.onecraft.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class OneCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.COMPUTER_SCREEN_HANDLER, ComputerScreen::new);

        // Register S2C Packets
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.SHOW_NPC_DIALOGUE_ID, ShowNpcDialogueS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModMessages.SHOW_ITEM_DIALOGUE_ID, ShowItemDialogueS2CPacket::receive);

        // Register HUD for NPC dialogues
        HudRenderCallback.EVENT.register(new NpcDialogueHud());
    }
}