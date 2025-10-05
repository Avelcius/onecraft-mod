package com.onecraft.sound;

import com.onecraft.OneCraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier DOOR_UNLOCK_ID = new Identifier(OneCraft.MOD_ID, "door_unlock");
    public static SoundEvent DOOR_UNLOCK_EVENT = SoundEvent.of(DOOR_UNLOCK_ID);

    public static void registerSounds() {
        OneCraft.LOGGER.info("Registering Sounds for " + OneCraft.MOD_ID);
        Registry.register(Registries.SOUND_EVENT, DOOR_UNLOCK_ID, DOOR_UNLOCK_EVENT);
    }
}