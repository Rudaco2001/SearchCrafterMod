package com.rudaco.searchcrafter.screen;

import net.minecraft.locale.Language;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class MenuChestPage extends MenuObjectPage{
    public MenuChestPage(PageController controller, SearchCrafterTableScreen screen, int pageNumber, int objectCont, int objectCont_x, int pageSizeX, int pageSizeY, ArrayList<CraftableInfo> elements, int maxPage) {
        super(controller, screen, pageNumber, objectCont, objectCont_x, pageSizeX, pageSizeY, elements, maxPage);
    }

    public void renderButtons(){

        this.x = (screen.width - pageSizeX) / 2 - 75;
        this.y = (screen.height - pageSizeY) / 2 - 44;
        int y_padding = 1;
        int x_padding = 1;
        int size = (pageSizeY-10-y_padding*(objectCont+1))/objectCont;
        int i = 0;
        int z = 0;
        for (CraftableInfo info: craftableList){
            String itemName = Language.getInstance().getOrDefault(info.item.getDescriptionId());
            drawItemButton(x+5 + ((size+x_padding)*z), y+((size + y_padding)*i)+8+y_padding, size, size, itemName, ()->{
                controller.selectExtract(info.item);
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
            drawPageButton(x + j*x_size, y+87, x_size, 10, text, ()->{this.changePage(finalJ);}, active);
        }
    }
}
