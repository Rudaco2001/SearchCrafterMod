package com.rudaco.searchcrafter.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class MenuObjectPage {


    SearchCrafterTableScreen screen;

    int x = 90;

    int y = 27;


    int pageSizeY;

    int pageSizeX;

    int objectCont;
    int x_objectCont;
    int pageNumber;

    int maxPage;
    int pageButtonCount = 5;

    ArrayList<Button> buttons = new ArrayList<>();

    ArrayList<CraftableInfo> craftableList;

    PageController controller;

    public  MenuObjectPage(PageController controller, SearchCrafterTableScreen screen, int pageNumber, int y_objectCont, int x_objectCont, int pageSizeX, int pageSizeY, ArrayList<CraftableInfo> elements, int maxPage){
        this.screen = screen;

        craftableList = elements;
        this.pageSizeX = pageSizeX;
        this.pageSizeY = pageSizeY;
        this.objectCont = y_objectCont;
        this.x_objectCont = x_objectCont;
        this.pageNumber = pageNumber;
        this.maxPage = maxPage;
        this.controller = controller;
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
                controller.selectCraft(info.item);
            }, new ItemStack(info.item), -1);
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

    protected void changePage(int page) {
        System.out.println(page);
        controller.changePage(page);
    }


    protected void drawItemButton(int x, int y, int width, int height, String text, FunctionalInterface action, ItemStack item, int number) {
        CustomImageButton button = new CustomImageButton(x, y, width, height, Component.literal(text), b -> {
            action.ejecutar();
        }, item, number);
        screen.addButton(button);
        buttons.add(button);
    }

    protected void drawPageButton(int x, int y, int width, int height, String text, FunctionalInterface action, boolean active){
        Button button = new Button(x, y, width, height, Component.literal(text), b -> {
            action.ejecutar();
        });



        if(!active) button.active = false;
        screen.addButton(button);
        buttons.add(button);

    }



    public void deletePage(){
        buttons.forEach(button->{
            screen.removeButton(button);
        });
        buttons.clear();

    }

    public void refresh(){
        this.changePage(1);
    }

}
