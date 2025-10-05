package com.onecraft.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ItemDialogueScreen extends Screen {

    private final Text dialogueText;

    public ItemDialogueScreen(Text dialogueText) {
        super(Text.literal("Dialogue"));
        this.dialogueText = dialogueText;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Draw the dialogue text in the center of the screen
        context.drawCenteredTextWithShadow(this.client.textRenderer, this.dialogueText,
                this.width / 2,
                this.height / 2, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.close();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}