package com.onecraft.screen;

import com.onecraft.block.entity.ComputerBlockEntity;
import com.onecraft.block.entity.ComputerState;
import com.onecraft.state.CodeState;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

public class ComputerScreenHandler extends ScreenHandler {
    protected final PropertyDelegate propertyDelegate;
    public final int[] colorSequence = new int[4];
    public ComputerState computerState;
    private final World world;
    private final BlockPos pos;

    // Client-side constructor
    public ComputerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, new ArrayPropertyDelegate(4), buf.readBlockPos());
        this.computerState = buf.readEnumConstant(ComputerState.class);
        if (this.computerState == ComputerState.AWAITING_CODE) {
            for(int i = 0; i < 4; i++) {
                this.colorSequence[i] = buf.readInt();
            }
        }
    }

    // Server-side constructor
    public ComputerScreenHandler(int syncId, PlayerInventory inventory, PropertyDelegate propertyDelegate, BlockPos pos) {
        super(ModScreenHandlers.COMPUTER_SCREEN_HANDLER, syncId);
        this.world = inventory.player.getWorld();
        this.pos = pos;
        this.computerState = this.world.getBlockEntity(pos) instanceof ComputerBlockEntity be ? be.getState() : ComputerState.AWAITING_CODE;
        checkDataCount(propertyDelegate, 4);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);
    }

    public int getDigit(int index) {
        return this.propertyDelegate.get(index);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (this.computerState != ComputerState.AWAITING_CODE) return false;

        if (id >= 0 && id < 4) { // Cycle digit up
            int currentDigit = this.propertyDelegate.get(id);
            this.propertyDelegate.set(id, (currentDigit + 1) % 10);
            return true;
        }
        if (id >= 4 && id < 8) { // Cycle digit down
            int digitIndex = id - 4;
            int currentDigit = this.propertyDelegate.get(digitIndex);
            this.propertyDelegate.set(digitIndex, (currentDigit - 1 + 10) % 10);
            return true;
        }
        if (id == 8) { // Enter button
            if (!world.isClient) {
                checkCodeAndChangeState((ServerWorld) world, player);
            }
            return true;
        }
        return false;
    }

    private void checkCodeAndChangeState(ServerWorld serverWorld, PlayerEntity player) {
        BlockEntity be = serverWorld.getBlockEntity(pos);
        if (!(be instanceof ComputerBlockEntity computerBE)) return;

        CodeState state = CodeState.getServerState(serverWorld);
        int[] correctCode = state.getCode("first_door_code");

        boolean match = true;
        for (int i = 0; i < 4; i++) {
            if (correctCode[i+4] != this.propertyDelegate.get(i)) {
                match = false;
                break;
            }
        }

        if (match) {
            computerBE.setState(ComputerState.SHOWING_MONOLOGUE);
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).closeHandledScreen();
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.world.getBlockState(this.pos).isOf(com.onecraft.block.ModBlocks.COMPUTER_BLOCK) && player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}