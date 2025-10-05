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
    // I will use a generic texture for the background.
    // The colored squares will be drawn with simple fill commands.
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/dispenser.png");

    public ComputerScreen(ComputerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Create buttons for the 4 digit slots
        for (int i = 0; i < 4; i++) {
            int slotX = x + 44 + (i * 22);

            // Digit buttons
            int digitButtonUpId = i;
            int digitButtonDownId = i + 4;
            this.addDrawableChild(ButtonWidget.builder(Text.literal("^"), (button) -> {
                client.interactionManager.clickButton(handler.syncId, digitButtonUpId);
            }).dimensions(slotX, y + 60, 20, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("v"), (button) -> {
                client.interactionManager.clickButton(handler.syncId, digitButtonDownId);
            }).dimensions(slotX, y + 82, 20, 20).build());
        }

        // Enter button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Enter"), (button) -> {
            client.interactionManager.clickButton(handler.syncId, 8);
        }).dimensions(x + (backgroundWidth / 2) - 40, y + 110, 80, 20).build());
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
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);

        // Draw the colored squares and the current digits
        for (int i = 0; i < 4; i++) {
            int slotX = 44 + (i * 22);

            // Draw colored square
            CodeColor color = CodeColor.values()[handler.colorSequence[i]];
            int colorInt = color.getFormatting().getColorValue() != null ? color.getFormatting().getColorValue() : 0xFFFFFF;
            context.fill(slotX, 40, slotX + 20, 60, 0xFF000000 | colorInt); // Draw solid color square

            // Draw digit text below the square
            int digit = handler.getDigit(i);
            String digitText = String.valueOf(digit);
            int textWidth = this.textRenderer.getWidth(digitText);
            context.drawText(this.textRenderer, Text.literal(digitText), slotX + (20 - textWidth) / 2, 46, 0xFFFFFF, true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}