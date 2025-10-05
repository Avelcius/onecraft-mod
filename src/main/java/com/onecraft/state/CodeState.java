package com.onecraft.state;

import com.onecraft.util.CodeColor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.*;

public class CodeState extends PersistentState {

    public Map<String, int[]> codes = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound codesNbt = new NbtCompound();
        codes.forEach((id, code) -> {
            codesNbt.putIntArray(id, code);
        });
        nbt.put("codes", codesNbt);
        return nbt;
    }

    public static CodeState createFromNbt(NbtCompound tag) {
        CodeState state = new CodeState();
        NbtCompound codesNbt = tag.getCompound("codes");
        for (String key : codesNbt.getKeys()) {
            state.codes.put(key, codesNbt.getIntArray(key));
        }
        return state;
    }

    public static CodeState getServerState(ServerWorld world) {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        CodeState state = persistentStateManager.getOrCreate(CodeState::createFromNbt, CodeState::new, "onecraft_codes");
        state.markDirty();
        return state;
    }

    public boolean generateCode(String id) {
        if (codes.containsKey(id)) {
            return false; // Code already exists
        }

        // Create a list of the 4 unique colors and shuffle them
        List<CodeColor> uniqueColors = new ArrayList<>(Arrays.asList(CodeColor.values()));
        Collections.shuffle(uniqueColors);

        Random random = new Random();
        int[] newCode = new int[8];
        for (int i = 0; i < 4; i++) {
            newCode[i] = uniqueColors.get(i).ordinal(); // Assign the unique shuffled color
            newCode[i + 4] = random.nextInt(10); // Random digit 0-9
        }
        codes.put(id, newCode);
        markDirty();
        return true;
    }

    public int[] getCode(String id) {
        return codes.getOrDefault(id, new int[8]); // Return empty code if not found
    }
}