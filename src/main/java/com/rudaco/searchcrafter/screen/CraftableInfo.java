package com.rudaco.searchcrafter.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CraftableInfo {




    public int quant;
    public Item item;

    public  CraftableInfo(Item item){


        this.item = item;
        this.quant = -1;
    }
    public  CraftableInfo(Item item, int quant){
        this.item = item;
        this.quant = quant;
    }

    public CraftableInfo(CraftableInfo info) {
        this.item = info.item;
        this.quant = info.quant;

    }

    public void toBytes(FriendlyByteBuf buf) {

        buf.writeInt(quant);
        buf.writeItem(new ItemStack(item)); // Asumiendo que item es un ItemStack


    }

    public static CraftableInfo fromBytes(FriendlyByteBuf buf) {

        int quant = buf.readInt();
        Item item = buf.readItem().getItem();
        CraftableInfo craftableInfo = new CraftableInfo(item,quant);
        return craftableInfo;
    }
}
