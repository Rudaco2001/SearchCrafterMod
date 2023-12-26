package com.rudaco.searchcrafter.network.packet;

import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.screen.CraftableInfo;
import com.rudaco.searchcrafter.staticInfo.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PacketC2S2 {
    private CraftableInfo item;
    private BlockPos pos;

    // Constructor sin argumentos necesario para Forge
    public PacketC2S2() {}

    // Constructor para tu mensaje
    public PacketC2S2(CraftableInfo item, BlockPos pos) {
        this.item = item;
        this.pos = pos;
    }

    public PacketC2S2(FriendlyByteBuf buf) {
        item = CraftableInfo.fromBytes(buf);
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }


    public PacketC2S2 fromBytes(FriendlyByteBuf buf) {
        return new PacketC2S2(buf);
    }




    public void toBytes(FriendlyByteBuf buf) {
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
            if (block instanceof SearchCrafterTableBlock) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof SearchCrafterTable table) {
                    if(table.canGenerateResultItem(item.item)){
                        ArrayList<CraftableInfo> list = new ArrayList<>();
                        list.add(new CraftableInfo(item));
                        if(Utils.checkItemsInChests(level, pos, table.range, Utils.deepCopyofCraftableInfo(Utils.deepCopyofCraftableInfo(list)))){
                            Utils.removeItemsFromChests(level, pos, table.range, list);
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
