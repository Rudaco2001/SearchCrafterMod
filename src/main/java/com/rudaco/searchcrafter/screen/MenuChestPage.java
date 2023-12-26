package com.rudaco.searchcrafter.screen;

import net.minecraft.locale.Language;

import java.util.ArrayList;

public class MenuChestPage extends MenuObjectPage{
    public MenuChestPage(PageController controller, SearchCrafterTableScreen screen, int pageNumber, int objectCont, int pageSizeX, int pageSizeY, ArrayList<CraftableInfo> elements, int maxPage) {
        super(controller, screen, pageNumber, objectCont, pageSizeX, pageSizeY, elements, maxPage);
    }

    public void renderButtons(){

        this.x = (screen.width - pageSizeX) / 2 - 75;
        this.y = (screen.height - pageSizeY) / 2 - 44;

        int y_size = (pageSizeY-10)/objectCont;
        int i = 0;
        for (CraftableInfo info: craftableList){
            String itemName = Language.getInstance().getOrDefault(info.item.getDescriptionId());
            drawItemButton(x+3, y+(y_size*i)+5, (pageSizeX-6), y_size, itemName + " x" + info.quant, ()->{
                controller.selectExtract(info.item);
            },0);
            i++;
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
