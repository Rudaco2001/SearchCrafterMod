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
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import java.util.concurrent.locks.ReentrantLock;
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
    int objectRows = 4;
    int objectColumns = 4;
    int sizeX = 90;
    int sizeY = 90;
    boolean isChestObjectsSelected = false;
    int currentObjectPageint = 1;
    boolean rightPageHasToRender = false;
    boolean isClosed = false;
    SearchCrafterTableScreen screen;
    ArrayList<CraftableInfo> craftableList = new ArrayList<>();
    ArrayList<CraftableInfo> filteredCraftableList;
    public ArrayList<CraftableInfo> chestList = new ArrayList<>();
    ArrayList<CraftableInfo> filteredChestList;
    MenuObjectPage currentObjectPage = null;
    CraftableMenu rigthSidePage = null;
    CraftableMenu rightBufferPage = null;
    final ReentrantLock lock1 = new ReentrantLock();

    final ReentrantLock craftGridLock = new ReentrantLock();


    ArrayList<CustomImageButton> craftGrid = new ArrayList<>();
    LoadingWidget loadingWidget;
    boolean craftGridVisibility = true;
    ArrayList<Pair<ItemStack,Pair<Integer, Integer>>> craftGridBuffer = new ArrayList<>();
    AtomicInteger craftNumberBuffer = new AtomicInteger(-1);

    Thread currentThread = null;
    private boolean rigthSidePageHasToHide;
    private boolean isUsingAlternativeAlgorithm = false;
    private Checkbox algorithmCheckbox;

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

    public void tick(){
        if(rigthSidePageHasToHide){
            if (rigthSidePage != null){
                rigthSidePage.deleteUnderPages();
            }
            rigthSidePageHasToHide = false;
        }
        if(rightPageHasToRender){
            if (rigthSidePage != null) rigthSidePage.deletePage();
            rigthSidePage = rightBufferPage;
            rigthSidePage.renderContent();
            rightPageHasToRender = false;
            loadingWidget.succedCraft();
        }
    }

    public void setCraftingGridValue(int index, ItemStack item, int state){
        craftGridLock.lock();
        craftGridBuffer.add(new Pair<>(item, new Pair<>(index,state)));
        craftGridLock.unlock();
    }

    public void setCraftingNumber(int number){
        craftNumberBuffer.set(number);
        loadingWidget.setCurrentNumber(number);
    }
    public void preRender(){
        craftGridLock.lock();
        while(!craftGridBuffer.isEmpty()){
            var b = craftGridBuffer.get(0);
            craftGrid.get(b.second.first).setItem(b.first);
            craftGrid.get(b.second.first).setState(b.second.second);
            craftGridBuffer.remove(0);
        }
        craftGridLock.unlock();
        int newNumber = craftNumberBuffer.get();
        if(newNumber != -1){
            craftGrid.get(0).setNumber(newNumber);
            craftNumberBuffer.set(-1);
        }
    }

    public void createObjectPage(int page){
        int objectPerPage = objectRows * objectColumns;
        ArrayList<CraftableInfo> e = new ArrayList<>();
        for(int i = objectPerPage*(page-1); i < objectPerPage*(page); i++){
            if(filteredCraftableList.size() <= i) break;
            e.add(filteredCraftableList.get(i));
        }
        int resto = ((filteredCraftableList.size() % objectPerPage == 0) ? 0 : 1);
        int maxPage = (filteredCraftableList.size()/objectPerPage) + resto;
        if(maxPage <= 0) maxPage = 1;

        currentObjectPage = new MenuObjectPage(this, screen, page, objectRows, objectColumns ,sizeX, sizeY, e, maxPage);
    }

    public void createChestPage(int page){
        int objectPerPage = objectRows * objectColumns;
        ArrayList<CraftableInfo> e = new ArrayList<>();
        for(int i = objectPerPage*(page-1); i < objectPerPage*(page); i++){
            if(filteredChestList.size() <= i) break;
            e.add(filteredChestList.get(i));
        }
        int resto = ((filteredChestList.size() % objectPerPage == 0) ? 0 : 1);
        int maxPage = (filteredChestList.size()/objectPerPage) + resto;
        if(maxPage <= 0) maxPage = 1;
        currentObjectPage = new MenuChestPage(this, screen, page, objectRows, objectColumns, sizeX, sizeY, e, maxPage);
    }

    public void initrender(){

        this.createAndRenderLeftPage();
        this.renderSearcher();
        this.renderLeftSwitch();
        this.renderDimensions();
        this.getDimensionValues();
        this.getRenderValue();
        this.hideDimensions();
        this.createCraftGrid();
    }
    public void renderObjectPage(){
        currentObjectPage.renderButtons();
    }


    public void getAllCraftingRecipes() {
        assert Minecraft.getInstance().level != null;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        Collection<CraftingRecipe> craftingRecipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);

        for (CraftingRecipe recipe : craftingRecipes) {
            Item item = recipe.getResultItem().getItem();
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
        }).sorted((a,b)->{
            int itemName1 = Language.getInstance().getOrDefault(a.item.getDescriptionId()).length();
            int itemName2 = Language.getInstance().getOrDefault(b.item.getDescriptionId()).length();
            return itemName1 - itemName2;
        }).collect(Collectors.toCollection(ArrayList::new));
        filteredChestList = chestList.stream().filter(e->{
            String itemName = Language.getInstance().getOrDefault(e.item.getDescriptionId());
            return itemName.toLowerCase().contains(searchBar.getValue().toLowerCase());
        }).sorted((a,b)->{
            int itemName1 = Language.getInstance().getOrDefault(a.item.getDescriptionId()).length();
            int itemName2 = Language.getInstance().getOrDefault(b.item.getDescriptionId()).length();
            return itemName1 - itemName2;
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
        assert Minecraft.getInstance().level != null;
        Block block = Minecraft.getInstance().level.getBlockState(screen.getEntityPos()).getBlock();
        if(block instanceof SearchCrafterTableBlock){
            BlockEntity ent = Minecraft.getInstance().level.getBlockEntity(screen.getEntityPos());
            if(ent instanceof SearchCrafterTable table) {
                if (dimensionX != null) dimensionX.setValue("" + table.range.x);
                if (dimensionY != null) dimensionY.setValue("" + table.range.y);
                if (dimensionZ != null) dimensionZ.setValue("" + table.range.z);
            }
        }
    }

    public void getRenderValue(){
        assert Minecraft.getInstance().level != null;
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


        dimensionTextButton.add(new ButtonForText(x ,y + (height+offset)*5, width+10, height, Component.literal("Use alternative algorithm")));
        dimensionTextButton.add(new ButtonForText(x ,y + (int)((height+offset)*5.3), width+10, height, Component.literal("(usually not recomended)")));

        algorithmCheckbox = new Checkbox(x ,y + (height+offset)*6, width, height, Component.literal(""), false){
            @Override
            public void onClick(double pMouseX, double pMouseY) {
                super.onClick(pMouseX, pMouseY);
                isUsingAlternativeAlgorithm = this.selected();
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

        showButton = new Button(x - width*3/10-2, y + (height+offset)*9, width /5, height-5, Component.literal(">>"), pButton -> {
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
        screen.addWidget(algorithmCheckbox);
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
        screen.addWidget(algorithmCheckbox);
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
        screen.removeRenderableWidget(algorithmCheckbox);
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

    void createCraftGrid(){
        int x = (screen.width) / 2 + 63;
        int y = (screen.height) / 2 - 102;
        int size = 18;
        int x_padding = 0;
        int y_padding = 0;
        CustomImageButton buttonObject = new CustomImageButton(x-60 + ((size+x_padding)), y+8+36, size, size, Component.literal(""), (e)->{}, null,-1);
        craftGrid.add(buttonObject);
        screen.addButton(buttonObject);
        for (int i = 0; i < 3; i++){
            for (int z = 0; z < 3; z++){
                CustomImageButton button = new CustomImageButton(x+5 + ((size+x_padding)*z), y+((size + y_padding)*i)+8+y_padding, size, size, Component.literal(""), (e)->{}, null,-1);
                craftGrid.add(button);
                screen.addButton(button);
            }
        }
        loadingWidget = new LoadingWidget(x-52, y+10, 55, 33, Component.literal(""), this);
        screen.addWidget(loadingWidget);
        loadingWidget.createButton();
        hideGrid();
    }

    void showGrid(){
        if(!craftGridVisibility){
            for(CustomImageButton button: craftGrid){
                button.showButton();
            }
            loadingWidget.show();
            craftGridVisibility = true;
        }


    }

    void hideGrid(){
        if(craftGridVisibility){
            for(CustomImageButton button: craftGrid){
                button.hideButton();
            }
            loadingWidget.hide();
            craftGridVisibility = false;
        }

    }

    public void selectCraft(Item item){
        if(!this.isUsingAlternativeAlgorithm){
            selectCraft_new(item);
        }
        else {
            selectCraft_old(item);
        }
    }

    public void selectCraftMultiple(Item item, int count){
        if(!this.isUsingAlternativeAlgorithm){
            selectCraftMultiple_new(item,count);
        }
        else {
            selectCraftMultiple_old(item,count);
        }
    }

    public void selectCraftAll(Item item){
        if(!this.isUsingAlternativeAlgorithm){
            selectCraftAll_new(item);
        }
        else {
            selectCraftAll_old(item);
        }
    }

    public void selectCraft_new(Item item){

            if (lock1.isLocked()) {
                assert Minecraft.getInstance().player != null;
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("There is another process ongoing. Please wait until it finishes"));
                return;
            }
            ArrayList<CraftableInfo> sequreChestList = Utils.deepCopyofCraftableInfo(this.chestList);
            showGrid();
            this.setCraftingGridValue(0, new ItemStack(item), 0);
            loadingWidget.setNumber(1);
            loadingWidget.initiateCraft();
            this.setCraftingNumber(1);
            loadingWidget.setCurrentNumber(0);
            rigthSidePageHasToHide = true;
        if (rigthSidePage != null) rigthSidePage.deletePage();
        rigthSidePage = new CraftableMenu(this, screen, 100, 100, item, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        rigthSidePage.renderContent();
            Thread thread = new Thread(() -> {
                try {
                    lock1.lock();
                    ArrayList<Pair<CraftableInfo, Boolean>> rest = new ArrayList<>();
                    IntHolder neededNum = new IntHolder(0);
                    ArrayList<CraftableInfo>  result = Utils.getCraftableInfo(true, item, Utils.deepCopyofCraftableInfo(sequreChestList), new ArrayList<>(), rest, neededNum, new HashMap<>(), this, 1, new Holder<>(true));
                    if (result == null) {
                        lock1.unlock();
                        return;
                    }
                    ArrayList<CraftableInfo> sepRest = new ArrayList<>(rest.stream().map(x->x.first).toList());
                    Utils.substractCraftableLists(result, sepRest);
                    ArrayList<CraftableInfo> needed = new ArrayList<>();
                    ArrayList<CraftableInfo> inInventory = new ArrayList<>();
                    Utils.separateItems(inInventory, needed, sequreChestList, new ArrayList<>(result));
                    rightBufferPage = new CraftableMenu(this, screen, 100, 100, item, inInventory, needed, new ArrayList<>(sepRest));
                    rightPageHasToRender = true;
                    int state = neededNum.number == 0 ? 1:-1;
                    this.setCraftingGridValue(0, new ItemStack(item), state);
                    setCraftingNumber(1);
                    System.out.println("Termino hilo nivel 1");
                    lock1.unlock();
                }
                catch (Exception e){
                    System.out.println("Error de concurrencia nivel 1");
                    e.printStackTrace();
                }
            });
            currentThread = thread;
            thread.start();

    }

    public void selectCraftMultiple_new(Item item, int count){
        if(lock1.isLocked()){
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("There is another process ongoing. Please wait until it finishes"));
            return;
        }
        this.setCraftingGridValue(0, new ItemStack(item), 0);
        this.setCraftingNumber(1);
        loadingWidget.setNumber(count);
        loadingWidget.initiateCraft();
        loadingWidget.setCurrentNumber(0);
        rigthSidePageHasToHide = true;
        showGrid();
            Thread thread = new Thread(() -> {
                lock1.lock();
                    ArrayList<CraftableInfo> result = new ArrayList<>();
                    ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
                    ArrayList<Pair<CraftableInfo, Boolean>> rest = new ArrayList<>();
                    HashMap<Pair<Item, Integer>, Pair<Item, Boolean>> map = new HashMap<>();

                for (int i = 0; i < count; ++i) {
                    IntHolder neededNum = new IntHolder(0);
                    Holder<Boolean> canCraft = new Holder<>(true);
                    Utils.combineLists(Utils.getCraftableInfo(true, item, chestCopy, new ArrayList<>(), rest, neededNum, map, this, 1, canCraft), result);
                    if(Thread.currentThread().isInterrupted()){
                        lock1.unlock();
                        return;
                    }
                    int state = neededNum.number == 0 && canCraft.value ? 1:-1;
                    this.setCraftingGridValue(0, new ItemStack(item), state);
                    this.setCraftingNumber(i+1);
                }
                ArrayList<CraftableInfo> sepRest = new ArrayList<>(rest.stream().map(x->x.first).toList());
                Utils.substractCraftableLists(result, sepRest);
                ArrayList<CraftableInfo> needed = new ArrayList<>();
                ArrayList<CraftableInfo> inInventory = new ArrayList<>();
                Utils.separateItems(inInventory, needed, this.chestList, result);
                rightBufferPage = new CraftableMenu(this, screen, 100, 100, item, inInventory, needed, sepRest, count);
                rightPageHasToRender = true;
                lock1.unlock();
            });
            currentThread = thread;
            thread.start();

    }

    public void selectCraftAll_new(Item item){
        if(lock1.isLocked()){
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("There is another process ongoing. Please wait until it finishes"));
            return;
        }
        this.setCraftingGridValue(0, new ItemStack(item), 0);
        this.setCraftingNumber(1);
        loadingWidget.setCurrentNumber(0);
        loadingWidget.setNumber(1);
        loadingWidget.initiateCraft();
        rigthSidePageHasToHide = true;
        showGrid();
        Thread thread = new Thread(() -> {
            lock1.lock();
            int count = 0;
            ArrayList<CraftableInfo> result = new ArrayList<>();
            ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
            ArrayList<Pair<CraftableInfo, Boolean>> rest = new ArrayList<>();
            HashMap<Pair<Item, Integer>, Pair<Item, Boolean>> map = new HashMap<>();
            ArrayList<CraftableInfo> prevResult;
            ArrayList<Pair<CraftableInfo, Boolean>> prevRest;
            IntHolder neededNum = new IntHolder(0);
            do{
                this.setCraftingGridValue(0, new ItemStack(item), 1);
                loadingWidget.setNumber(count);
                this.setCraftingNumber(count);
                prevResult = Utils.deepCopyofCraftableInfo(result);
                prevRest = Utils.deepCopyofRest(rest);
                Utils.combineLists(Utils.getCraftableInfo(true, item, chestCopy, new ArrayList<>(), rest, neededNum, map, this, 1, new Holder<>(true)), result);
                count++;
                if(Thread.currentThread().isInterrupted()){
                    lock1.unlock();
                    return;
                }
            }while(neededNum.number == 0);
            if(count <= 1){
                prevResult = result;
                prevRest = rest;
                this.setCraftingGridValue(0, new ItemStack(item), -1);
                loadingWidget.setNumber(count);
                this.setCraftingNumber(count);
            }
            ArrayList<CraftableInfo> sepRest = new ArrayList<>(prevRest.stream().map(x->x.first).toList());
            Utils.substractCraftableLists(prevResult, sepRest);
            ArrayList<CraftableInfo> afterNeeded = new ArrayList<>();
            ArrayList<CraftableInfo> afterInInventory = new ArrayList<>();
            Utils.separateItems(afterInInventory, afterNeeded, this.chestList, prevResult);

            rightBufferPage = new CraftableMenu(this, screen, 100, 100, item, afterInInventory, afterNeeded, sepRest, count-1);
            rightPageHasToRender = true;
            lock1.unlock();
        });
        currentThread = thread;
        thread.start();
    }

    public void selectCraft_old(Item item){
        this.setCraftingGridValue(0, new ItemStack(item), 0);
        this.setCraftingNumber(1);
        loadingWidget.initiateCraft();
        rigthSidePageHasToHide = true;
        showGrid();
        Thread thread = new Thread(() -> {
            lock1.lock();
            ArrayList<CraftableInfo> rest = new ArrayList<>();
            ArrayList<CraftableInfo> result = Utils.getCraftableInfo_old(true, item, Utils.deepCopyofCraftableInfo(this.chestList), new ArrayList<>(), rest, this, 1);
            if(result == null){
                lock1.unlock();
                return;
            }
            Utils.substractCraftableLists(result, rest);
            ArrayList<CraftableInfo> needed = new ArrayList<>();
            ArrayList<CraftableInfo> inInventory = new ArrayList<>();
            Utils.separateItems(inInventory, needed, this.chestList, result);
            if(needed.isEmpty()){
                this.setCraftingGridValue(0, new ItemStack(item), 1);
            }
            else {
                this.setCraftingGridValue(0, new ItemStack(item), -1);
            }
            rightBufferPage = new CraftableMenu(this, screen, 100, 100, item, inInventory, needed, rest);
            rightPageHasToRender = true;
            lock1.unlock();
        });
        currentThread = thread;
        thread.start();
    }
    public void selectCraftMultiple_old(Item item, int count){
        this.setCraftingGridValue(0, new ItemStack(item), 0);
        this.setCraftingNumber(1);
        loadingWidget.setCurrentNumber(0);
        loadingWidget.initiateCraft();
        rigthSidePageHasToHide = true;
        showGrid();
        Thread thread = new Thread(() -> {
            lock1.lock();
            ArrayList<CraftableInfo> result = new ArrayList<>();
            ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
            ArrayList<CraftableInfo> rest = new ArrayList<>();
            for (int i = 0; i < count; ++i) {
                Utils.combineLists(Utils.getCraftableInfo_old(true, item, chestCopy, new ArrayList<>(), rest, this, 1), result);
                this.setCraftingNumber(i + 1);
                if(Thread.currentThread().isInterrupted()){
                    lock1.unlock();
                    return;
                }
            }
            Utils.substractCraftableLists(result, rest);
            ArrayList<CraftableInfo> needed = new ArrayList<>();
            ArrayList<CraftableInfo> inInventory = new ArrayList<>();
            Utils.separateItems(inInventory, needed, this.chestList, result);
            if(needed.isEmpty()){
                this.setCraftingGridValue(0, new ItemStack(item), 1);
            }
            else {
                this.setCraftingGridValue(0, new ItemStack(item), -1);
            }
            rightBufferPage = new CraftableMenu(this, screen, 100, 100, item, inInventory, needed, rest, count);
            rightPageHasToRender = true;
            lock1.unlock();
        });
        currentThread = thread;
        thread.start();

    }

    public void selectCraftAll_old(Item item){
        this.setCraftingGridValue(0, new ItemStack(item), 0);
        this.setCraftingNumber(1);
        loadingWidget.setCurrentNumber(0);
        loadingWidget.initiateCraft();
        rigthSidePageHasToHide = true;
        showGrid();
        Thread thread = new Thread(() -> {
            lock1.lock();
            int count = 0;
            ArrayList<CraftableInfo> result = new ArrayList<>();
            ArrayList<CraftableInfo> chestCopy = Utils.deepCopyofCraftableInfo(this.chestList);
            ArrayList<CraftableInfo> rest = new ArrayList<>();
            ArrayList<CraftableInfo> needed = new ArrayList<>();
            ArrayList<CraftableInfo> prevResult;
            ArrayList<CraftableInfo> prevRest;
            do {
                this.setCraftingGridValue(0, new ItemStack(item), 1);
                loadingWidget.setNumber(count);
                this.setCraftingNumber(count);
                prevResult = Utils.deepCopyofCraftableInfo(result);
                prevRest = Utils.deepCopyofCraftableInfo(rest);
                Utils.combineLists(Utils.getCraftableInfo_old(true, item, chestCopy, new ArrayList<>(), rest, this, 1), result);
                Utils.substractCraftableLists(result, rest);
                ArrayList<CraftableInfo> inInventory = new ArrayList<>();
                Utils.separateItems(inInventory, needed, this.chestList, result);
                count++;
                if(Thread.currentThread().isInterrupted()){
                    lock1.unlock();
                    return;
                }
            } while (needed.isEmpty());
            //selectCraftMultiple(item, Math.max(1, count.get() - 1));
            if(count <= 1){
                prevResult = result;
                prevRest = rest;
                this.setCraftingGridValue(0, new ItemStack(item), -1);
                loadingWidget.setNumber(count);
                this.setCraftingNumber(count);
            }
            Utils.substractCraftableLists(prevResult, prevRest);
            ArrayList<CraftableInfo> afterNeeded = new ArrayList<>();
            ArrayList<CraftableInfo> afterInInventory = new ArrayList<>();
            Utils.separateItems(afterInInventory, afterNeeded, this.chestList, prevResult);
            rightBufferPage = new CraftableMenu(this, screen, 100, 100, item, afterInInventory, afterNeeded, prevRest, count-1);
            rightPageHasToRender = true;
            lock1.unlock();
            lock1.unlock();
        });

        thread.start();
    }
    public void selectExtract(Item item){
        this.hideGrid();
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
        this.hideGrid();
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
        this.hideGrid();
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

    public void onClose() {
        cancelProcess();
        isClosed = true;
    }

    public void cancelProcess(){
        if(currentThread != null && currentThread.isAlive()){
            currentThread.interrupt();
        }
        loadingWidget.interruptCraft();
    }


}
