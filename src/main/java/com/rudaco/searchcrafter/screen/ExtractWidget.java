package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ExtractWidget extends AbstractWidget {

    ItemStack item;
    int actCount;
    int extCount;
    float scale = 0.6f;
    public ExtractWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage, Item item, int actCount, int extCount) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.item = new ItemStack(item);
        this.actCount = actCount;
        this.extCount = extCount;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        fill(pPoseStack, this.x, this.y, this.x+width, y+height, 0xFF000000);
        drawBorderedRect(pPoseStack, x+2, y+2, x+width-2, y+height-2, 1, 0xFFFFFFFF);
        pPoseStack.pushPose();
        pPoseStack.scale(scale,scale,1);

        drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Extraction Info: "), (int)((x + 5)/scale), (int)((y + 5)/scale), 0xFFFFFFFF);
        drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Item: " + item.getHoverName().getString()), (int)((x + 5)/scale), (int)((y + 12)/scale), 0xFFFFFFFF);
        drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Actual Quantity: " + actCount), (int)((x + 5)/scale), (int)((y + 19)/scale), 0xFFFFFFFF);
        drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Extract Quantity: " + extCount), (int)((x + 5)/scale), (int)((y + 26)/scale), 0xFFFFFFFF);
        drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Remaining Quantity: " + (actCount - extCount)), (int)((x + 5)/scale), (int)((y + 33)/scale), 0xFFFFFFFF);
        drawString(pPoseStack, Minecraft.getInstance().font, Component.literal("Operation: " + (actCount - extCount >= 0 ? "Possible":"Not Possible")), (int)((x + 5)/scale), (int)((y + 40)/scale), (actCount - extCount >= 0 ? 0xAA98BB77 : 0xAAB60502));
        pPoseStack.popPose();

    }

    public void drawBorderedRect(PoseStack poseStack, int x, int y, int x2, int y2, int thickness, int border) {
        // Dibuja solo el borde exterior
        fill(poseStack, x, y, x + thickness, y2, border); // Izquierda
        fill(poseStack, x2 - thickness, y, x2, y2, border); // Derecha
        fill(poseStack, x + thickness, y, x2 - thickness, y + thickness, border); // Superior
        fill(poseStack, x + thickness, y2 - thickness, x2 - thickness, y2, border); // Inferior
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
