package com.rudaco.searchcrafter.block.custom;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rudaco.searchcrafter.block.entity.SearchCrafterTable;
import com.rudaco.searchcrafter.staticInfo.Utils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
    public boolean shouldRenderOffScreen(SearchCrafterTable pBlockEntity) {
        return true;
    }



    @Override
    public void render(SearchCrafterTable pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if(!pBlockEntity.renderActive) return;
        pPoseStack.pushPose();
        int rangeX = pBlockEntity.range.x;
        int rangeY = pBlockEntity.range.y;
        int rangeZ = pBlockEntity.range.z;
        Utils.drawLineBox(pPoseStack, new AABB(rangeX+1,rangeY+1,rangeZ+1,-rangeX,-rangeY,-rangeZ));
        pPoseStack.popPose();
    }




}
