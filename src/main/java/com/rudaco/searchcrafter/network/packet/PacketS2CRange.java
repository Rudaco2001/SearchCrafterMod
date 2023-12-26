package com.rudaco.searchcrafter.network.packet;

import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.staticInfo.StaticInfo;
import com.rudaco.searchcrafter.staticInfo.Utils;
import com.rudaco.searchcrafter.staticInfo.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketS2CRange {

    public Vector3 range;
    public BlockPos pos;


    // Constructor sin argumentos necesario para Forge
    public PacketS2CRange() {}

    // Constructor para tu mensaje
    public PacketS2CRange(Vector3 range, BlockPos pos) {
        this.range = range;
        this.pos = pos;
    }

    public PacketS2CRange(FriendlyByteBuf buf) {
        range = new Vector3(buf.readInt(), buf.readInt(), buf.readInt());
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }


    public PacketS2CRange fromBytes(FriendlyByteBuf buf) {
        return new PacketS2CRange(buf);
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

    public static boolean handle(PacketS2CRange msg, Supplier<NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(()->{
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handleRangePacket(msg, supplier));
        });
        return true;
    }

}
