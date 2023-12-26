package com.rudaco.searchcrafter.network.packet;

import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.staticInfo.StaticInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPacketHandlerClass {
    public static void handleRangePacket(PacketS2CRange msg, Supplier<NetworkEvent.Context> supplier){
        Level level = Minecraft.getInstance().level;
        BlockState blockState = level.getBlockState(msg.pos);
        Block block = blockState.getBlock();
        if (block instanceof SearchCrafterTableBlock) {
            BlockEntity ent = level.getBlockEntity(msg.pos);
            if(ent instanceof SearchCrafterTable table){
                table.range = msg.range;
                if(StaticInfo.controller != null) StaticInfo.controller.getDimensionValues();
            }
        }
    }

    public static void handleVisiblePacket(PacketS2CVisible msg, Supplier<NetworkEvent.Context> supplier) {
        Level level = Minecraft.getInstance().level;
        BlockState blockState = level.getBlockState(msg.pos);
        Block block = blockState.getBlock();
        if (block instanceof SearchCrafterTableBlock) {
            BlockEntity blockEntity = level.getBlockEntity(msg.pos);
            if (blockEntity instanceof SearchCrafterTable table) {
                table.renderActive = msg.visible;
                if(StaticInfo.controller != null) StaticInfo.controller.getRenderValue();
            }
        }
    }
}
