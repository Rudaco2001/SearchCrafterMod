package com.rudaco.searchcrafter.block.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.staticInfo.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.EmptyModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
@OnlyIn(Dist.CLIENT)
public class RendererBlock implements BlockEntityRenderer<SearchCrafterTable> {

    BlockEntityRendererProvider.Context context;


    public RendererBlock(BlockEntityRendererProvider.Context context){
        this.context = context;
    }

    @Override
    public boolean shouldRender(SearchCrafterTable pBlockEntity, Vec3 pCameraPos) {
        return true;
    }


    @Override
    public void render(SearchCrafterTable pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if(!pBlockEntity.renderActive) return;
        int rangeX = pBlockEntity.range.x;
        int rangeY = pBlockEntity.range.y;
        int rangeZ = pBlockEntity.range.z;
        Utils.drawLineBox(pPoseStack, new AABB(rangeX,rangeY,rangeZ,-rangeX,-rangeY,-rangeZ));
    }



}
