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

public class PacketC2SVisible {

    private boolean visible;
    private BlockPos pos;


    // Constructor sin argumentos necesario para Forge
    public PacketC2SVisible() {}

    // Constructor para tu mensaje
    public PacketC2SVisible(Boolean visible, BlockPos pos) {
        this.visible = visible;
        this.pos = pos;
    }

    public PacketC2SVisible(FriendlyByteBuf buf) {
        visible = buf.readBoolean();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }


    public PacketC2SVisible fromBytes(FriendlyByteBuf buf) {
        return new PacketC2SVisible(buf);
    }




    public void toBytes(FriendlyByteBuf buf) {
        // Escribe los datos en el paquete de red
        buf.writeBoolean(visible);
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
                    table.setVisible(visible);
                    for (ServerPlayer player1 : player.getServer().getPlayerList().getPlayers()) {
                        MySimpleChannel.sendToPlayer(new PacketS2CVisible(visible, pos), player1);
                    }
                    MySimpleChannel.sendToPlayer(new PacketS2CVisible(visible, pos), player);
                }
            }
        });
        return true;
    }

}
