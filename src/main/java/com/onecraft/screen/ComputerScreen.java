package com.onecraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.onecraft.block.entity.ComputerState;
import com.onecraft.networking.ModMessages;
import com.onecraft.util.CodeColor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ComputerScreen extends HandledScreen<ComputerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/dispenser.png");

    public ComputerScreen(ComputerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        if (handler.computerState == ComputerState.AWAITING_CODE) {
            this.backgroundHeight = 166;
        }
    }

    @Override
    protected void init() {
        super.init();
        if (handler.computerState == ComputerState.AWAITING_CODE) {
            initCodeScreen();
        } else if (handler.computerState == ComputerState.SHOWING_MONOLOGUE) {
            initMonologueScreen();
        }
    }

    private void initCodeScreen() {
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        for (int i = 0; i < 4; i++) {
            int slotX = x + 44 + (i * 22);
            int digitButtonUpId = i;
            int digitButtonDownId = i + 4;
            this.addDrawableChild(ButtonWidget.builder(Text.literal("^"), (button) -> client.interactionManager.clickButton(handler.syncId, digitButtonUpId)).dimensions(slotX, y + 60, 20, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("v"), (button) -> client.interactionManager.clickButton(handler.syncId, digitButtonDownId)).dimensions(slotX, y + 82, 20, 20).build());
        }
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Enter"), (button) -> client.interactionManager.clickButton(handler.syncId, 8)).dimensions(x + (backgroundWidth / 2) - 40, y + 110, 80, 20).build());
    }

    private void initMonologueScreen() {
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Continue"), (button) -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.getPos());
                    ClientPlayNetworking.send(ModMessages.MONOLOGUE_COMPLETE_ID, buf);
                    this.close();
                })
                .dimensions(this.width / 2 - 50, this.height - 30, 100, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        if (handler.computerState == ComputerState.SHOWING_MONOLOGUE) {
            Text monologueText = Text.translatable("computer.first_door.monologue");
            List<OrderedText> wrappedText = textRenderer.wrapLines(monologueText, this.width - 50);
            int yPos = (this.height - (wrappedText.size() * this.textRenderer.fontHeight)) / 2;

            for (OrderedText line : wrappedText) {
                context.drawCenteredTextWithShadow(this.textRenderer, line, this.width / 2, yPos, 0xFFFFFF);
                yPos += this.textRenderer.fontHeight;
            }
        }

        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        if (handler.computerState == ComputerState.AWAITING_CODE) {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TEXTURE);
            int x = (width - backgroundWidth) / 2;
            int y = (height - backgroundHeight) / 2;
            context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        if (handler.computerState == ComputerState.AWAITING_CODE) {
            context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
            for (int i = 0; i < 4; i++) {
                int slotX = 44 + (i * 22);
                CodeColor color = CodeColor.values()[handler.colorSequence[i]];
                int colorInt = color.getFormatting().getColorValue() != null ? color.getFormatting().getColorValue() : 0xFFFFFF;
                context.fill(slotX, 40, slotX + 20, 60, 0xFF000000 | colorInt);
                int digit = handler.getDigit(i);
                String digitText = String.valueOf(digit);
                int textWidth = this.textRenderer.getWidth(digitText);
                context.drawText(this.textRenderer, Text.literal(digitText), slotX + (20 - textWidth) / 2, 46, 0xFFFFFF, true);
            }
        }
    }
}