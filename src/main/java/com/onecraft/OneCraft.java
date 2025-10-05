package com.onecraft;

import com.onecraft.block.ModBlocks;
import com.onecraft.block.entity.ModBlockEntities;
import com.onecraft.command.GenerateCodeCommand;
import com.onecraft.item.ModItems;
import com.onecraft.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneCraft implements ModInitializer {
    public static final String MOD_ID = "onecraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello from OneCraft!");
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        // Register Commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            GenerateCodeCommand.register(dispatcher, environment.dedicated);
        });
    }
}