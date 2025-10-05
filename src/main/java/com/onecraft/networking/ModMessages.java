package com.onecraft.networking;

import com.onecraft.OneCraft;
import com.onecraft.networking.packet.MonologueCompleteC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier SHOW_NPC_DIALOGUE_ID = new Identifier(OneCraft.MOD_ID, "show_npc_dialogue");
    public static final Identifier SHOW_ITEM_DIALOGUE_ID = new Identifier(OneCraft.MOD_ID, "show_item_dialogue");
    public static final Identifier MONOLOGUE_COMPLETE_ID = new Identifier(OneCraft.MOD_ID, "monologue_complete");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(MONOLOGUE_COMPLETE_ID, MonologueCompleteC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        // This is just a placeholder, all registration happens on the client.
    }
}