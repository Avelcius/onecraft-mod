package com.onecraft.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class ItemDialogueHud implements HudRenderCallback {

    private static Text currentDialogue = null;
    private static int dialogueTicks = 0;
    private static int fadeInTicks = 10;
    private static int fadeOutTicks = 10;

    public static void showItemDialogue(Text text, int durationTicks) {
        currentDialogue = text;
        dialogueTicks = durationTicks;
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (dialogueTicks > 0 && currentDialogue != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            // Basic fade-in and fade-out logic
            float alpha = 1.0f;
            int totalDuration = fadeInTicks + fadeOutTicks;
            if (dialogueTicks > totalDuration) {
                 // Fully visible
            } else if (dialogueTicks > fadeOutTicks) {
                // Fade in
                alpha = 1.0f - (float)(dialogueTicks - fadeOutTicks) / fadeInTicks;
            } else {
                // Fade out
                alpha = (float)dialogueTicks / fadeOutTicks;
            }

            int color = ((int)(alpha * 255) << 24) | 0xFFFFFF;

            // Center the text
            int textWidth = client.textRenderer.getWidth(currentDialogue);
            int x = (screenWidth - textWidth) / 2;
            int y = screenHeight / 2;

            drawContext.drawTextWithShadow(client.textRenderer, currentDialogue, x, y, color);

            dialogueTicks--;
        }
    }
}