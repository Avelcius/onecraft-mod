package com.onecraft.client.hud;

import com.onecraft.OneCraft;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NpcDialogueHud implements HudRenderCallback {

    private static Text currentDialogue = null;
    private static Identifier currentPortrait = null;
    private static int dialogueTicks = 0;

    public static void showNpcDialogue(Text text, String portraitId, int durationTicks) {
        currentDialogue = text;
        if (portraitId != null && !portraitId.isEmpty()) {
            currentPortrait = new Identifier(OneCraft.MOD_ID, "textures/gui/portraits/" + portraitId + ".png");
        } else {
            currentPortrait = null;
        }
        dialogueTicks = durationTicks;
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (dialogueTicks > 0 && currentDialogue != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            int boxHeight = 50;
            int boxY = screenHeight - boxHeight - 10;

            // Draw the dialogue box background
            drawContext.fill(10, boxY, screenWidth - 10, screenHeight - 10, 0x80000000); // Semi-transparent black

            int textX = 20;
            int textY = boxY + 10;

            // Draw the portrait if it exists
            if (currentPortrait != null) {
                int portraitSize = 40;
                int portraitX = 15;
                int portraitY = boxY + 5;

                // Draw the portrait texture
                drawContext.drawTexture(currentPortrait, portraitX, portraitY, 0, 0, portraitSize, portraitSize, portraitSize, portraitSize);

                // Indent the text to make space for the portrait
                textX += portraitSize + 10;
            }

            // Draw the text, wrapped if necessary
            drawContext.drawTextWithShadow(client.textRenderer, currentDialogue, textX, textY, 0xFFFFFF);

            dialogueTicks--;
        }
    }
}