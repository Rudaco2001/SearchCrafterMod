package com.rudaco.searchcrafter.screen;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class BorderButton extends Button {

    public BorderButton(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage, pButton -> {});
        this.active = false;
    }

    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        drawBorderedRect(pPoseStack, this.x, this.y, this.x + this.width, this.y + this.height, 1,  0xFF000000);
    }
    public void drawBorderedRect(PoseStack poseStack, int x, int y, int x2, int y2, int thickness, int border) {
        // Dibuja solo el borde exterior
        fill(poseStack, x, y, x + thickness, y2, border); // Izquierda
        fill(poseStack, x2 - thickness, y, x2, y2, border); // Derecha
        fill(poseStack, x + thickness, y, x2 - thickness, y + thickness, border); // Superior
        fill(poseStack, x + thickness, y2 - thickness, x2 - thickness, y2, border); // Inferior
    }
}
