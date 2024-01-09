package com.rudaco.searchcrafter.network.packet;

import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketC2SInfoReq {
    private BlockPos pos;


    // Constructor sin argumentos necesario para Forge
    public PacketC2SInfoReq() {}

    // Constructor para tu mensaje
    public PacketC2SInfoReq(BlockPos pos) {
        this.pos = pos;
    }

    public PacketC2SInfoReq(FriendlyByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }


    public PacketC2SInfoReq fromBytes(FriendlyByteBuf buf) {
        return new PacketC2SInfoReq(buf);
    }




    public void toBytes(FriendlyByteBuf buf) {
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
                BlockEntity ent = level.getBlockEntity(pos);
                if(ent instanceof SearchCrafterTable table) {
                    MySimpleChannel.sendToPlayer(new PacketS2CRange(table.range, pos), player);
                    MySimpleChannel.sendToPlayer(new PacketS2CVisible(table.renderActive, pos), player);
                }
            }
        });
        return true;
    }

}
