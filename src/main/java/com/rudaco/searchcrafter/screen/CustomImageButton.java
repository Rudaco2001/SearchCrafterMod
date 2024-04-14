package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CustomImageButton extends Button {


    int x;
    int y;
    int width;
    int height;
    public float maxScale = 1;
    int number = -1;
    int state = 0;
boolean hidden = false;

    ItemStack item;
    public CustomImageButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, ItemStack item){

        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
        this.x = pX;
        y = pY;
        width = pWidth;
        height = pHeight;
        this.item = item;
    }


    public CustomImageButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, ItemStack item, int number){

        super(pX, pY, pWidth, pHeight, pMessage, pOnPress);
        this.x = pX;
        y = pY;
        width = pWidth;
        height = pHeight;
        this.item = item;
        this.number = number;
    }


    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if(hidden) return;

        if(state == -1){
            fill(pPoseStack, x,y,x+width,y+height,0xAAB60502);
        }
        else if(state == 1){
            fill(pPoseStack, x,y,x+width,y+height,0xAA98BB77);
        }
        int centered_x = x;
        int centered_y = y;
        if(this.width > 16){
            centered_x += (this.width-16)/2;
        }
        if(this.height > 16){
            centered_y += (this.height-16)/2;
        }
        if(item != null){
            pPoseStack.pushPose();
            pPoseStack.scale(0.1f,0.1f,1);
            Minecraft.getInstance().getItemRenderer().renderGuiItem(item, centered_x, centered_y);
            pPoseStack.popPose();
        }

        drawBorderedRect(pPoseStack, this.x, this.y, this.x + this.width, this.y + this.height, 1,  0xFF000000);
        if(number != -1){
            String numberString = String.valueOf(number);
            if(number >= 1000){
                numberString = ((float) (number / 100))/10 + "k";
            }
            if(number >= 100000){
                numberString = number / 1000 + "k";
            }
            if(number >= 1000000){
                numberString = ((float) (number / 100000))/10 + "M";
            }

            int numberwidth =  Minecraft.getInstance().font.width(numberString);
            int numberheigth =  Minecraft.getInstance().font.lineHeight;
            float scale = 0.6f;
            numberwidth = (int)(scale * (float)numberwidth);
            numberheigth = (int)(scale * (float)numberheigth);
            if(numberwidth > 3*width/4){
                scale = ((3*(float) width/4) / numberwidth)*scale;
                numberwidth = 3*width/4;
                numberheigth = (int)(scale * Minecraft.getInstance().font.lineHeight);
            }
            pPoseStack.pushPose();
            pPoseStack.translate(0,0,200);
            AbstractButton.fill(pPoseStack, (this.x + width - numberwidth - 2), this.y + this.height - numberheigth - 2, this.x+width, y+height, 0xAA000000);
            pPoseStack.scale(scale,scale,1);
            drawString(pPoseStack, Minecraft.getInstance().font, numberString, (int) ((this.x + width - numberwidth - 1)/scale), (int) ((this.y + this.height - numberheigth)/scale), 0xFFFFFFFF);
            pPoseStack.popPose();
        }


        //this.drawText(pPoseStack,this.width-10);
    }

    void hideButton(){
        hidden = true;
    }

    void showButton(){
        hidden = false;
    }

    void setState(int newState){
        state = newState;
    }

    public void setItem(ItemStack item){
        this.item = item;
    }

    public void setNumber(int number){
        this.number = number;
    }

    public void showTooltip(PoseStack pPoseStack, int pMouseX, int pMouseY){
        if(hidden) return;
        if (this.isHoveredOrFocused() && this.item != null) {
            pPoseStack.pushPose();
            pPoseStack.translate(0,0, 500);
            String hoverName = item.getHoverName().getString();
            if(number != -1) hoverName += " - " + number;
            int textWidth = Minecraft.getInstance().font.width(hoverName);
            AbstractButton.fill(pPoseStack, pMouseX-3, pMouseY-4 + 10, pMouseX + textWidth+3, pMouseY + 9 + 10 + 1, 0xFF000000);
            drawString(pPoseStack, Minecraft.getInstance().font, hoverName, pMouseX, pMouseY + 10, 0xFFFFFFFF);
            drawBorderedRect(pPoseStack, pMouseX-2, pMouseY-3 + 10, pMouseX + textWidth+2, pMouseY + 9 + 10, 1, 0xFFFFFFFF);
            pPoseStack.popPose();
        }
    }

    public void drawBorderedRect(PoseStack poseStack, int x, int y, int x2, int y2, int thickness, int border) {
        // Dibuja solo el borde exterior
        fill(poseStack, x, y, x + thickness, y2, border); // Izquierda
        fill(poseStack, x2 - thickness, y, x2, y2, border); // Derecha
        fill(poseStack, x + thickness, y, x2 - thickness, y + thickness, border); // Superior
        fill(poseStack, x + thickness, y2 - thickness, x2 - thickness, y2, border); // Inferior
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


    public void postRender(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        showTooltip(pPoseStack, pMouseX, pMouseY);
    }
}
