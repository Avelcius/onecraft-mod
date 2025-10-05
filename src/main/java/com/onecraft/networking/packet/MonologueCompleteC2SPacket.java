package com.onecraft.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import com.onecraft.block.entity.ComputerBlockEntity;

public class MonologueCompleteC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        ServerWorld world = player.getServerWorld();

        server.execute(() -> {
            if (world.getBlockEntity(pos) instanceof ComputerBlockEntity computer) {
                computer.resolvePuzzle(world, pos, player);
            }
        });
    }
}