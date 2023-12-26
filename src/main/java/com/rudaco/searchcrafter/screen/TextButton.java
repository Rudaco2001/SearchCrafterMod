package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class TextButton extends Button {

    float maxScale = 1;

    public TextButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, float maxScaleText) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
        this.maxScale = maxScaleText;
    }

    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(pPoseStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(pPoseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);
        int j = getFGColor();
        this.drawText(pPoseStack, this.width);

    }




    private void drawText(PoseStack pPoseStack, int targetWidth) {

        int actualWidth = Minecraft.getInstance().font.width(this.getMessage());
        // Calcula la relación entre la longitud deseada y la longitud actual
        float scale = Math.min((float) targetWidth / actualWidth, this.maxScale);
        pPoseStack.scale(scale,scale,1);
        int adjustedX = (int) (x / scale);
        int adjustedY = (int) (y / scale);
        int adjustedHeight = (int)(this.height/scale);
        int offsetX = (int)(3/scale);
        int offsetY = (int)(8/scale);
        drawString(pPoseStack, Minecraft.getInstance().font, this.getMessage(), adjustedX + offsetX, adjustedY + (adjustedHeight-offsetY+(int)((1-scale)*20)) / 2, 0xFFFFFFFF);
        pPoseStack.scale(1/scale,1/scale,1);
    }

}
