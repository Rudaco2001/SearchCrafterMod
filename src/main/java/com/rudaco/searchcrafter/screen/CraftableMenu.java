package com.rudaco.searchcrafter.screen;

import com.rudaco.searchcrafter.staticInfo.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;


import java.util.ArrayList;

public class CraftableMenu {
    private int pageSizeY;
    private int pageSizeX;
    protected EditBox counter;
    int x;
    int y;
    int width = 100;
    MiniPage toUseObjectsPage = null;
    MenuChestPage notEnoughPage;

    int objectsPerRow = 2;

    int objectsPerColumn = 2;

    int objectPerPage;
    protected PageController controller;
    public SearchCrafterTableScreen screen;
    protected Item item;
    ArrayList<CraftableInfo> toUseItems;
    ArrayList<CraftableInfo> notEnoughItems;

    ArrayList<CraftableInfo> rest;
    ArrayList<Button> buttons = new ArrayList<>();
    String text;

    int count;


    public  CraftableMenu(PageController controller, SearchCrafterTableScreen screen, int pageSizeX, int pageSizeY, Item item, ArrayList<CraftableInfo> toUseItems, ArrayList<CraftableInfo> notEnoughItems, ArrayList<CraftableInfo> rest){
        this.screen = screen;
        this.pageSizeX = pageSizeX;
        this.pageSizeY = pageSizeY;
        this.controller = controller;
        this.item = item;
        this.toUseItems = toUseItems;
        this.notEnoughItems = notEnoughItems;
        this.text = "Se esta intentando craftear:";
        this.count = 1;
        this.rest = rest;
        this.objectPerPage = objectsPerRow*objectsPerColumn;
    }

    public  CraftableMenu(PageController controller, SearchCrafterTableScreen screen, int pageSizeX, int pageSizeY, Item item, ArrayList<CraftableInfo> toUseItems, ArrayList<CraftableInfo> notEnoughItems,ArrayList<CraftableInfo> rest, int count){
        this.screen = screen;
        this.pageSizeX = pageSizeX;
        this.pageSizeY = pageSizeY;
        this.controller = controller;
        this.item = item;
        this.toUseItems = toUseItems;
        this.notEnoughItems = notEnoughItems;
        this.text = "Se esta intentando craftear:";
        this.count = count;
        this.rest = rest;
        this.objectPerPage = objectsPerRow*objectsPerColumn;
    }



    public void renderContent(){
        this.x = (screen.width - pageSizeX) / 2 + 60;
        this.y = (screen.height - pageSizeY) / 2 - 44;

        buttons.add(new ButtonForText(x,y,width,10, Component.literal(this.text)));
        y += 15;
        String itemName = Language.getInstance().getOrDefault(item.getDescriptionId());
        buttons.add(new ButtonForText(x,y,width,10, Component.literal(itemName + " x" + count)));
        y += 15;
        renderUnderPart();
        addCounter();
        for(Button b: buttons){
            screen.addButton(b);
        }

    }

    public void renderUnderPart(){
        buttons.add(new ButtonForText(x,y,width/2 - 10,10, Component.literal("Se va a usar:")));
        x += width/2 + 5;
        buttons.add(new ButtonForText(x,y,width/2 - 10,10, Component.literal("Se necesita:")));
        y += 15;
        x -= width/2;

        changePagetoUse(1);
        changePageNeeded(1);
        Button craftButton = new Button(this.x + 68, this.y + 65, 40, 20, Component.literal("Craft"), pButton -> {
            if(!notEnoughItems.isEmpty()) return;
            this.controller.craft(new CraftableInfo(item, count), this.toUseItems, this.rest);
        });
        if(!this.notEnoughItems.isEmpty()) craftButton.active = false;
        buttons.add(craftButton);

    }

    public void addCounter(){
        int localx = (screen.width - pageSizeX) / 2 + 25;
        int localy = (screen.height - pageSizeY) / 2 - 14;
        buttons.add(new BorderButton(localx, localy - 20, 30, 62, Component.empty()));
        buttons.add(new ButtonForText(localx, localy - 15, 20, 10, Component.literal("Quant")));
        Button okButton = new Button(localx + 5, localy + 15, 20, 10, Component.literal("OK"), pButton -> {
            if(counter == null) return;
            if(!Utils.isNumeric(counter.getValue())){
                changeCuant(count);
                return;
            }
            changeCuant(Integer.parseInt(counter.getValue()));
        });
        buttons.add(okButton);
        Button allButton = new Button(localx + 5, localy + 27, 20, 10, Component.literal("All"), pButton -> {
            onAllPressed();
        });
        buttons.add(allButton);
        counter = new EditBox(Minecraft.getInstance().font, localx + 5, localy , 20, 10, Component.literal("SEARCH")){
            @Override
            public boolean charTyped(char pCodePoint, int pModifiers) {
                boolean result = super.charTyped(pCodePoint, pModifiers);
                okButton.active = Utils.isNumeric(this.getValue());
                return result;
            }
        };
        counter.setValue(String.valueOf(count));
        screen.addInput(counter);
    }

    public void changeCuant(int cuant){
        controller.selectCraftMultiple(item, cuant);
    }
    public void onAllPressed(){controller.selectCraftAll(item);}
    public void changePagetoUse(int page){
        ArrayList<CraftableInfo> e = new ArrayList<>();
        for(int i = objectPerPage*(page-1); i < objectPerPage*(page); i++){
            if(toUseItems.size() <= i) break;
            e.add(toUseItems.get(i));
        }
        int resto = ((toUseItems.size() % objectPerPage == 0) ? 0 : 1);
        int maxPage = (toUseItems.size()/objectPerPage) + resto;
        if(maxPage <= 0) maxPage = 1;
        if(toUseObjectsPage != null) toUseObjectsPage.deletePage();
        toUseObjectsPage = new MiniPage(this, screen, page, objectsPerColumn, objectsPerRow, width/2 + 8, 50, e, maxPage);
        toUseObjectsPage.renderButtons();

    }

    public void changePageNeeded(int page){
        ArrayList<CraftableInfo> e = new ArrayList<>();
        for(int i = objectPerPage*(page-1); i < objectPerPage*(page); i++){
            if(notEnoughItems.size() <= i) break;
            e.add(notEnoughItems.get(i));
        }
        int resto = ((notEnoughItems.size() % objectPerPage == 0) ? 0 : 1);
        int maxPage = (notEnoughItems.size()/objectPerPage) + resto;
        if(maxPage <= 0) maxPage = 1;

        if(notEnoughPage != null) notEnoughPage.deletePage();
        notEnoughPage = new MiniPage2(this, screen, page, objectsPerColumn, objectsPerRow, width/2 + 8, 50, e, maxPage);
        notEnoughPage.renderButtons();

    }

    public void deletePage(){
        toUseObjectsPage.deletePage();
        notEnoughPage.deletePage();
        for(Button b: buttons){
            screen.removeButton(b);
        }
        screen.removeInput(counter);
    }

    public void refresh(){
        controller.selectCraftMultiple(this.item, this.count);
    }


}
