package com.rudaco.searchcrafter.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class ButtonForText extends Button {

    int scale = 1;

    public ButtonForText(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage, pButton->{});
        this.active = false;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        this.drawText(pPoseStack, this.width);
    }






    private void drawText(PoseStack pPoseStack, int targetWidth) {

        int actualWidth = Minecraft.getInstance().font.width(this.getMessage());

        float scale = Math.min((float) targetWidth / actualWidth, 1);
        pPoseStack.scale(scale,scale,1);
        int adjustedX = (int) (x / scale);
        int adjustedY = (int) (y / scale);
        int adjustedHeight = (int)(this.height/scale);
        int offsetX = (int)(5/scale);
        int offsetY = (int)(8/scale);
        drawString(pPoseStack, Minecraft.getInstance().font, this.getMessage(), adjustedX + offsetX, adjustedY + (adjustedHeight-offsetY+(int)((1-scale)*20)) / 2, 0xFFFFFFFF);
        pPoseStack.scale(1/scale,1/scale,1);
    }
}
