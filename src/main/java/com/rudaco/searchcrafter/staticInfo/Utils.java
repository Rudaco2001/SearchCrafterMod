package com.rudaco.searchcrafter.staticInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rudaco.searchcrafter.screen.CraftableInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
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
                    LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                    itemHandleropt.ifPresent(itemHandler -> {
                        boolean isRepeated;
                        for (int i = 0; i < itemHandler.getSlots(); i++) {
                            isRepeated = false;
                            ItemStack stack = itemHandler.getStackInSlot(i);
                            Item item = stack.getItem().asItem();
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
        return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
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
                    LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
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
                    LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
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
                    LazyOptional<IItemHandler>  itemHandleropt = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
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


    public static ArrayList<CraftableInfo> getCraftableInfo(boolean first, Item searcheditem, ArrayList<CraftableInfo> modificableInfo, ArrayList<Item> alreadyChecked, ArrayList<CraftableInfo> rest, IntHolder neededCount, Map<Pair<Item,Integer>, Pair<Item, Boolean>> memoizationMap) {
        ArrayList<CraftableInfo> resultlist = new ArrayList<>();

        for (CraftableInfo cr : rest) {
            if (cr.item.equals(searcheditem) && cr.quant > 0) {
                cr.quant--;
                if(cr.quant == 0) rest.remove(cr);
                return resultlist;
            }
        }

        if(!first){
            if(isInInventory(searcheditem, modificableInfo)){
                resultlist.add(new CraftableInfo(searcheditem, 1));
                return resultlist;
            }
        }

        if (alreadyChecked.contains(searcheditem)) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            neededCount.number++;
            return resultlist;
        }

        alreadyChecked.add(searcheditem);
        ArrayList<CraftingRecipe> recipes = getRecipe(searcheditem);
        if (recipes.isEmpty()) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            neededCount.number++;
            return resultlist;
        }

        ArrayList<CraftableInfo> bestRecipeResult = new ArrayList<>();
        ArrayList<CraftableInfo> bestRecipeRest = new ArrayList<>();
        ArrayList<CraftableInfo> bestRecipeMod = new ArrayList<>();
        int bestRecipeNeeded = -1;
        CraftingRecipe bestRecipe = null;
        for(CraftingRecipe recipe: recipes){
            ArrayList<CraftableInfo> finalResult = new ArrayList<>();
            ArrayList<CraftableInfo> persistRest = deepCopyofCraftableInfo(rest);
            int finalNeeded = 0;
            ArrayList<CraftableInfo> persistMod = deepCopyofCraftableInfo(modificableInfo);
            for (Ingredient ing: recipe.getIngredients()){
                if(ing.getItems().length == 0) continue;
                int bestNeeded = -1;
                ArrayList<CraftableInfo> bestResult = new ArrayList<>();
                ArrayList<CraftableInfo> bestRest = new ArrayList<>();
                ArrayList<CraftableInfo> bestMod = new ArrayList<>();
                boolean found = false;
                Item bestVariant = null;
                if(memoizationMap.containsKey(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length))){
                    found = true;
                    ArrayList<CraftableInfo> copyMod = deepCopyofCraftableInfo(persistMod);
                    ArrayList<CraftableInfo> result;
                    ArrayList<CraftableInfo> copyRest = deepCopyofCraftableInfo(persistRest);
                    IntHolder holder = new IntHolder(0);
                    result = getCraftableInfo(false, memoizationMap.get(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length)).first, copyMod, new ArrayList<>(alreadyChecked), copyRest, holder, memoizationMap);
                    bestNeeded = holder.number;
                    bestResult = result;
                    bestMod = copyMod;
                    bestRest = copyRest;
                    if(memoizationMap.get(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length)).second && bestNeeded != 0){
                        bestNeeded = -1;
                        found = false;
                    }
                }
                if(!found){
                    for (int i = 0; i < ing.getItems().length; i++) {
                        ArrayList<CraftableInfo> copyMod = deepCopyofCraftableInfo(persistMod);
                        ArrayList<CraftableInfo> result;
                        ArrayList<CraftableInfo> copyRest = deepCopyofCraftableInfo(persistRest);
                        IntHolder holder = new IntHolder(0);
                        result = getCraftableInfo(false, ing.getItems()[i].getItem(), copyMod, new ArrayList<>(alreadyChecked), copyRest, holder, memoizationMap);
                        if(bestNeeded == -1 || holder.number < bestNeeded){
                            bestNeeded = holder.number;
                            bestResult = result;
                            bestMod = copyMod;
                            bestRest = copyRest;
                            bestVariant = ing.getItems()[i].getItem();
                            if(bestNeeded == 0) break;
                        }
                    }
                }
                persistMod = bestMod;
                combineLists(bestResult, finalResult);
                persistRest = bestRest;
                finalNeeded += bestNeeded;
                if(!found && ing.getItems().length > 1){
                    if(bestNeeded == getCraftableListCount(bestResult) && bestNeeded != 0){
                        memoizationMap.put(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length), new Pair<>(bestVariant, false));
                    }
                    else {
                        memoizationMap.put(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length), new Pair<>(bestVariant, true));
                    }
                }
            }
            if(bestRecipeNeeded == -1 || finalNeeded < bestRecipeNeeded){
                bestRecipeNeeded = finalNeeded;
                bestRecipeResult = finalResult;
                bestRecipeRest = persistRest;
                bestRecipeMod = persistMod;
                bestRecipe = recipe;
                if(bestRecipeNeeded == 0) break;
            }
        }
        int count = bestRecipe.getResultItem().getCount();
        if (count > 1) {
            bestRecipeRest.add(new CraftableInfo(searcheditem, count - 1));
        }
        neededCount.number = bestRecipeNeeded;
        modificableInfo.clear();
        modificableInfo.addAll(bestRecipeMod);
        rest.clear();
        rest.addAll(bestRecipeRest);
        return bestRecipeResult;
    }


    public static ArrayList<CraftableInfo> getCraftableInfo(boolean first, Item searcheditem, ArrayList<CraftableInfo> modificableInfo, ArrayList<Item> alreadyChecked, CopyOnWriteArrayList<CraftableInfo> rest, IntHolder neededCount, Map<Pair<Item,Integer>, Pair<Item, Boolean>> memoizationMap) {
        ArrayList<CraftableInfo> resultlist = new ArrayList<>();

        for (CraftableInfo cr : rest) {
            if (cr.item.equals(searcheditem) && cr.quant > 0) {
                cr.quant--;
                if(cr.quant == 0) rest.remove(cr);
                return resultlist;
            }
        }

        if(!first){
            if(isInInventory(searcheditem, modificableInfo)){
                resultlist.add(new CraftableInfo(searcheditem, 1));
                return resultlist;
            }
        }

        if (alreadyChecked.contains(searcheditem)) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            neededCount.number++;
            return resultlist;
        }

        alreadyChecked.add(searcheditem);
        ArrayList<CraftingRecipe> recipes = getRecipe(searcheditem);
        if (recipes.isEmpty()) {
            resultlist.add(new CraftableInfo(searcheditem, 1));
            neededCount.number++;
            return resultlist;
        }

        ArrayList<CraftableInfo> bestRecipeResult = new ArrayList<>();
        ArrayList<CraftableInfo> bestRecipeRest = new ArrayList<>();
        ArrayList<CraftableInfo> bestRecipeMod = new ArrayList<>();
        int bestRecipeNeeded = -1;
        CraftingRecipe bestRecipe = null;
        for(CraftingRecipe recipe: recipes){
            ArrayList<CraftableInfo> finalResult = new ArrayList<>();
            ArrayList<CraftableInfo> persistRest = deepCopyofCraftableInfo(new ArrayList<>(rest));
            int finalNeeded = 0;
            ArrayList<CraftableInfo> persistMod = deepCopyofCraftableInfo(modificableInfo);
            for (Ingredient ing: recipe.getIngredients()){
                if(ing.getItems().length == 0) continue;
                int bestNeeded = -1;
                ArrayList<CraftableInfo> bestResult = new ArrayList<>();
                ArrayList<CraftableInfo> bestRest = new ArrayList<>();
                ArrayList<CraftableInfo> bestMod = new ArrayList<>();
                boolean found = false;
                Item bestVariant = null;
                if(memoizationMap.containsKey(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length))){
                    found = true;
                    ArrayList<CraftableInfo> copyMod = deepCopyofCraftableInfo(persistMod);
                    ArrayList<CraftableInfo> result;
                    ArrayList<CraftableInfo> copyRest = deepCopyofCraftableInfo(persistRest);
                    IntHolder holder = new IntHolder(0);
                    result = getCraftableInfo(false, memoizationMap.get(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length)).first, copyMod, new ArrayList<>(alreadyChecked), copyRest, holder, memoizationMap);
                    bestNeeded = holder.number;
                    bestResult = result;
                    bestMod = copyMod;
                    bestRest = copyRest;
                    if(memoizationMap.get(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length)).second && bestNeeded != 0){
                        bestNeeded = -1;
                        found = false;
                    }
                }
                if(!found){
                    for (int i = 0; i < ing.getItems().length; i++) {
                        ArrayList<CraftableInfo> copyMod = deepCopyofCraftableInfo(persistMod);
                        ArrayList<CraftableInfo> result;
                        ArrayList<CraftableInfo> copyRest = deepCopyofCraftableInfo(persistRest);
                        IntHolder holder = new IntHolder(0);
                        result = getCraftableInfo(false, ing.getItems()[i].getItem(), copyMod, new ArrayList<>(alreadyChecked), copyRest, holder, memoizationMap);
                        if(bestNeeded == -1 || holder.number < bestNeeded){
                            bestNeeded = holder.number;
                            bestResult = result;
                            bestMod = copyMod;
                            bestRest = copyRest;
                            bestVariant = ing.getItems()[i].getItem();
                            if(bestNeeded == 0) break;
                        }
                    }
                }
                persistMod = bestMod;
                combineLists(bestResult, finalResult);
                persistRest = bestRest;
                finalNeeded += bestNeeded;
                if(!found && ing.getItems().length > 1){
                    if(bestNeeded == getCraftableListCount(bestResult) && bestNeeded != 0){
                        memoizationMap.put(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length), new Pair<>(bestVariant, false));
                    }
                    else {
                        memoizationMap.put(new Pair<>(ing.getItems()[0].getItem(), ing.getItems().length), new Pair<>(bestVariant, true));
                    }
                }
            }
            if(bestRecipeNeeded == -1 || finalNeeded < bestRecipeNeeded){
                bestRecipeNeeded = finalNeeded;
                bestRecipeResult = finalResult;
                bestRecipeRest = persistRest;
                bestRecipeMod = persistMod;
                bestRecipe = recipe;
                if(bestRecipeNeeded == 0) break;
            }
        }
        int count = bestRecipe.getResultItem().getCount();
        if (count > 1) {
            bestRecipeRest.add(new CraftableInfo(searcheditem, count - 1));
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

        boolean someMatchbool = false;
        boolean allMatchbool = true;
        boolean found = false;
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

    public static ArrayList<Pair<Item, Boolean>> getRecipeItems(CraftingRecipe recipe, ArrayList<CraftableInfo> modificableInfo) {
        boolean found;
        ArrayList<Pair<Item, Boolean>> items = new ArrayList<>();
        found = false;
        for (Ingredient ing : recipe.getIngredients()) {
            found = false;
            for (int i = 0; i < ing.getItems().length; i++) {
                if (isInInventory(ing.getItems()[i].getItem(), modificableInfo)) {
                    found = true;
                    items.add(new Pair<>(ing.getItems()[i].getItem(), true));
                    break;
                }
            }
            if (!found && ing.getItems().length > 0) items.add(new Pair<>(ing.getItems()[0].getItem(), false));
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

    public static void substractCraftableLists(CopyOnWriteArrayList<CraftableInfo> list1, CopyOnWriteArrayList<CraftableInfo> list2) {

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

    public static ArrayList<CraftableInfo> getCraftableInfo_old(Item searcheditem, ArrayList<CraftableInfo> modificableInfo, ArrayList<Item> alreadyChecked, ArrayList<CraftableInfo> rest) {
        ArrayList<CraftableInfo> resultlist = new ArrayList<>();

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
        ArrayList<Pair<Item, Boolean>> intermediateList = getRecipeItems(recipe, modificableInfo);
        for (Pair<Item, Boolean> i : intermediateList) {
            if (i.second) addToCraftableList(resultlist, new CraftableInfo(i.first, 1));
            else
                combineLists(getCraftableInfo_old(i.first, modificableInfo, new ArrayList<>(alreadyChecked), rest), resultlist);
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
