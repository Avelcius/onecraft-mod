package com.onecraft.block.entity;

import com.onecraft.screen.ComputerScreenHandler;
import com.onecraft.state.CodeState;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    private final int[] digits = new int[4]; // This will hold the player's input digits
    protected final PropertyDelegate propertyDelegate;

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPUTER_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return ComputerBlockEntity.this.digits[index];
            }

            @Override
            public void set(int index, int value) {
                ComputerBlockEntity.this.digits[index] = value;
            }

            @Override
            public int size() {
                return 4;
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
        // We now pass the property delegate for the digits to the screen handler
        return new ComputerScreenHandler(syncId, playerInventory, this.propertyDelegate, this.pos);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        // Fetch the global code and write the COLOR part to the buffer for the client to use
        CodeState state = CodeState.getServerState((ServerWorld) this.world);
        int[] code = state.getCode("first_door_code");
        for (int i = 0; i < 4; i++) {
            buf.writeInt(code[i]);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("onecraft.computer_digits")) {
            int[] loadedCode = nbt.getIntArray("onecraft.computer_digits");
            if(loadedCode.length == 4) {
                System.arraycopy(loadedCode, 0, this.digits, 0, 4);
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putIntArray("onecraft.computer_digits", this.digits);
    }
}