package com.onecraft.screen;

import com.onecraft.OneCraft;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ComputerScreenHandler> COMPUTER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(OneCraft.MOD_ID, "computer_screen"),
                    new ExtendedScreenHandlerType<>(ComputerScreenHandler::new));


    public static void registerScreenHandlers() {
        OneCraft.LOGGER.info("Registering Screen Handlers for " + OneCraft.MOD_ID);
    }
}