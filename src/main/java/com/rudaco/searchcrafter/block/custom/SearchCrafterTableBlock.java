package com.rudaco.searchcrafter.block.custom;


import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.network.packet.PacketS2C;
import com.rudaco.searchcrafter.staticInfo.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SearchCrafterTableBlock extends BaseEntityBlock {


    public BlockPos pos;


    public SearchCrafterTableBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        this.pos = pPos;
        return new SearchCrafterTable(pPos,pState);
    }



    @Override
    public @NotNull RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof SearchCrafterTable table) {
                table.drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof SearchCrafterTable table) {
                MySimpleChannel.sendToPlayer(new PacketS2C(Utils.getAllChestItems(pLevel,pPos, table.range)),(ServerPlayer) pPlayer);
                NetworkHooks.openScreen(((ServerPlayer)pPlayer), (SearchCrafterTable)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }





}
