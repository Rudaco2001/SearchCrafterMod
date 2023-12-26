package com.rudaco.searchcrafter.network.packet;

import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.screen.CraftableInfo;
import com.rudaco.searchcrafter.staticInfo.StaticInfo;
import com.rudaco.searchcrafter.staticInfo.Utils;
import com.rudaco.searchcrafter.staticInfo.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PacketC2SRange {

    public Vector3 range;
    public BlockPos pos;


    // Constructor sin argumentos necesario para Forge
    public PacketC2SRange() {}

    // Constructor para tu mensaje
    public PacketC2SRange(Vector3 range, BlockPos pos) {
        this.range = range;
        this.pos = pos;
    }

    public PacketC2SRange(FriendlyByteBuf buf) {
        range = new Vector3(buf.readInt(), buf.readInt(), buf.readInt());
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }


    public PacketC2SRange fromBytes(FriendlyByteBuf buf) {
        return new PacketC2SRange(buf);
    }




    public void toBytes(FriendlyByteBuf buf) {
        // Escribe los datos en el paquete de red
        buf.writeInt(range.x);
        buf.writeInt(range.y);
        buf.writeInt(range.z);
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
                    range.x = Math.min(range.x, 150);
                    range.y = Math.min(range.y, 150);
                    range.z = Math.min(range.z, 150);
                    table.setRange(range);
                    MySimpleChannel.sendToPlayer(new PacketS2C(Utils.getAllChestItems(level, pos, table.range)), player);
                    for (ServerPlayer player1 : player.getServer().getPlayerList().getPlayers()) {
                        MySimpleChannel.sendToPlayer(new PacketS2CRange(range, pos), player1);
                    }
                    MySimpleChannel.sendToPlayer(new PacketS2CRange(range, pos), player);
                }
            }
        });
        return true;
    }


}
