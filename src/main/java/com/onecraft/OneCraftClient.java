package com.onecraft;

import com.onecraft.screen.ComputerScreen;
import com.onecraft.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class OneCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.COMPUTER_SCREEN_HANDLER, ComputerScreen::new);
    }
}