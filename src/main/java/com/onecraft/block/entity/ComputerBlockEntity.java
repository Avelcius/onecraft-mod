package com.onecraft.block.entity;

import com.onecraft.block.LockedDoorBlock;
import com.onecraft.networking.ModMessages;
import com.onecraft.screen.ComputerScreenHandler;
import com.onecraft.sound.ModSounds;
import com.onecraft.state.CodeState;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import com.onecraft.OneCraft;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ComputerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    private final int[] digits = new int[4];
    private ComputerState currentState = ComputerState.AWAITING_CODE;
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

    public ComputerState getState() {
        return this.currentState;
    }

    public void setState(ComputerState state) {
        this.currentState = state;
        if (this.world != null) {
            this.world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
        markDirty();
    }

    public void resolvePuzzle(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
        if (this.currentState != ComputerState.SHOWING_MONOLOGUE) {
            return;
        }

        setState(ComputerState.SOLVED);

        // Find and unlock the door
        for (BlockPos checkPos : BlockPos.iterate(pos.add(-10, -10, -10), pos.add(10, 10, 10))) {
            BlockState checkState = world.getBlockState(checkPos);
            if (checkState.getBlock() instanceof LockedDoorBlock) {
                world.setBlockState(checkPos, checkState.with(LockedDoorBlock.LOCKED, false));
                world.playSound(null, checkPos, ModSounds.DOOR_UNLOCK_EVENT, SoundCategory.BLOCKS, 1f, 1f);
                break; // Assume one door per computer
            }
        }

        // Create file on desktop
        try {
            String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
            File clueFile = new File(documentsPath, "ONESHOT_CLUE.txt");
            try (FileWriter writer = new FileWriter(clueFile)) {
                writer.write("There is no escape.");
            }
        } catch (IOException e) {
            OneCraft.LOGGER.error("Failed to create clue file", e);
        }

        // Send dialogue to player
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeText(Text.translatable("dialogue.onecraft.door_unlocked"));
        ServerPlayNetworking.send(player, ModMessages.SHOW_ITEM_DIALOGUE_ID, buf);
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
        buf.writeEnumConstant(this.currentState);

        if (this.currentState == ComputerState.AWAITING_CODE) {
            CodeState state = CodeState.getServerState((ServerWorld) this.world);
            int[] code = state.getCode("first_door_code");
            for (int i = 0; i < 4; i++) {
                buf.writeInt(code[i]);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.currentState = ComputerState.valueOf(nbt.getString("onecraft.computer_state"));
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
        nbt.putString("onecraft.computer_state", this.currentState.name());
        nbt.putIntArray("onecraft.computer_digits", this.digits);
    }
}