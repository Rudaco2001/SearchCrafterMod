package com.rudaco.searchcrafter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rudaco.searchcrafter.SearchCrafter;
import com.rudaco.searchcrafter.staticInfo.StaticInfo;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;


public class SearchCrafterTableScreen extends AbstractContainerScreen<SearchCrafterTableMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(SearchCrafter.MOD_ID, "textures/gui/search_crafter_table_gui.png");

    PageController pageController;
    public SearchCrafterTableScreen(SearchCrafterTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 200;
        this.imageWidth = 256;
        this.pageController = new PageController(this);

    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 2000;
        this.inventoryLabelY = 2000;
        pageController.initrender();

    }

    @Override
    protected void containerTick() {
        super.containerTick();
        pageController.tick();

    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = ((width - imageWidth) / 2);
        int y = ((height - imageHeight) / 2);
        blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

    }


    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        pageController.preRender();
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        for(Widget widget : this.renderables) {
            if(widget instanceof CustomImageButton button ){
                button.postRender(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }
        }
    }



    public void addButton(Button button){
        this.addRenderableWidget(button);
    }

    public void addWidget(AbstractWidget widget){
        this.addRenderableWidget(widget);
    }

    public void addInput(EditBox input){
        this.addRenderableWidget(input);

    }

    public void removeButton(Button button){
        this.removeWidget(button);
    }

    public void removeRenderableWidget(GuiEventListener widget){
        this.removeWidget(widget);
    }

    public void removeInput(EditBox input){
        this.removeWidget(input);
    }

    public BlockPos getEntityPos(){
        return menu.getEntityPos();
    }

    @Override
    public void onClose() {
        super.onClose();
        StaticInfo.controller = null;
        StaticInfo.chestItems = new ArrayList<>();
        pageController.onClose();
    }
}
