package com.onecraft.item;

import com.onecraft.block.ModBlocks;
import com.onecraft.networking.ModMessages;
import com.onecraft.state.CodeState;
import com.onecraft.util.CodeColor;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RemoteControlItem extends Item {
    public RemoteControlItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            BlockHitResult hitResult = (BlockHitResult) user.raycast(5.0D, 1.0F, false);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state.isOf(ModBlocks.SPECIAL_WINDOW)) {
                CodeState codeState = CodeState.getServerState((ServerWorld) world);
                int[] code = codeState.getCode("first_door_code");

                PacketByteBuf buf = PacketByteBufs.create();
                if (code.length == 8 && (code[0] != 0 || code[1] != 0 || code[2] != 0 || code[3] != 0)) {
                    buf.writeText(formatCode(code));
                } else {
                    buf.writeText(Text.literal("No code is currently active. Use /onecraft generatecode first_door_code"));
                }

                // Send the packet to the client to open the modal screen
                ServerPlayNetworking.send(serverPlayer, ModMessages.SHOW_ITEM_DIALOGUE_ID, buf);

                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    private Text formatCode(int[] code) {
        MutableText formattedText = Text.empty();
        for (int i = 0; i < 4; i++) {
            CodeColor color = CodeColor.values()[code[i]];
            int digit = code[i + 4];

            MutableText part = Text.literal(String.valueOf(digit)).formatted(color.getFormatting());

            formattedText.append(part);
            if (i < 3) {
                formattedText.append(Text.literal(" "));
            }
        }
        return formattedText;
    }
}