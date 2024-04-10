package com.rudaco.searchcrafter.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class MiniPage extends MenuChestPage{

    CraftableMenu menu;
    public MiniPage(CraftableMenu menu, SearchCrafterTableScreen screen, int pageNumber, int objectCont, int objectCount_x, int pageSizeX, int pageSizeY, ArrayList<CraftableInfo> elements, int maxPage) {
        super(null, screen, pageNumber, objectCont, objectCount_x, pageSizeX, pageSizeY, elements, maxPage);
        this.menu = menu;
    }

    @Override
    protected void changePage(int page) {
        menu.changePagetoUse(page);
    }


    public void renderButtons(){
        this.x = (screen.width - pageSizeX) / 2 + 40;
        this.y = (screen.height - pageSizeY) / 2 - 30;
        int y_padding = 1;
        int x_padding = 1;
        int size = (pageSizeY-10-y_padding*(objectCont+1))/objectCont;
        int i = 0;
        int z = 0;
        for (CraftableInfo info: craftableList){
            String itemName = Language.getInstance().getOrDefault(info.item.getDescriptionId());
            drawItemButton(x+5 + ((size+x_padding)*z), y+((size + y_padding)*i)+8+y_padding, size, size, itemName, ()->{
                controller.selectCraft(info.item);
            }, new ItemStack(info.item), info.quant);
            z++;
            if(z > x_objectCont - 1){
                z = 0;
                i++;
            }
        }

        int x_size = (pageSizeX)/pageButtonCount;
        int minPage = Math.max(1, (pageNumber-1));

        String text;
        for(int j = 0; j < pageButtonCount; j++){
            int nextPage = minPage+j-1;

            boolean active = minPage + j-1 != pageNumber;
            if(j == 0){
                text = "<<";
                nextPage = 1;
            }
            else if(j == pageButtonCount-1){
                text = ">>";
                nextPage = maxPage;
            }
            else{
                text = String.valueOf(minPage+j-1);
                if(nextPage > maxPage) continue;
            }

            int finalJ = nextPage;
            drawPageButton(x + j * x_size, y + pageSizeY, x_size, 7, text, () -> {
                this.changePage(finalJ);
            }, active);
        }
    }

    protected void drawPageButton(int x, int y, int width, int height, String text, FunctionalInterface action, boolean active){
        Button button = new TextButton(x, y, width, height, Component.literal(text), b -> {
            action.ejecutar();
        }, 0.5f);
        if(!active) button.active = false;
        screen.addButton(button);
        buttons.add(button);
    }

}
