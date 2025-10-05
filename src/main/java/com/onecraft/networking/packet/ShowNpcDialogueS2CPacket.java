package com.onecraft.networking.packet;

import com.onecraft.client.hud.NpcDialogueHud;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class ShowNpcDialogueS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        Text dialogueText = buf.readText();
        String portraitId = buf.readString();
        int duration = buf.readInt();

        // Show the dialogue on the NPC HUD
        client.execute(() -> {
            NpcDialogueHud.showNpcDialogue(dialogueText, portraitId, duration);
        });
    }
}