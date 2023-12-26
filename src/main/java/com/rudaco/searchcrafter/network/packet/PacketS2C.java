package com.rudaco.searchcrafter.network.packet;

import com.rudaco.searchcrafter.screen.CraftableInfo;
import com.rudaco.searchcrafter.staticInfo.StaticInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PacketS2C {
    private ArrayList<CraftableInfo> data;

    // Constructor sin argumentos necesario para Forge
    public PacketS2C() {}

    // Constructor para tu mensaje
    public PacketS2C(ArrayList<CraftableInfo> tuDato) {
        this.data = tuDato;
    }

    public PacketS2C(FriendlyByteBuf buf) {
        ArrayList<CraftableInfo> craftableList = new ArrayList<>();
        int listSize = buf.readInt();

        for (int i = 0; i < listSize; i++) {
            craftableList.add(CraftableInfo.fromBytes(buf));
        }

        this.data = craftableList;
    }


    public PacketS2C fromBytes(FriendlyByteBuf buf) {
        return new PacketS2C(buf);
    }




    public void toBytes(FriendlyByteBuf buf) {
        // Escribe los datos en el paquete de red
        buf.writeInt(data.size());
        for (CraftableInfo craftableInfo : data) {
            craftableInfo.toBytes(buf);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(()->{
            StaticInfo.chestItems = new ArrayList<>(data);
            if(StaticInfo.controller == null) return;
            StaticInfo.controller.getAllChestItems();
            StaticInfo.controller.refreshMenu();
        });
        return true;
    }

}
