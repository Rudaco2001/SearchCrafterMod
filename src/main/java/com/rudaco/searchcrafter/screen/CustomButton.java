package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sun.jna.platform.unix.X11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class CustomButton extends Button {


    int x;
    int y;
    int width;
    int height;
    public float maxScale = 1;
    int textureId;

    public CustomButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, int textureId) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
        this.x = pX;
        y = pY;
        width = pWidth;
        height = pHeight;
        this.textureId = textureId;

    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        drawBorderedRect(pPoseStack, this.x, this.y, this.x + this.width, this.y + this.height, 1,  0xFF000000);

        drawImage(pPoseStack, this.x, this.y, 5, 5, this.height-10, this.height-10, this.textureId );
        this.drawText(pPoseStack,this.width-10);
    }


    public void drawBorderedRect(PoseStack poseStack, int x, int y, int x2, int y2, int thickness, int border) {
        // Dibuja solo el borde exterior
        fill(poseStack, x, y, x + thickness, y2, border); // Izquierda
        fill(poseStack, x2 - thickness, y, x2, y2, border); // Derecha
        fill(poseStack, x + thickness, y, x2 - thickness, y + thickness, border); // Superior
        fill(poseStack, x + thickness, y2 - thickness, x2 - thickness, y2, border); // Inferior
    }

    private void drawImage(PoseStack poseStack, int x, int y, int u, int v, int width, int height, int textureId) {
        //RenderSystem.bindTexture(textureId);
        //SearchCrafterTableScreen.blit(poseStack, x, y, u, v, width, height, width, height);
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
