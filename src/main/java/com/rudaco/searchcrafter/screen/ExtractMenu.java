package com.rudaco.searchcrafter.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class ExtractMenu extends CraftableMenu{
    ExtractWidget extractWidget;
    int remaninig;
    public ExtractMenu(PageController controller, SearchCrafterTableScreen screen, int pageSizeX, int pageSizeY, Item item, int remaining) {
        super(controller, screen, pageSizeX, pageSizeY, item, null, null, null);
        this.text = "Se esta intentando extraer";
        this.remaninig = remaining;
    }

    public ExtractMenu(PageController controller, SearchCrafterTableScreen screen, int pageSizeX, int pageSizeY, Item item, int remaining, int count) {
        super(controller, screen, pageSizeX, pageSizeY, item, null, null,null, count);
        this.text = "Se esta intentando extraer";
        this.remaninig = remaining;
    }


    @Override
    public void renderContent() {
        super.renderContent();
        createUpperpart();
    }



    public void createUpperpart(){
        extractWidget = new ExtractWidget(this.x+5, this.y-30, 100, 50, Component.literal( ""), item, remaninig, count);
        screen.addWidget(extractWidget);
    }

    public void renderUnderPart(){
        this.y += 25;
        buttons.add(new ButtonForText(this.x+15, this.y, 80, 20, Component.literal("   >>>>>>>   " )));
        CustomImageButton leftImg = new CustomImageButton(this.x+5, this.y, 20, 20, Component.literal( ""), (b)->{}, new ItemStack(item), remaninig);
        CustomImageButton rightImg = new CustomImageButton(this.x+75, this.y, 20, 20, Component.literal( ""), (b)->{}, new ItemStack(item), remaninig-count);
        buttons.add(leftImg);
        buttons.add(rightImg);
        leftImg.setState(1);
        rightImg.setState(remaninig-count >= 0 ? 1:-1);
          Button extButton = new Button(this.x+5, this.y + 25, 90, 20, Component.literal("Extract"), pButton -> {
            this.controller.extract(new CraftableInfo(item, count));
        });
        if(remaninig < count) extButton.active = false;
        buttons.add(extButton);
        this.y -= 25;
    }

    @Override
    public void changeCuant(int quant) {
        controller.selectExtractMultiple(item, quant);
    }

    public void onAllPressed(){controller.selectExtractAll(item);}
    public void deletePage(){
        for(Button b: buttons){
            screen.removeButton(b);
        }
        screen.removeInput(counter);
        screen.removeRenderableWidget(extractWidget);
    }

    @Override
    public void refresh() {
        controller.selectExtractMultiple(item, count);
    }
}
