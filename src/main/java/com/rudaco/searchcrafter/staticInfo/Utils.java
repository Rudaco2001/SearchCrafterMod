package com.rudaco.searchcrafter.staticInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rudaco.searchcrafter.screen.CraftableInfo;
import com.rudaco.searchcrafter.screen.PageController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Utils {


    public static void drawLineBox(PoseStack matrixStack, AABB aabb) {
        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(matrixStack, vertexConsumer, aabb, 15f, 15f, 15f, 1F);
    }

    public static ArrayList<CraftableInfo> getAllChestItems(Level level, BlockPos blockPos, Vector3 range) {
        ArrayList<CraftableInfo> chestList = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-range.x, -range.y, -range.z), blockPos.offset(range.x, range.y, range.z))) {
            if(pos.equals(blockPos))continue;
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity == null) continue;
            if (blockEntity instanceof ChestBlockEntity) {
                RandomizableContainerBlockEntity chestEntity = (RandomizableContainerBlockEntity) blockEntity;
                boolean isRepeated;
                for (int i = 0; i < chestEntity.getContainerSize(); i++) {
                    isRepeated = false;
                    ItemStack stack = chestEntity.getItem(i);
                    Item item = stack.getItem().asItem();
                    if(stack.getCount() == 0) continue;
                    for (CraftableInfo ele : chestList) {
                        if (ele.item.equals(item)) {
                            isRepeated = true;
                            ele.quant += stack.getCount();
                            break;
                        }
                    }
                    if (!isRepeated) chestList.add(new CraftableInfo(item, stack.getCount()));
                }
            }
            else if(hasInventory(blockEntity)){
                LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
                itemHandleropt.ifPresent(itemHandler -> {
                    boolean isRepeated;
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        isRepeated = false;
                        ItemStack stack = itemHandler.getStackInSlot(i);
                        Item item = stack.getItem().asItem();
                        if(stack.getCount() == 0) continue;
                        for (CraftableInfo ele : chestList) {
                            if (ele.item.equals(item)) {
                                isRepeated = true;
                                ele.quant += stack.getCount();
                                break;
                            }
                        }
                        if (!isRepeated) chestList.add(new CraftableInfo(item, stack.getCount()));
                    }
                });

            }

        }
        return chestList;
    }

    private static boolean hasInventory(BlockEntity blockEntity) {
        // Verifica si la entidad de bloque implementa la interfaz Inventory
        assert blockEntity != null;
        return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
    }

    public static boolean checkItemsInChests(Level level, BlockPos blockPos, Vector3 range, ArrayList<CraftableInfo> toRemove) {
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-range.x, -range.y, -range.z), blockPos.offset(range.x, range.y, range.z))) {
            if(pos.equals(blockPos))continue;
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity == null) continue;
            if (blockEntity instanceof ChestBlockEntity) {
                RandomizableContainerBlockEntity chestEntity = (RandomizableContainerBlockEntity) blockEntity;
                for (int i = 0; i < chestEntity.getContainerSize(); i++) {
                    ItemStack stack = chestEntity.getItem(i);
                    Item item = stack.getItem().asItem();

                    ArrayList<CraftableInfo> elementsToRemove = new ArrayList<>();
                    for (CraftableInfo ele : toRemove) {
                        if (ele.item.equals(item)) {
                            if (ele.quant > stack.getCount()) {
                                ele.quant -= stack.getCount();
                            } else if (ele.quant == stack.getCount()) {
                                elementsToRemove.add(ele);
                            } else {
                                elementsToRemove.add(ele);
                            }
                        }
                    }
                    toRemove.removeAll(elementsToRemove);
                }
            }
            else if(hasInventory(blockEntity)){
                LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
                itemHandleropt.ifPresent(itemHandler -> {
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        ItemStack stack = itemHandler.getStackInSlot(i);
                        Item item = stack.getItem().asItem();
                        ArrayList<CraftableInfo> elementsToRemove = new ArrayList<>();
                        for (CraftableInfo ele : toRemove) {
                            if (ele.item.equals(item)) {
                                if (ele.quant > stack.getCount()) {
                                    ele.quant -= stack.getCount();
                                } else if (ele.quant == stack.getCount()) {
                                    elementsToRemove.add(ele);
                                } else {
                                    elementsToRemove.add(ele);
                                }
                            }
                        }
                        toRemove.removeAll(elementsToRemove);
                    }
                });

            }
        }

        return toRemove.isEmpty();
    }

    public static boolean removeItemsFromChests(Level level, BlockPos blockPos, Vector3 range, ArrayList<CraftableInfo> toRemove) {
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-range.x, -range.y, -range.z), blockPos.offset(range.x, range.y, range.z))) {
                if(pos.equals(blockPos)) continue;
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if(blockEntity == null) continue;
                if (blockEntity instanceof ChestBlockEntity) {
                    RandomizableContainerBlockEntity chestEntity = (RandomizableContainerBlockEntity) blockEntity;
                    for (int i = 0; i < chestEntity.getContainerSize(); i++) {
                        ItemStack stack = chestEntity.getItem(i);
                        Item item = stack.getItem().asItem();

                        ArrayList<CraftableInfo> elementsToRemove = new ArrayList<>();
                        for (CraftableInfo ele : toRemove) {
                            if (ele.item.equals(item)) {
                                if (ele.quant > stack.getCount()) {
                                    ele.quant -= stack.getCount();
                                    chestEntity.removeItem(i, stack.getCount());
                                } else if (ele.quant == stack.getCount()) {
                                    elementsToRemove.add(ele);
                                    chestEntity.removeItem(i, stack.getCount());
                                } else {
                                    chestEntity.removeItem(i, ele.quant);
                                    elementsToRemove.add(ele);
                                }
                            }
                        }
                        toRemove.removeAll(elementsToRemove);
                    }
                }
                else if(hasInventory(blockEntity)){
                    LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
                    itemHandleropt.ifPresent(itemHandler -> {
                        for (int i = 0; i < itemHandler.getSlots(); i++) {
                            ItemStack stack = itemHandler.getStackInSlot(i);
                            Item item = stack.getItem().asItem();

                            ArrayList<CraftableInfo> elementsToRemove = new ArrayList<>();
                            for (CraftableInfo ele : toRemove) {
                                if (ele.item.equals(item)) {
                                    if (ele.quant > stack.getCount()) {
                                        ele.quant -= stack.getCount();
                                        itemHandler.extractItem(i, stack.getCount(), false);
                                    } else if (ele.quant == stack.getCount()) {
                                        elementsToRemove.add(ele);
                                        itemHandler.extractItem(i, stack.getCount(), false);
                                    } else {
                                        itemHandler.extractItem(i, ele.quant, false);
                                        elementsToRemove.add(ele);
                                    }
                                }
                            }
                            toRemove.removeAll(elementsToRemove);
                        }
                    });

                }
            }

        return toRemove.isEmpty();
    }

    public static void insertRestInChest(Level level, BlockPos blockPos, ArrayList<CraftableInfo> rest) {
        if (rest.isEmpty()) return;
        for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
                if(pos.equals(blockPos))continue;
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if(blockEntity == null) continue;
                if (blockEntity instanceof ChestBlockEntity) {
                    RandomizableContainerBlockEntity chestEntity = (RandomizableContainerBlockEntity) blockEntity;
                    for (int i = 0; i < chestEntity.getContainerSize(); i++) {
                        ItemStack stack = chestEntity.getItem(i);
                        Item item = stack.getItem().asItem();
                        if (stack.isEmpty()) {
                            if (rest.get(0).quant <= 64) {
                                chestEntity.setItem(i, new ItemStack(rest.get(rest.size() - 1).item, rest.get(rest.size() - 1).quant));
                                rest.remove(rest.size() - 1);
                                if (rest.isEmpty()) return;
                            } else {
                                chestEntity.setItem(i, new ItemStack(rest.get(rest.size() - 1).item, 64));
                                rest.get(rest.size() - 1).quant -= 64;
                            }
                        } else {
                            CraftableInfo match = null;
                            for (CraftableInfo ele : rest) {
                                if (ele.item.equals(item)) {
                                    match = ele;
                                    break;
                                }
                            }
                            if (match == null) continue;
                            if (match.quant + stack.getCount() <= 64) {
                                chestEntity.setItem(i, new ItemStack(item, match.quant + stack.getCount()));
                                rest.remove(match);
                                if (rest.isEmpty()) return;
                            } else {
                                chestEntity.setItem(i, new ItemStack(item, 64));
                                match.quant -= 64 - stack.getCount();
                            }
                        }
                    }
                }
                else if(hasInventory(blockEntity)){
                    LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER);
                    itemHandleropt.ifPresent(itemHandler -> {
                        for (int i = 0; i < itemHandler.getSlots(); i++) {
                            ItemStack stack = itemHandler.getStackInSlot(i);
                            Item item = stack.getItem().asItem();
                            if (stack.isEmpty()) {
                                if (rest.get(0).quant <= 64) {
                                    itemHandler.insertItem(i, new ItemStack(rest.get(rest.size() - 1).item, rest.get(rest.size() - 1).quant), false);
                                    rest.remove(rest.size() - 1);
                                    if (rest.isEmpty()) return;
                                } else {
                                    itemHandler.insertItem(i, new ItemStack(rest.get(rest.size() - 1).item, 64), false);
                                    rest.get(rest.size() - 1).quant -= 64;
                                }
                            } else {
                                CraftableInfo match = null;
                                for (CraftableInfo ele : rest) {
                                    if (ele.item.equals(item)) {
                                        match = ele;
                                        break;
                                    }
                                }
                                if (match == null) continue;
                                if (match.quant + stack.getCount() <= 64) {
                                    itemHandler.insertItem(i, new ItemStack(item, match.quant + stack.getCount()), false);
                                    rest.remove(match);
                                    if (rest.isEmpty()) return;
                                } else {
                                    itemHandler.insertItem(i, new ItemStack(item, 64), false);
                                    match.quant -= 64 - stack.getCount();
                                }
                            }
                        }
                    });
            }
        }
        for (CraftableInfo e : rest) {
            ItemStack stack = new ItemStack(e.item, e.quant);
            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
            level.addFreshEntity(itemEntity);
        }
    }


    public static ArrayList<CraftableInfo> getCraftableInfo(boolean first, Item searcheditem, ArrayList<CraftableInfo> modificableInfo, ArrayList<Item> alreadyChecked, ArrayList<Pair<CraftableInfo, Boolean>> rest, IntHolder neededCount, Map<Pair<Item,Integer>, Pair<Item, Boolean>> memoizationMap, PageController currentController, int craftIndex, Holder<Boolean> canCraft) {
        ArrayList<CraftableInfo> resultlist = new ArrayList<>();

        if(Thread.currentThread().isInterrupted()){
            return null;
        }

        for (var r : rest) {
            CraftableInfo cr = r.first;
            if (cr.item.equals(searcheditem) && cr.quant > 0) {
                cr.quant--;
                if(cr.quant == 0) rest.remove(r);
                canCraft.value = r.second;
                return resultlist;
            }
        }

        ArrayList<Pair<ItemStack, Pair<Integer, Integer>>> usedGrid = new ArrayList<>();
        ArrayList<Pair<ItemStack, Pair<Integer, Integer>>> actualGrid = new ArrayList<>();

        if(!first){
            if(isInInventory(searcheditem, modificableInfo)){
                resultlist.add(new CraftableInfo(searcheditem, 1));
                return resultlist;
            }
        }


        if (alreadyChecked.contains(searcheditem)) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            neededCount.number++;
            canCraft.value = false;
            return resultlist;
        }

        alreadyChecked.add(searcheditem);
        ArrayList<CraftingRecipe> recipes = getRecipe(searcheditem);
        if (recipes.isEmpty()) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            neededCount.number++;
            canCraft.value = false;
            return resultlist;
        }



        ArrayList<CraftableInfo> bestRecipeResult = new ArrayList<>();
        ArrayList<Pair<CraftableInfo,Boolean>> bestRecipeRest = new ArrayList<>();
        ArrayList<CraftableInfo> bestRecipeMod = new ArrayList<>();
        int bestRecipeNeeded = -1;
        CraftingRecipe bestRecipe = null;
        for(CraftingRecipe recipe: recipes){
            ArrayList<CraftableInfo> finalResult = new ArrayList<>();
            ArrayList<Pair<CraftableInfo, Boolean>> persistRest = deepCopyofRest(rest);
            int finalNeeded = 0;
            Holder<Boolean> finalCanCraft = new Holder<>(true);
            ArrayList<CraftableInfo> persistMod = deepCopyofCraftableInfo(modificableInfo);
            if(first){
                craftIndex = 1;
            }
            if(first){
                for(int r = 1; r < 10; r++){
                    currentController.setCraftingGridValue(r, null, 0);
                }
            }
            for (Ingredient ing: recipe.getIngredients()){
                if(ing.getItems().length == 0){
                    if(first){
                        currentController.setCraftingGridValue(craftIndex, null, 0);
                        ++craftIndex;
                    }
                    continue;
                }
                int bestNeeded = -1;
                Holder<Boolean> bestCanCraft = new Holder<>(true);
                ArrayList<CraftableInfo> bestResult = new ArrayList<>();
                ArrayList<Pair<CraftableInfo, Boolean>> bestRest = new ArrayList<>();
                ArrayList<CraftableInfo> bestMod = new ArrayList<>();
                boolean found = false;
                Item bestVariant = null;
                if(memoizationMap.containsKey(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length))){
                    found = true;
                    ArrayList<CraftableInfo> copyMod = deepCopyofCraftableInfo(persistMod);
                    ArrayList<CraftableInfo> result;
                    ArrayList<Pair<CraftableInfo, Boolean>> copyRest = deepCopyofRest(persistRest);
                    IntHolder holder = new IntHolder(0);
                    currentController.setCraftingGridValue(craftIndex, new ItemStack(ing.getItems()[0].getItem()), 0);
                    bestVariant = memoizationMap.get(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length)).first;
                    Holder<Boolean> canCraftMaterial = new Holder<>(true);
                    result = getCraftableInfo(false, bestVariant, copyMod, new ArrayList<>(alreadyChecked), copyRest, holder, memoizationMap, currentController, craftIndex, canCraftMaterial);
                    if(result == null){
                        return null;
                    }
                    bestNeeded = holder.number;
                    bestResult = result;
                    bestMod = copyMod;
                    bestRest = copyRest;
                    bestCanCraft = canCraftMaterial;
                    if(memoizationMap.get(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length)).second && bestNeeded != 0){
                        bestNeeded = -1;
                        found = false;
                    }
                }
                if(!found){
                    for (int i = 0; i < ing.getItems().length; i++) {
                        ArrayList<CraftableInfo> copyMod = deepCopyofCraftableInfo(persistMod);
                        ArrayList<CraftableInfo> result;
                        ArrayList<Pair<CraftableInfo,Boolean>> copyRest = deepCopyofRest(persistRest);
                        IntHolder holder = new IntHolder(0);
                        Holder<Boolean> canCraftMaterial = new Holder<>(true);
                        currentController.setCraftingGridValue(craftIndex, new ItemStack(ing.getItems()[i].getItem()), 0);
                        result = getCraftableInfo(false, ing.getItems()[i].getItem(), copyMod, new ArrayList<>(alreadyChecked), copyRest, holder, memoizationMap, currentController, craftIndex, canCraftMaterial);
                        if(result == null){
                            return null;
                        }
                        if(bestNeeded == -1 || holder.number < bestNeeded){
                            bestNeeded = holder.number;
                            bestResult = result;
                            bestMod = copyMod;
                            bestRest = copyRest;
                            bestVariant = ing.getItems()[i].getItem();
                            bestCanCraft = canCraftMaterial;
                            if(bestNeeded == 0) break;
                        }
                    }
                }
                persistMod = bestMod;
                combineLists(bestResult, finalResult);
                persistRest = bestRest;
                finalNeeded += bestNeeded;
                finalCanCraft.value = finalCanCraft.value && bestCanCraft.value;
                if(!found && ing.getItems().length > 1){
                    if(bestNeeded == getCraftableListCount(bestResult) && bestNeeded != 0){
                        memoizationMap.put(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length), new Pair<>(bestVariant, false));
                    }
                    else {
                        memoizationMap.put(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length), new Pair<>(bestVariant, true));
                    }
                }
                if(first){
                    int state = bestNeeded == 0 && bestCanCraft.value ? 1:-1;
                    currentController.setCraftingGridValue(craftIndex, new ItemStack(bestVariant), state);
                    actualGrid.add(new Pair<>(new ItemStack(bestVariant), new Pair<>(craftIndex, state)));
                    craftIndex++;
                    if(recipe instanceof ShapedRecipe shapedRecipe){
                        while((craftIndex-1) % 3 > shapedRecipe.getWidth()-1){
                            craftIndex++;
                        }
                    }
                }
            }
            if(bestRecipeNeeded == -1 || finalNeeded < bestRecipeNeeded){
                canCraft.value = finalCanCraft.value;
                bestRecipeNeeded = finalNeeded;
                bestRecipeResult = finalResult;
                bestRecipeRest = persistRest;
                bestRecipeMod = persistMod;
                bestRecipe = recipe;
                if(first){
                    usedGrid = new ArrayList<>(actualGrid);
                    actualGrid.clear();
                }
                if(bestRecipeNeeded == 0) break;
            }
        }
        if(first){
            for(int i = 1; i < 10; i++){
                currentController.setCraftingGridValue(i, null, 0);
            }
            for (var e:usedGrid){
                currentController.setCraftingGridValue(e.second.first, e.first, e.second.second);
            }
        }
        int count = bestRecipe.getResultItem().getCount();
        if (count > 1) {
            bestRecipeRest.add(new Pair<>(new CraftableInfo(searcheditem, count - 1), bestRecipeNeeded==0 && canCraft.value));
        }
        neededCount.number = bestRecipeNeeded;
        modificableInfo.clear();
        modificableInfo.addAll(bestRecipeMod);
        rest.clear();
        rest.addAll(bestRecipeRest);
        return bestRecipeResult;
    }



    public static void addToCraftableList(ArrayList<CraftableInfo> list, CraftableInfo element) {
        boolean found = false;
        for (CraftableInfo e : list) {
            if (e.item.equals(element.item)) {
                e.quant += element.quant;
                found = true;
                break;
            }
        }
        if (!found) list.add(element);
    }

    public static void combineLists(ArrayList<CraftableInfo> list1, ArrayList<CraftableInfo> list2) {
        boolean found;
        if(list1 == null || list2 == null){
            return;
        }
        for (CraftableInfo ele1 : list1) {
            found = false;
            for (CraftableInfo ele2 : list2) {
                if (ele1.item.equals(ele2.item)) {
                    ele2.quant += ele1.quant;
                    found = true;
                    break;
                }
            }
            if (!found) {
                list2.add(ele1);
            }
        }
    }

    public static ArrayList<CraftingRecipe> getRecipe(Item searcheditem) {
        assert Minecraft.getInstance().level != null;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        Collection<CraftingRecipe> craftingRecipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);
        ArrayList<CraftingRecipe> re = new ArrayList<>();
        for (CraftingRecipe recipe : craftingRecipes) {
            Item item = recipe.getResultItem().getItem();
            if (item.equals(searcheditem)) {
                re.add(recipe);
            }
        }
        return re;
    }

    public static CraftingRecipe selectBestRecipe(ArrayList<CraftingRecipe> recipe, ArrayList<CraftableInfo> modificableInfo) {
        CraftingRecipe someMatch = null;
        CraftingRecipe noMatch = null;

        boolean someMatchbool;
        boolean allMatchbool;
        boolean found;
        for (CraftingRecipe r : recipe) {

            someMatchbool = false;
            allMatchbool = true;
            for (Ingredient ing : r.getIngredients()) {
                found = false;
                for (int i = 0; i < ing.getItems().length; i++) {
                    if (isInInventory(ing.getItems()[i].getItem(), modificableInfo)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    someMatchbool = true;
                } else {
                    allMatchbool = false;
                }
            }
            if (allMatchbool) return r;
            else if (someMatchbool && someMatch == null) someMatch = r;
            else if (noMatch == null) noMatch = r;
        }
        if (someMatch == null) return noMatch;
        return someMatch;
    }

    public static ArrayList<Pair<Item, Pair<Boolean, Integer>>> getRecipeItems(boolean first, CraftingRecipe recipe, ArrayList<CraftableInfo> modificableInfo, PageController currentController, int gridIndex) {
        boolean found;
        ArrayList<Pair<Item,Pair<Boolean, Integer>>> items = new ArrayList<>();
        for (Ingredient ing : recipe.getIngredients()) {
            found = false;
            for (int i = 0; i < ing.getItems().length; i++) {
                if(first){
                    currentController.setCraftingGridValue(gridIndex, ing.getItems()[i], 0);
                }
                if (isInInventory(ing.getItems()[i].getItem(), modificableInfo)) {
                    found = true;
                    items.add(new Pair<>(ing.getItems()[i].getItem(),new Pair<>(true, gridIndex)));
                    break;
                }
            }
            if (!found && ing.getItems().length > 0) items.add(new Pair<>(ing.getItems()[0].getItem(), new Pair<>(false, gridIndex)));
            if(first){
                if(ing.getItems().length == 0) currentController.setCraftingGridValue(gridIndex, null, 0);
                gridIndex++;
            }

        }
        return items;
    }

    public static boolean isInInventory(Item item, ArrayList<CraftableInfo> modificableInfo) {
        for (CraftableInfo info : modificableInfo) {
            if (item.equals(info.item)) {
                if (info.quant > 0) {
                    info.quant--;
                    return true;
                } else return false;
            }
        }
        return false;
    }


    public static void separateItems(ArrayList<CraftableInfo> inInventory, ArrayList<CraftableInfo> needed, ArrayList<CraftableInfo> inChest, ArrayList<CraftableInfo> required) {
        boolean found;
        for (CraftableInfo req : required) {
            found = false;
            for (CraftableInfo inChst : inChest) {
                if (req.item.equals(inChst.item)) {
                    if (req.quant <= inChst.quant) inInventory.add(new CraftableInfo(req.item, req.quant));
                    else {
                        inInventory.add(new CraftableInfo(req.item, inChst.quant));
                        needed.add(new CraftableInfo(req.item, req.quant - inChst.quant));
                    }
                    found = true;
                    break;
                }
            }
            if (!found) needed.add(new CraftableInfo(req.item, req.quant));
        }

    }




    public static ArrayList<CraftableInfo> deepCopyofCraftableInfo(ArrayList<CraftableInfo> list) {
        ArrayList<CraftableInfo> result = new ArrayList<>();
        for (CraftableInfo info : list) {
            result.add(new CraftableInfo(info));
        }
        return result;
    }

    public static ArrayList<Pair<CraftableInfo, Boolean>> deepCopyofRest(ArrayList<Pair<CraftableInfo, Boolean>> list) {
        ArrayList<Pair<CraftableInfo, Boolean>> result = new ArrayList<>();
        for (var info : list) {
            result.add(new Pair<>(new CraftableInfo(info.first), info.second));
        }
        return result;
    }


    public static void substractRestWithResult(ArrayList<Pair<CraftableInfo, Boolean>> rest, ArrayList<CraftableInfo> result) {
        for (int i = 0; i < rest.size(); i++) {
            CraftableInfo ele1 = rest.get(i).first;
            for (int j = 0; j < result.size(); j++) {
                CraftableInfo ele2 = result.get(j);
                if (ele1.item.equals(ele2.item)) {
                    if (ele1.quant > ele2.quant) {
                        ele1.quant -= ele2.quant;
                        result.remove(j);
                        j--; // Adjust index to account for removed element
                    } else if (ele1.quant == ele2.quant) {
                        rest.remove(i);
                        result.remove(j);
                        i--; // Adjust index to account for removed element
                        j--; // Adjust index to account for removed element
                    } else {
                        ele2.quant -= ele1.quant;
                        rest.remove(i);
                        i--; // Adjust index to account for removed element
                    }
                }
            }
        }
    }

    public static void substractCraftableLists(ArrayList<CraftableInfo> list1, ArrayList<CraftableInfo> list2) {

        for (int i = 0; i < list1.size(); i++) {
            CraftableInfo ele1 = list1.get(i);
            for (int j = 0; j < list2.size(); j++) {
                CraftableInfo ele2 = list2.get(j);
                if (ele1.item.equals(ele2.item)) {
                    if (ele1.quant > ele2.quant) {
                        ele1.quant -= ele2.quant;
                        list2.remove(j);
                        j--; // Adjust index to account for removed element
                    } else if (ele1.quant == ele2.quant) {
                        list1.remove(i);
                        list2.remove(j);
                        i--; // Adjust index to account for removed element
                        j--; // Adjust index to account for removed element
                    } else {
                        ele2.quant -= ele1.quant;
                        list1.remove(i);
                        i--; // Adjust index to account for removed element
                    }

                }
            }
        }
    }

    public static int getCraftableListCount(ArrayList<CraftableInfo> list){
        int count = 0;
        for (CraftableInfo info : list) {
            count += info.quant;
        }
        return count;
    }


    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static ArrayList<CraftableInfo> getCraftableInfo_old(boolean first, Item searcheditem, ArrayList<CraftableInfo> modificableInfo, ArrayList<Item> alreadyChecked, ArrayList<CraftableInfo> rest, PageController currentController, int craftIndex) {
        ArrayList<CraftableInfo> resultlist = new ArrayList<>();

        if(Thread.currentThread().isInterrupted()){
            return null;
        }

        for (CraftableInfo cr : rest) {
            if (cr.item.equals(searcheditem) && cr.quant > 0) {
                cr.quant--;
                return resultlist;
            }
        }

        if (alreadyChecked.contains(searcheditem)) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            return resultlist;
        }

        alreadyChecked.add(searcheditem);
        ArrayList<CraftingRecipe> recipes = getRecipe(searcheditem);
        if (recipes.isEmpty()) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            return resultlist;
        }

        CraftingRecipe recipe = selectBestRecipe(recipes, deepCopyofCraftableInfo(modificableInfo));
        int count = recipe.getResultItem().getCount();
        if (count > 1) {
            rest.add(new CraftableInfo(searcheditem, count - 1));
        }
        if(first){
            for(int r = recipe.getIngredients().size() + 1; r < 10; r++){
                currentController.setCraftingGridValue(r, null, 0);
            }
        }
        ArrayList<Pair<Item,Pair<Boolean, Integer>>> intermediateList = getRecipeItems(first, recipe, modificableInfo, currentController, craftIndex);
        for (Pair<Item,Pair<Boolean, Integer>> i : intermediateList) {
            if (i.second.first) addToCraftableList(resultlist, new CraftableInfo(i.first, 1));
            else{
                ArrayList<CraftableInfo> result = getCraftableInfo_old(false, i.first, modificableInfo, new ArrayList<>(alreadyChecked), rest, currentController, i.second.second);
                if(result == null){
                    return null;
                }
                if(first){
                    currentController.setCraftingGridValue(i.second.second, new ItemStack(i.first), 0);
                }
                combineLists(result, resultlist);
            }
        }
        return resultlist;
    }

    public static boolean isReversibleCraft(CraftingRecipe recipe){
        Item result = recipe.getResultItem().getItem();
        boolean entered = false;
        for(Ingredient ing: recipe.getIngredients()){
            if(ing.getItems().length == 0) continue;
            for(CraftingRecipe recipe1: getRecipe(ing.getItems()[0].getItem())){
                for(Ingredient ing2 :recipe1.getIngredients()){
                    if(ing2.getItems().length == 0) continue;
                    if(ing2.getItems()[0].getItem().equals(result)){
                        return true;
                    }
                    entered = true;
                }

            }
            if(entered) return false;
        }
        return false;
    }

}
