package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadingWidget extends AbstractWidget {



    float scale = 0.5f;
    PageController controller;
    CustomButton cancelButton;
    AtomicInteger number = new AtomicInteger(1);
    int currentNumber = 0;

    int state = 0;
    public LoadingWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, PageController controller) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.controller = controller;
        this.active = false;
    }
    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if(!visible) return;
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        fill(pPoseStack, this.x, this.y, this.x+width, y+height, 0xFF000000);
        drawBorderedRect(pPoseStack, x+2, y+2, x+width-2, y+height-2, 1, 0xFFFFFFFF);
        pPoseStack.pushPose();
        pPoseStack.scale(scale,scale,1);
        if(state == 0){
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Calculating Craft"), (int)((x + 5)/scale), (int)((y + 5)/scale), 0xFFFFFFFF);
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Progress:"), (int)((x + 5)/scale), (int)((y + 11)/scale), 0xFFFFFFFF);
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal(currentNumber + "/" + number.get()), (int)((x + 5)/scale), (int)((y + 17)/scale), 0xFFFFFFFF);
            String LoadingString = "Loading";
            int k = ((int) (System.currentTimeMillis()/1000) % 5);
            LoadingString += (".".repeat(k));
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal(LoadingString), (int)((x + 5)/scale), (int)((y + 23)/scale), 0xFFFFFFFF);
        }
        else if(state == 1){
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Craft succeded"), (int)((x + 5)/scale), (int)((y + 5)/scale), 0xFFFFFFFF);
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Progress:"), (int)((x + 5)/scale), (int)((y + 11)/scale), 0xFFFFFFFF);
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal(currentNumber + "/" + number.get()), (int)((x + 5)/scale), (int)((y + 17)/scale), 0xFFFFFFFF);
            String LoadingString = "Completed";
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal(LoadingString), (int)((x + 5)/scale), (int)((y + 23)/scale), 0xFFFFFFFF);
        }
        else if(state == -1){
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Craft aborted"), (int)((x + 5)/scale), (int)((y + 5)/scale), 0xFFFFFFFF);
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Progress:"), (int)((x + 5)/scale), (int)((y + 11)/scale), 0xFFFFFFFF);
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal(currentNumber + "/" + number.get()), (int)((x + 5)/scale), (int)((y + 17)/scale), 0xFFFFFFFF);
            String LoadingString = "Failed";
            drawString(pPoseStack, Minecraft.getInstance().font, Component.literal(LoadingString), (int)((x + 5)/scale), (int)((y + 23)/scale), 0xFFFFFFFF);
        }
        pPoseStack.popPose();

    }
    public void hide(){
        this.visible = false;
        cancelButton.visible = false;
    }

    public void setNumber(int number){
        this.number.set(number);
    }

    public void setCurrentNumber(int currentNumber){
        this.currentNumber = currentNumber;
    }

    public void show(){
        this.visible = true;
        cancelButton.visible = true;
    }

   public void createButton(){
        cancelButton = new CustomButton(x+33, y+22, (int)(width/3.2f), (int)(height/4.3f), Component.literal("Abort"), (b)-> controller.cancelProcess(), 0xFFFF1F3D, 0xFFFFFFFF);
        controller.screen.addButton(cancelButton);
   }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public void succedCraft(){
        this.state = 1;
        cancelButton.visible = false;
        cancelButton.active = false;
    }
    public void interruptCraft(){
        this.state = -1;
        cancelButton.visible = false;
        cancelButton.active = false;
    }
    public void initiateCraft(){
        this.state = 0;
        cancelButton.visible = true;
        cancelButton.active = true;
    }

    public void drawBorderedRect(PoseStack poseStack, int x, int y, int x2, int y2, int thickness, int border) {
        // Dibuja solo el borde exterior
        fill(poseStack, x, y, x + thickness, y2, border); // Izquierda
        fill(poseStack, x2 - thickness, y, x2, y2, border); // Derecha
        fill(poseStack, x + thickness, y, x2 - thickness, y + thickness, border); // Superior
        fill(poseStack, x + thickness, y2 - thickness, x2 - thickness, y2, border); // Inferior
    }

}
