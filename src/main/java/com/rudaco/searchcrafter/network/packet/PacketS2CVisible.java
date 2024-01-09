package com.rudaco.searchcrafter.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketS2CVisible {

    public boolean visible;
    public BlockPos pos;


    // Constructor sin argumentos necesario para Forge
    public PacketS2CVisible() {}

    // Constructor para tu mensaje
    public PacketS2CVisible(Boolean visible, BlockPos pos) {
        this.visible = visible;
        this.pos = pos;
    }

    public PacketS2CVisible(FriendlyByteBuf buf) {
        visible = buf.readBoolean();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }


    public PacketS2CVisible fromBytes(FriendlyByteBuf buf) {
        return new PacketS2CVisible(buf);
    }




    public void toBytes(FriendlyByteBuf buf) {
        // Escribe los datos en el paquete de red
        buf.writeBoolean(visible);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public static boolean handle(PacketS2CVisible msg, Supplier<NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(()->{
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handleVisiblePacket(msg, supplier));
        });
        return true;
    }

}
