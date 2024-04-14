package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class CustomButton extends Button {


    int x;
    int y;
    int width;
    int height;
    public float maxScale = 1;
    int bgColor;
    int textColor;

    public CustomButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, int bgColor, int textColor) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
        this.x = pX;
        y = pY;
        width = pWidth;
        height = pHeight;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        fill(pPoseStack,x, y, x + width, y + height, bgColor);

        int textwidth =  Minecraft.getInstance().font.width(this.getMessage().getString());
        int textheigth =  Minecraft.getInstance().font.lineHeight;
        float scale = 1f;
        int horizontalPadding = 2;
        if(textwidth > width - 2*horizontalPadding){
            scale = ((float)(width -2*horizontalPadding)/textwidth);
            textheigth = (int)(scale * Minecraft.getInstance().font.lineHeight);
        }
        pPoseStack.scale(scale,scale,1);
        drawString(pPoseStack, Minecraft.getInstance().font, getMessage().getString(), (int) ((this.x + horizontalPadding)/scale), (int) ((this.y + 1+(this.height - textheigth)/2)/scale), textColor);
        pPoseStack.popPose();

    }





    public void drawText(PoseStack pPoseStack, int targetWidth) {

        int actualWidth = Minecraft.getInstance().font.width(this.getMessage());
        // Calcula la relación entre la longitud deseada y la longitud actual
        float scale = Math.min((float) targetWidth / actualWidth, this.maxScale);
        pPoseStack.scale(scale,scale,1);
        int adjustedX = (int) (x / scale);
        int adjustedY = (int) (y / scale);
        int adjustedHeight = (int)(this.height/scale);
        int offsetX = (int)(5/scale);
        int offsetY = (int)(12/scale);
        drawString(pPoseStack, Minecraft.getInstance().font, this.getMessage(), adjustedX + offsetX, adjustedY + (adjustedHeight-offsetY+(int)((1-scale)*40)+(this.height/2)) / 2, 0xFFFFFFFF);
        pPoseStack.scale(1/scale,1/scale,1);
    }



}
