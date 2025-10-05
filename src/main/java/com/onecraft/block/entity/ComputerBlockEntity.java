package com.onecraft.block.entity;

import com.onecraft.screen.ComputerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    private final int[] code = new int[8]; // This will hold the player's input
    protected final PropertyDelegate propertyDelegate;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPUTER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return ComputerBlockEntity.this.code[index];
            }

            @Override
            public void set(int index, int value) {
                ComputerBlockEntity.this.code[index] = value;
            }

            @Override
            public int size() {
                return 8;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.onecraft.computer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ComputerScreenHandler(syncId, playerInventory, this.propertyDelegate, this.pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("onecraft.computer_code")) {
            int[] loadedCode = nbt.getIntArray("onecraft.computer_code");
            if(loadedCode.length == 8) {
                System.arraycopy(loadedCode, 0, this.code, 0, 8);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putIntArray("onecraft.computer_code", this.code);
    }
}