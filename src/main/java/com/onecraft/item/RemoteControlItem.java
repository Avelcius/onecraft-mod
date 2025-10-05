package com.onecraft.item;

import com.onecraft.block.ModBlocks;
import com.onecraft.state.CodeState;
import com.onecraft.util.CodeColor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            BlockHitResult hitResult = (BlockHitResult) user.raycast(5.0D, 1.0F, false);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state.isOf(ModBlocks.SPECIAL_WINDOW)) {
                CodeState codeState = CodeState.getServerState(serverWorld);
                // We hardcode the ID for the first puzzle. This can be made dynamic later if needed.
                int[] code = codeState.getCode("first_door_code");

                if (code.length == 8 && (code[0] != 0 || code[1] != 0 || code[2] != 0 || code[3] != 0) ) { // Check if code is not empty
                    user.sendMessage(formatCode(code), false);
                } else {
                    user.sendMessage(Text.literal("No code is currently active. Use /onecraft generatecode first_door_code"), false);
                }
                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    private Text formatCode(int[] code) {
        StringBuilder sb = new StringBuilder("Code: ");
        for (int i = 0; i < 4; i++) {
            CodeColor color = CodeColor.values()[code[i]];
            int digit = code[i + 4];
            sb.append(String.format("[%s-%d]", color.name(), digit));
            if (i < 3) {
                sb.append(" ");
            }
        }
        return Text.literal(sb.toString());
    }
}