package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class SmallCustomButton extends CustomButton{
    public SmallCustomButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, int textureId) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, textureId);
    }

    @Override
    public void drawText(PoseStack pPoseStack, int targetWidth) {

        int actualWidth = Minecraft.getInstance().font.width(this.getMessage())-5;
        // Calcula la relación entre la longitud deseada y la longitud actual
        float scale = Math.min((float) targetWidth / actualWidth, this.maxScale);
        pPoseStack.scale(scale,scale,1);
        int adjustedX = (int) (x / scale);
        int adjustedY = (int) (y / scale);
        int adjustedHeight = (int)(this.height/scale);
        int offsetX = (int)(5/scale);
        int offsetY = (int)(-20/scale);
        drawString(pPoseStack, Minecraft.getInstance().font, this.getMessage(), adjustedX + offsetX, adjustedY + (adjustedHeight-offsetY-(int)((1-scale)*100)+(this.height/2)) / 2, 0xFFFFFFFF);
        pPoseStack.scale(1/scale,1/scale,1);
    }
}
