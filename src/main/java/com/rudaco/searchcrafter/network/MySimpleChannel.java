package com.rudaco.searchcrafter.network;

import com.rudaco.searchcrafter.SearchCrafter;
import com.rudaco.searchcrafter.network.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkDirection;

public class MySimpleChannel {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return MySimpleChannel.packetId++;
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(()->player), message);
    }

    public static void register(){

        SimpleChannel net = NetworkRegistry.ChannelBuilder
                        .named(new ResourceLocation(SearchCrafter.MOD_ID, "messages"))
                        .networkProtocolVersion(()-> PROTOCOL_VERSION)
                        .clientAcceptedVersions(s->true)
                        .serverAcceptedVersions(s->true)
                        .simpleChannel();

        INSTANCE = net;

        INSTANCE.messageBuilder(PacketS2C.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketS2C::new)
                .encoder(PacketS2C::toBytes)
                .consumerMainThread(PacketS2C::handle)
                .add();
        INSTANCE.messageBuilder(PacketS2CRange.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketS2CRange::new)
                .encoder(PacketS2CRange::toBytes)
                .consumerMainThread(PacketS2CRange::handle)
                .add();
        INSTANCE.messageBuilder(PacketS2CVisible.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketS2CVisible::new)
                .encoder(PacketS2CVisible::toBytes)
                .consumerMainThread(PacketS2CVisible::handle)
                .add();
        INSTANCE.messageBuilder(PacketC2S.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketC2S::new)
                .encoder(PacketC2S::toBytes)
                .consumerMainThread(PacketC2S::handle)
                .add();
        INSTANCE.messageBuilder(PacketC2S2.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketC2S2::new)
                .encoder(PacketC2S2::toBytes)
                .consumerMainThread(PacketC2S2::handle)
                .add();
        INSTANCE.messageBuilder(PacketC2SRange.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketC2SRange::new)
                .encoder(PacketC2SRange::toBytes)
                .consumerMainThread(PacketC2SRange::handle)
                .add();
        INSTANCE.messageBuilder(PacketC2SVisible.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketC2SVisible::new)
                .encoder(PacketC2SVisible::toBytes)
                .consumerMainThread(PacketC2SVisible::handle)
                .add();
        INSTANCE.messageBuilder(PacketC2SInfoReq.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketC2SInfoReq::new)
                .encoder(PacketC2SInfoReq::toBytes)
                .consumerMainThread(PacketC2SInfoReq::handle)
                .add();
    }


}
