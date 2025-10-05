package com.onecraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onecraft.util.CodeColor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ComputerScreen extends HandledScreen<ComputerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/generic_54.png");

    public ComputerScreen(ComputerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // Make the background taller to fit all the buttons
        this.backgroundHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Create buttons for 4 code slots
        for (int i = 0; i < 4; i++) {
            int slotX = x + 44 + (i * 22);

            // Color buttons
            int colorButtonUpId = i;
            int colorButtonDownId = i + 4;
            this.addDrawableChild(ButtonWidget.builder(Text.literal("^"), (button) -> {
                client.interactionManager.clickButton(handler.syncId, colorButtonUpId);
            }).dimensions(slotX, y + 30, 20, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("v"), (button) -> {
                client.interactionManager.clickButton(handler.syncId, colorButtonDownId);
            }).dimensions(slotX, y + 70, 20, 20).build());

            // Digit buttons
            int digitButtonUpId = i + 8;
            int digitButtonDownId = i + 12;
             this.addDrawableChild(ButtonWidget.builder(Text.literal("^"), (button) -> {
                client.interactionManager.clickButton(handler.syncId, digitButtonUpId);
            }).dimensions(slotX, y + 100, 20, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("v"), (button) -> {
                client.interactionManager.clickButton(handler.syncId, digitButtonDownId);
            }).dimensions(slotX, y + 140, 20, 20).build());
        }

        // Enter button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Enter"), (button) -> {
            client.interactionManager.clickButton(handler.syncId, 16);
        }).dimensions(x + (backgroundWidth / 2) - 40, y + 170, 80, 20).build());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        // Draw the current code state
        for (int i = 0; i < 4; i++) {
            int slotX = 44 + (i * 22);

            // Draw color text
            CodeColor color = CodeColor.values()[handler.getColor(i)];
            context.drawText(this.textRenderer, color.asText(), slotX, 55, 0xFFFFFF, true);

            // Draw digit text
            int digit = handler.getDigit(i);
            context.drawText(this.textRenderer, Text.literal(String.valueOf(digit)), slotX + 7, 125, 0xFFFFFF, true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Corrected call to renderBackground
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}