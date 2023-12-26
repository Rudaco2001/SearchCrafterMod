package com.rudaco.searchcrafter.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.ArrayList;

public class ExtractMenu extends CraftableMenu{

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

    public void renderUnderPart(){
        buttons.add(new ButtonForText(this.x+10, this.y, 80, 20, Component.literal(remaninig + "   >>>>>>>>>>>   " + (remaninig-count))));
        Button extButton = new Button(this.x+5, this.y + 20, 90, 20, Component.literal("Extract"), pButton -> {
            this.controller.extract(new CraftableInfo(item, count));
        });
        if(remaninig < count) extButton.active = false;
        buttons.add(extButton);
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
    }

    @Override
    public void refresh() {
        controller.selectExtractMultiple(item, count);
    }
}
