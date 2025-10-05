package com.onecraft.networking.packet;

import com.onecraft.screen.ItemDialogueScreen;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class ShowItemDialogueS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        Text dialogueText = buf.readText();

        // Open the new modal dialogue screen
        client.execute(() -> {
            client.setScreen(new ItemDialogueScreen(dialogueText));
        });
    }
}