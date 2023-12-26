package com.rudaco.searchcrafter.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rudaco.searchcrafter.SearchCrafter;
import com.rudaco.searchcrafter.block.custom.RendererBlock;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.network.packet.PacketC2SInfoReq;
import com.rudaco.searchcrafter.screen.CraftableInfo;
import com.rudaco.searchcrafter.screen.SearchCrafterTableMenu;
import com.rudaco.searchcrafter.staticInfo.StaticInfo;
import com.rudaco.searchcrafter.staticInfo.Utils;
import com.rudaco.searchcrafter.staticInfo.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.loading.targets.FMLServerLaunchHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;


public class SearchCrafterTable extends BlockEntity implements MenuProvider{


    private final static int OUTPUT_SLOT = 0;
    protected final ContainerData data;

    public boolean renderActive;
    public Vector3 range;


    public SearchCrafterTable(BlockPos pPos, BlockState pBlockState) {

        super(ModBlockEntities.SEARCH_CRAFTING_TABLE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return 0;
            }

            @Override
            public void set(int pIndex, int pValue) {

            }

            @Override
            public int getCount() {
                return 0;
            }
        };
        this.renderActive = false;
        this.range = new Vector3(5,5,5);

        if(this.level != null && !this.level.isClientSide()) this.level.sendBlockUpdated(pPos, getBlockState(), getBlockState(), Block.UPDATE_ALL);

    }

    public void generateResultItem(Item item, int number) {
        if (!this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            if (!this.itemHandler.getStackInSlot(OUTPUT_SLOT).getItem().equals(item)) return;
            this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(item, this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + number));
            return;
        }
        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(item, number));
    }

    public boolean canGenerateResultItem(Item item) {
        if (!this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getItem().equals(item);
        }
        return true;
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();


    @Override
    public Component getDisplayName() {

        return Component.literal("Search Crafting Table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {

        return new SearchCrafterTableMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("Inventory", itemHandler.serializeNBT());
        super.saveAdditional(pTag);

        var modData = new CompoundTag();
        modData.putBoolean("Render", this.renderActive);
        modData.putInt("rangeX", range.x);
        modData.putInt("rangeY", range.y);
        modData.putInt("rangeZ", range.z);
        pTag.put(SearchCrafter.MOD_ID, modData);

    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("Inventory"));

        CompoundTag modData = pTag.getCompound(SearchCrafter.MOD_ID);
        this.renderActive = modData.getBoolean("Render");
        this.range = new Vector3(modData.getInt("rangeX"),modData.getInt("rangeY"),modData.getInt("rangeZ"));
        if(this.level != null && !this.level.isClientSide()) this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        else if (this.level != null) MySimpleChannel.sendToServer(new PacketC2SInfoReq(this.worldPosition));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag modData = super.getUpdateTag();
        saveAdditional(modData);
        return modData;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isEmpty() {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty();
    }

    public void setRange(Vector3 range) {
        this.range = range;
    }
    public void setVisible(boolean renderActive){
        this.renderActive = renderActive;
    }


}