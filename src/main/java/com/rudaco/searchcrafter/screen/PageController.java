package com.rudaco.searchcrafter.screen;

import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.network.packet.PacketC2S;
import com.rudaco.searchcrafter.network.packet.PacketC2S2;
import com.rudaco.searchcrafter.network.packet.PacketC2SRange;
import com.rudaco.searchcrafter.network.packet.PacketC2SVisible;
import com.rudaco.searchcrafter.staticInfo.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class PageController{

    EditBox searchBar = null;
    EditBox dimensionX = null;
    EditBox dimensionY = null;
    EditBox dimensionZ = null;
    Button okrenderButton;
    Button norenderButton;
    ArrayList<Button> dimensionTextButton = new ArrayList<>();
    Button showButton;
    boolean dimensionsShown = false;

    Button okButton;
    int objectPerPage = 5;
    int sizeX = 90;
    int sizeY = 90;

    boolean isChestObjectsSelected = false;
    int currentObjectPageint = 1;
    SearchCrafterTableScreen screen;
    ArrayList<CraftableInfo> craftableList = new ArrayList<>();


    ArrayList<CraftableInfo> filteredCraftableList;


    public ArrayList<CraftableInfo> chestList = new ArrayList<>();


    ArrayList<CraftableInfo> filteredChestList;


    MenuObjectPage currentObjectPage = null;

    CraftableMenu rigthSidePage = null;
    public PageController(SearchCrafterTableScreen screen){
        this.screen = screen;
        this.getAllCraftingRecipes();
        this.getAllChestItems();
        filteredCraftableList = new ArrayList<>(craftableList);
        filteredChestList = new ArrayList<>(chestList);
        StaticInfo.controller = this;
    }

    public void changePage(int page){
        this.currentObjectPage.deletePage();
        this.currentObjectPageint = page;
        this.createAndRenderLeftPage();
    }

    public void createAndRenderLeftPage(){
        if(isChestObjectsSelected){
            createChestPage(currentObjectPageint);
        }
        else {
            createObjectPage(currentObjectPageint);

        }
        renderObjectPage();
    }

    public void createObjectPage(int page){
        ArrayList<CraftableInfo> e = new ArrayList<>();
        for(int i = objectPerPage*(page-1); i < objectPerPage*(page); i++){
            if(filteredCraftableList.size() <= i) break;
            e.add(filteredCraftableList.get(i));
        }
        int resto = ((filteredCraftableList.size() % objectPerPage == 0) ? 0 : 1);
        int maxPage = (filteredCraftableList.size()/objectPerPage) + resto;
        if(maxPage <= 0) maxPage = 1;
        currentObjectPage = new MenuObjectPage(this, screen, page, objectPerPage, sizeX, sizeY, e, maxPage);
    }

    public void createChestPage(int page){
        ArrayList<CraftableInfo> e = new ArrayList<>();
        for(int i = objectPerPage*(page-1); i < objectPerPage*(page); i++){
            if(filteredChestList.size() <= i) break;
            e.add(filteredChestList.get(i));
        }
        int resto = ((filteredChestList.size() % objectPerPage == 0) ? 0 : 1);
        int maxPage = (filteredChestList.size()/objectPerPage) + resto;
        if(maxPage <= 0) maxPage = 1;
        currentObjectPage = new MenuChestPage(this, screen, page, objectPerPage, sizeX, sizeY, e, maxPage);
    }

    public void initrender(){

        this.createAndRenderLeftPage();
        this.renderSearcher();
        this.renderLeftSwitch();
        this.renderDimensions();
        this.getDimensionValues();
        this.getRenderValue();
        this.hideDimensions();
    }
    public void renderObjectPage(){
        currentObjectPage.renderButtons();
    }


    public void getAllCraftingRecipes() {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        Collection<CraftingRecipe> craftingRecipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);

        for (CraftingRecipe recipe : craftingRecipes) {
            ResourceLocation recipeId = recipe.getId();
            Item item = recipe.getResultItem().getItem();
            int id = Item.getId(item);

            boolean repeat = false;
            for(CraftableInfo c: craftableList){
                if (c.item.equals(item)) {
                    repeat = true;
                    break;
                }
            }
            if(!repeat) this.craftableList.add(new CraftableInfo(item));


        }
    }

    public void getAllChestItems(){
        this.chestList = StaticInfo.chestItems;
    }

    public void filter(){
        if(searchBar == null) return;
        filteredCraftableList = craftableList.stream().filter(e->{
            String itemName = Language.getInstance().getOrDefault(e.item.getDescriptionId());
            return itemName.toLowerCase().contains(searchBar.getValue().toLowerCase());
        }).collect(Collectors.toCollection(ArrayList::new));
        filteredChestList = chestList.stream().filter(e->{
            String itemName = Language.getInstance().getOrDefault(e.item.getDescriptionId());
            return itemName.toLowerCase().contains(searchBar.getValue().toLowerCase());
        }).collect(Collectors.toCollection(ArrayList::new));
        this.changePage(1);
    }

    public void renderSearcher(){
        int height = 15;
        int width = 100;
        int x = (screen.width) / 2 - 120;
        int y = (screen.height) / 2 - 117;
        searchBar = new EditBox(Minecraft.getInstance().font, x, y, width, height, Component.literal("SEARCH")){
            @Override
            public boolean charTyped(char pCodePoint, int pModifiers) {
                boolean result = super.charTyped(pCodePoint, pModifiers);
                filter();
                return result;
            }

            @Override
            protected void onFocusedChanged(boolean pFocused) {
                filter();
                super.onFocusedChanged(pFocused);
            }

            @Override
            public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
                if (pKeyCode == GLFW.GLFW_KEY_E && this.isFocused()) {
                    // Cancela la propagación del evento, evitando que llegue al menú
                    return true;
                }
                boolean result = super.keyPressed(pKeyCode, pScanCode, pModifiers);
                if (pKeyCode == GLFW.GLFW_KEY_BACKSPACE) {
                    filter();
                }
                return result;
            }
        };

        screen.addInput(searchBar);

    }

    public void getDimensionValues(){
        Block block = Minecraft.getInstance().level.getBlockState(screen.getEntityPos()).getBlock();
        if(block instanceof SearchCrafterTableBlock sBlock){
            BlockEntity ent = Minecraft.getInstance().level.getBlockEntity(screen.getEntityPos());
            if(ent instanceof SearchCrafterTable table) {
                if (dimensionX != null) dimensionX.setValue("" + table.range.x);
                if (dimensionY != null) dimensionY.setValue("" + table.range.y);
                if (dimensionZ != null) dimensionZ.setValue("" + table.range.z);
            }
        }
    }

    public void getRenderValue(){
        Block block = Minecraft.getInstance().level.getBlockState(screen.getEntityPos()).getBlock();
        if(block instanceof SearchCrafterTableBlock){
            BlockEntity ent = Minecraft.getInstance().level.getBlockEntity(screen.getEntityPos());
                if(ent instanceof SearchCrafterTable table){
                    if(table.renderActive){
                        if(okrenderButton != null){
                            okrenderButton.active = false;
                            norenderButton.active = true;
                        }
                    }
                    else if(norenderButton != null){
                        norenderButton.active = false;
                        okrenderButton.active = true;
                    }
                }
        }
    }

    public void renderDimensions(){

        int height = 15;
        int width = 60;
        int offset = 5;
        int x = (screen.width) / 2 + 130;
        int y = (screen.height) / 2 - 98;

         okButton = new Button(x, y + (height+offset)*4, width + 3, height, Component.literal("OK"), pButton -> {
            if(dimensionX == null) return;
            if(dimensionY == null) return;
            if(dimensionZ == null) return;
            if(!Utils.isNumeric(dimensionX.getValue())||!Utils.isNumeric(dimensionY.getValue())||!Utils.isNumeric(dimensionZ.getValue())){
                getDimensionValues();
                return;
            }
            MySimpleChannel.sendToServer(new PacketC2SRange(new Vector3(Integer.parseInt(dimensionX.getValue()),Integer.parseInt(dimensionY.getValue()),Integer.parseInt(dimensionZ.getValue())), screen.getEntityPos()));
        });
        dimensionTextButton.add(new ButtonForText(x,y, width, height, Component.literal("Range")));
        dimensionTextButton.add(new ButtonForText(x,y + (height+offset), width/4, height, Component.literal("X: ")));

        dimensionX = new EditBox(Minecraft.getInstance().font, x + width/4, y + (height+offset), width*3/4, height, Component.literal("SEARCH")){
            @Override
            public boolean charTyped(char pCodePoint, int pModifiers) {
                boolean result = super.charTyped(pCodePoint, pModifiers);
                if(!Utils.isNumeric(this.getValue())) okButton.active = false;
                return result;
            }
        };


        dimensionTextButton.add(new ButtonForText(x,y + (height+offset)*2, width/4, height, Component.literal("Y: ")));
        dimensionY = new EditBox(Minecraft.getInstance().font, x + width/4, y + (height+offset)*2 , width*3/4, height, Component.literal("SEARCH")){
            @Override
            public boolean charTyped(char pCodePoint, int pModifiers) {
                boolean result = super.charTyped(pCodePoint, pModifiers);
                if(!Utils.isNumeric(this.getValue())) okButton.active = false;
                return result;
            }
        };
        dimensionTextButton.add(new ButtonForText(x ,y + (height+offset)*3, width/4, height, Component.literal("Z: ")));
        dimensionZ = new EditBox(Minecraft.getInstance().font, x + width/4 , y + (height+offset)*3 , width*3/4, height, Component.literal("SEARCH")){
            @Override
            public boolean charTyped(char pCodePoint, int pModifiers) {
                boolean result = super.charTyped(pCodePoint, pModifiers);
                if(!Utils.isNumeric(this.getValue())) okButton.active = false;
                return result;
            }
        };
        dimensionTextButton.add(new ButtonForText(x ,y + (height+offset)*8, width, height, Component.literal("Render limits")));

        okrenderButton = new Button(x, y + (height+offset)*9, width*3/4 - 2, height, Component.literal("Enable"), pButton -> {
            MySimpleChannel.sendToServer(new PacketC2SVisible(true, screen.getEntityPos()));
            pButton.active = false;
        });
        norenderButton = new Button(x + width*3/4+2, y + (height+offset)*9, width*3/4 - 2, height, Component.literal("Disable"), pButton -> {
            MySimpleChannel.sendToServer(new PacketC2SVisible(false, screen.getEntityPos()));
            pButton.active = false;
        });

        showButton = new Button(x - width*3/10-2, y + (height+offset)*9, width*1/5, height-5, Component.literal(">>"), pButton -> {
            if(dimensionsShown){
                hideDimensions();
                pButton.setMessage(Component.literal(">>"));
            }
            else {
                showDimensions();
                pButton.setMessage(Component.literal("<<"));
            }
        });
        screen.addButton(showButton);
        screen.addInput(dimensionX);
        screen.addInput(dimensionY);
        screen.addInput(dimensionZ);
        screen.addButton(okButton);
        screen.addButton(okrenderButton);
        screen.addButton(norenderButton);
        for (Button b: dimensionTextButton) {
            screen.addButton(b);
        }
    }

    public void showDimensions(){
        dimensionsShown = true;
        screen.addInput(dimensionX);
        screen.addInput(dimensionY);
        screen.addInput(dimensionZ);
        screen.addButton(okButton);
        screen.addButton(okrenderButton);
        screen.addButton(norenderButton);
        for (Button b: dimensionTextButton) {
            screen.addButton(b);
        }
    }

    public void hideDimensions(){
        dimensionsShown = false;
        screen.removeInput(dimensionX);
        screen.removeInput(dimensionY);
        screen.removeInput(dimensionZ);
        screen.removeButton(okButton);
        screen.removeButton(okrenderButton);
        screen.removeButton(norenderButton);
        for (Button b: dimensionTextButton) {
            screen.removeButton(b);
        }
    }

    public void renderLeftSwitch(){
        int height = 11;
        int width = 35;
        int x = (screen.width) / 2 - 117;
        int y = (screen.height) / 2 - 94;
        int xOffset = 3;

        final Button[] buttonChestObjects = {null};


        Button buttonCraftableObjects = new Button(x,y,width,height,Component.literal("Craft"), pButton -> {
            if(buttonChestObjects[0] == null) return;
            buttonChestObjects[0].active = true;
            pButton.active = false;
            isChestObjectsSelected = false;
            changePage(1);
        });
        buttonChestObjects[0] = new Button(x + width + xOffset,y,width,height,Component.literal("Chest"),pButton -> {
            buttonCraftableObjects.active = true;
            pButton.active = false;
            isChestObjectsSelected = true;
            changePage(1);
        });
        buttonCraftableObjects.active = false;

        screen.addButton(buttonCraftableObjects);
        screen.addButton(buttonChestObjects[0]);
    }

    public void selectCraft(Item item){
        ArrayList<CraftableInfo> rest = new ArrayList<>();
        ArrayList<CraftableInfo> result = new ArrayList<>();
        Thread thread = new Thread(() -> {
            result.addAll(Utils.getCraftableInfo(true,item, Utils.deepCopyofCraftableInfo(this.chestList), new ArrayList<>(), rest, new IntHolder(0), new HashMap<>()));
        });
        thread.start(); // Iniciar la ejecución del hilo
        try {
            thread.join(10000); // Esperar a que el hilo termine durante un máximo de 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (thread.isAlive()) { // Si el hilo todavía está en ejecución después de 5 segundos
            thread.interrupt(); // Interrumpir el hilo
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Tiempo de espera agotado, cambiando a algoritmo simple..."));
            selectCraft_old(item);
            return;
        }
        Utils.substractCraftableLists(result,rest);
        ArrayList<CraftableInfo> needed = new ArrayList<>();
        ArrayList<CraftableInfo> inInventory = new ArrayList<>();
        Utils.separateItems(inInventory, needed, this.chestList, result);
        if(rigthSidePage != null) rigthSidePage.deletePage();
        rigthSidePage = new CraftableMenu(this,screen, 100, 100, item, inInventory, needed, rest);
        rigthSidePage.renderContent();
    }

    public void selectCraftMultiple(Item item, int count){
        ArrayList<CraftableInfo> result = new ArrayList<>();
        ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
        ArrayList<CraftableInfo> rest = new ArrayList<>();
        HashMap<Pair<Item, Integer>, Pair<Item, Boolean>> map = new HashMap<>();
        Thread thread = new Thread(() -> {
            for(int i = 0; i < count; ++i){
                Utils.combineLists(Utils.getCraftableInfo(true, item, chestCopy, new ArrayList<>(), rest, new IntHolder(0), map), result);
            }
        });
        thread.start(); // Iniciar la ejecución del hilo
        try {
            thread.join(10000); // Esperar a que el hilo termine durante un máximo de 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (thread.isAlive()) { // Si el hilo todavía está en ejecución después de 5 segundos
            thread.interrupt(); // Interrumpir el hilo
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Tiempo de espera agotado, cambiando a algoritmo simple..."));
            selectCraftMultiple_old(item,count);
            return;
        }
        Utils.substractCraftableLists(result,rest);
        ArrayList<CraftableInfo> needed = new ArrayList<>();
        ArrayList<CraftableInfo> inInventory = new ArrayList<>();
        Utils.separateItems(inInventory, needed, this.chestList, result);
        if(rigthSidePage != null) rigthSidePage.deletePage();
        rigthSidePage = new CraftableMenu(this,screen, 100, 100, item, inInventory, needed, rest, count);
        rigthSidePage.renderContent();
    }

    public void selectCraftAll(Item item){
        AtomicInteger count = new AtomicInteger();
        ArrayList<CraftableInfo> result = new ArrayList<>();
        ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
        ArrayList<CraftableInfo> rest = new ArrayList<>();
        ArrayList<CraftableInfo> needed = new ArrayList<>();
        HashMap<Pair<Item, Integer>, Pair<Item, Boolean>> map = new HashMap<>();
        Thread thread = new Thread(() -> {
            do{
                needed.clear();
                Utils.combineLists(Utils.getCraftableInfo(true, item, chestCopy, new ArrayList<>(), rest, new IntHolder(0), map), result);
                Utils.substractCraftableLists(result,rest);
                ArrayList<CraftableInfo> inInventory = new ArrayList<>();
                Utils.separateItems(inInventory, needed, this.chestList, result);
                count.getAndIncrement();
            }while(needed.isEmpty());
        });
        thread.start(); // Iniciar la ejecución del hilo
        try {
            thread.join(10000); // Esperar a que el hilo termine durante un máximo de 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (thread.isAlive()) { // Si el hilo todavía está en ejecución después de 5 segundos
            thread.interrupt(); // Interrumpir el hilo
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Tiempo de espera agotado, cambiando a algoritmo simple..."));
            selectCraftAll_old(item);
             return;
        }
        selectCraftMultiple(item, Math.max(1, count.get() -1));
    }

    public void selectCraft_old(Item item){
        ArrayList<CraftableInfo> rest = new ArrayList<>();
        ArrayList<CraftableInfo> result = Utils.getCraftableInfo_old(item, Utils.deepCopyofCraftableInfo(this.chestList), new ArrayList<>(), rest);
        Utils.substractCraftableLists(result,rest);
        ArrayList<CraftableInfo> needed = new ArrayList<>();
        ArrayList<CraftableInfo> inInventory = new ArrayList<>();
        Utils.separateItems(inInventory, needed, this.chestList, result);
        if(rigthSidePage != null) rigthSidePage.deletePage();
        rigthSidePage = new CraftableMenu(this,screen, 100, 100, item, inInventory, needed, rest);
        rigthSidePage.renderContent();
    }
    public void selectCraftMultiple_old(Item item, int count){
        ArrayList<CraftableInfo> result = new ArrayList<>();
        ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
        ArrayList<CraftableInfo> rest = new ArrayList<>();
        for(int i = 0; i < count; ++i){
            Utils.combineLists(Utils.getCraftableInfo_old(item, chestCopy, new ArrayList<>(), rest), result);
        }
        Utils.substractCraftableLists(result,rest);
        ArrayList<CraftableInfo> needed = new ArrayList<>();
        ArrayList<CraftableInfo> inInventory = new ArrayList<>();
        Utils.separateItems(inInventory, needed, this.chestList, result);
        if(rigthSidePage != null) rigthSidePage.deletePage();
        rigthSidePage = new CraftableMenu(this,screen, 100, 100, item, inInventory, needed, rest, count);
        rigthSidePage.renderContent();
    }

    public void selectCraftAll_old(Item item){
        int count = 0;
        ArrayList<CraftableInfo> result = new ArrayList<>();
        ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
        ArrayList<CraftableInfo> rest = new ArrayList<>();
        ArrayList<CraftableInfo> needed;
        do{
            needed = new ArrayList<>();
            Utils.combineLists(Utils.getCraftableInfo_old(item, chestCopy, new ArrayList<>(), rest), result);
            Utils.substractCraftableLists(result,rest);
            ArrayList<CraftableInfo> inInventory = new ArrayList<>();
            Utils.separateItems(inInventory, needed, this.chestList, result);
            count++;
        }while(needed.isEmpty());
        selectCraftMultiple(item, Math.max(1, count-1));
    }
    public void selectExtract(Item item){
        if(rigthSidePage != null) rigthSidePage.deletePage();
        int remaining = 0;
        for(CraftableInfo info: chestList){
            if(item.equals(info.item)){
                remaining = info.quant;
            }
        }
        rigthSidePage = new ExtractMenu(this,screen, 100, 100, item, remaining);
        rigthSidePage.renderContent();
    }


    public void selectExtractMultiple(Item item, int count){
        if(rigthSidePage != null) rigthSidePage.deletePage();
        int remaining = 0;
        for(CraftableInfo info: chestList){
            if(item.equals(info.item)){
                remaining = info.quant;
            }
        }
        rigthSidePage = new ExtractMenu(this,screen, 100, 100, item, remaining, count);
        rigthSidePage.renderContent();
    }

    public void selectExtractAll(Item item){
        if(rigthSidePage != null) rigthSidePage.deletePage();
        int remaining = 0;
        for(CraftableInfo info: chestList){
            if(item.equals(info.item)){
                remaining = info.quant;
            }
        }
        rigthSidePage = new ExtractMenu(this,screen, 100, 100, item, remaining, remaining);
        rigthSidePage.renderContent();
    }



    public void extract(CraftableInfo item) {
        MySimpleChannel.sendToServer(new PacketC2S2(item, screen.getEntityPos()));
    }



    public void craft(CraftableInfo item, ArrayList<CraftableInfo> usedMaterials, ArrayList<CraftableInfo> rest) {
        MySimpleChannel.sendToServer(new PacketC2S(usedMaterials, rest, item, screen.getEntityPos()));
    }

    public void refreshMenu(){
        filteredChestList = new ArrayList<>(chestList);
        if(currentObjectPage != null) currentObjectPage.refresh();
        if(rigthSidePage != null) rigthSidePage.refresh();
    }
}
