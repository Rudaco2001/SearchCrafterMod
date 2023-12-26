package com.rudaco.searchcrafter.network.packet;

import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.screen.CraftableInfo;
import com.rudaco.searchcrafter.staticInfo.StaticInfo;
import com.rudaco.searchcrafter.staticInfo.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PacketC2S {
    private ArrayList<CraftableInfo> toUse;
    private ArrayList<CraftableInfo> rest;
    private CraftableInfo item;
    private BlockPos pos;



    // Constructor sin argumentos necesario para Forge
    public PacketC2S() {}

    // Constructor para tu mensaje
    public PacketC2S(ArrayList<CraftableInfo> toUse, ArrayList<CraftableInfo> rest, CraftableInfo item, BlockPos pos) {
        this.toUse = toUse;
        this.rest = rest;
        this.item = item;
        this.pos = pos;
    }

    public PacketC2S(FriendlyByteBuf buf) {
        ArrayList<CraftableInfo> toUseList = new ArrayList<>();
        ArrayList<CraftableInfo> restList = new ArrayList<>();
        int toUselistSize = buf.readInt();
        for (int i = 0; i < toUselistSize; i++) {
            toUseList.add(CraftableInfo.fromBytes(buf));
        }
        this.toUse = toUseList;
        int restlistSize = buf.readInt();
        for (int i = 0; i < restlistSize; i++) {
            restList.add(CraftableInfo.fromBytes(buf));
        }
        this.rest = restList;

        item = CraftableInfo.fromBytes(buf);

        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }


    public PacketC2S fromBytes(FriendlyByteBuf buf) {
        return new PacketC2S(buf);
    }




    public void toBytes(FriendlyByteBuf buf) {
        // Escribe los datos en el paquete de red
        buf.writeInt(toUse.size());
        for (CraftableInfo craftableInfo : toUse) {
            craftableInfo.toBytes(buf);
        }
        buf.writeInt(rest.size());
        for (CraftableInfo craftableInfo : rest) {
            craftableInfo.toBytes(buf);
        }
        item.toBytes(buf);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(()->{
            ServerPlayer player = supplier.get().getSender();
            Level level = player.getServer().getLevel(player.level.dimension());

            BlockState blockState = level.getBlockState(pos);
            Block block = blockState.getBlock();
            if (block instanceof SearchCrafterTableBlock searchBlock) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof SearchCrafterTable table) {
                    if(table.canGenerateResultItem(item.item)){
                        if(Utils.checkItemsInChests(level, pos, table.range, Utils.deepCopyofCraftableInfo(toUse))){
                            Utils.removeItemsFromChests(level, pos, table.range, toUse);
                            Utils.insertRestInChest(level,pos,rest);
                            table.generateResultItem(item.item, item.quant);
                            MySimpleChannel.sendToPlayer(new PacketS2C(Utils.getAllChestItems(level,pos, table.range)),player);
                        }
                    }
                }
            }
        });
        return true;
    }

}
