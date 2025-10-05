package com.onecraft.screen;

import com.onecraft.block.LockedDoorBlock;
import com.onecraft.block.ModBlocks;
import com.onecraft.state.CodeState;
import com.onecraft.util.CodeColor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

public class ComputerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    protected final PropertyDelegate propertyDelegate;
    private final BlockPos computerPos;
    private final World world;

    // Client-side constructor
    public ComputerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new ArrayPropertyDelegate(8), buf.readBlockPos());
    }

    // Server-side constructor
    public ComputerScreenHandler(int syncId, PlayerInventory playerInventory, PropertyDelegate propertyDelegate, BlockPos pos) {
        super(ModScreenHandlers.COMPUTER_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(0);
        this.world = playerInventory.player.getWorld();
        this.propertyDelegate = propertyDelegate;
        this.computerPos = pos;

        addProperties(propertyDelegate);
    }

    public int getColor(int index) {
        return this.propertyDelegate.get(index);
    }

    public int getDigit(int index) {
        return this.propertyDelegate.get(index + 4);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < 16) { // Handle code input buttons
            if (id < 4) { // cycle color up
                int currentColor = this.propertyDelegate.get(id);
                this.propertyDelegate.set(id, (currentColor + 1) % CodeColor.values().length);
            } else if (id < 8) { // cycle color down
                int colorIndex = id - 4;
                int currentColor = this.propertyDelegate.get(colorIndex);
                this.propertyDelegate.set(colorIndex, (currentColor - 1 + CodeColor.values().length) % CodeColor.values().length);
            } else if (id < 12) { // cycle digit up
                int digitIndex = id - 8;
                int currentDigit = this.propertyDelegate.get(digitIndex + 4);
                this.propertyDelegate.set(digitIndex + 4, (currentDigit + 1) % 10);
            } else { // cycle digit down
                int digitIndex = id - 12;
                int currentDigit = this.propertyDelegate.get(digitIndex + 4);
                this.propertyDelegate.set(digitIndex + 4, (currentDigit - 1 + 10) % 10);
            }
        } else if (id == 16) { // Enter button
            if (!world.isClient) {
                checkCodeAndOpenDoor();
            }
            return true;
        }
        return true;
    }

    private void checkCodeAndOpenDoor() {
        if (!(world instanceof ServerWorld serverWorld)) {
            return;
        }

        int[] enteredCode = new int[8];
        for (int i = 0; i < 8; i++) {
            enteredCode[i] = this.propertyDelegate.get(i);
        }

        CodeState state = CodeState.getServerState(serverWorld);
        int[] correctCode = state.getCode("first_door_code");

        if (Arrays.equals(enteredCode, correctCode)) {
            openNearestDoor();
        }
    }

    private void openNearestDoor() {
        // Search for the door in a 10 block radius
        for (BlockPos pos : BlockPos.iterate(computerPos.add(-10, -10, -10), computerPos.add(10, 10, 10))) {
            BlockState state = world.getBlockState(pos);
            if (state.isOf(ModBlocks.LOCKED_DOOR) && state.get(LockedDoorBlock.LOCKED)) {
                world.setBlockState(pos, state.with(LockedDoorBlock.LOCKED, false), 3);
                return; // Open only one door
            }
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}