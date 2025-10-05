package com.onecraft.block;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LockedDoorBlock extends DoorBlock {
    public static final BooleanProperty LOCKED = BooleanProperty.of("locked");

    public LockedDoorBlock(Settings settings) {
        // Updated constructor to include BlockSetType, required in modern versions.
        super(settings, BlockSetType.OAK);
        this.setDefaultState(this.stateManager.getDefaultState().with(LOCKED, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LOCKED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(LOCKED)) {
            return ActionResult.FAIL; // If the door is locked, do nothing.
        }
        return super.onUse(state, world, pos, player, hand, hit); // Otherwise, behave like a normal door.
    }
}