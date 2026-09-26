
package com.fabledzx.boxed;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BoxedIronFarmBlockEntity extends BlockEntity
        implements Inventory, NamedScreenHandlerFactory {

    private static final int INVENTORY_SIZE = 5;
    private static final int PRODUCTION_INTERVAL = 600; // 30 秒
    private static final int IRON_PER_PRODUCTION = 4;

    private final DefaultedList<ItemStack> items =
        DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int tickCounter = 0;
    private boolean hasZombie = false;
    private boolean hasVillager = false;

    public BoxedIronFarmBlockEntity(BlockPos pos, BlockState state) {
        super(BoxedMod.BOX_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) return;

        if (hasZombie != state.get(BoxedIronFarmBlock.HAS_ZOMBIE)
            || hasVillager != state.get(BoxedIronFarmBlock.HAS_VILLAGER)) {
            world.setBlockState(pos, state
                .with(BoxedIronFarmBlock.HAS_ZOMBIE, hasZombie)
                .with(BoxedIronFarmBlock.HAS_VILLAGER, hasVillager));
        }

        if (!hasZombie || !hasVillager) return;
        if (!hasSpaceForIron()) return;

        tickCounter++;
        if (tickCounter >= PRODUCTION_INTERVAL) {
            tickCounter = 0;
            produceIron();
            markDirty();
            world.updateListeners(pos, state, state, 3);
        }
    }

    private boolean hasSpaceForIron() {
        for (ItemStack stack : items) {
            if (stack.isEmpty()) return true;
            if (stack.isOf(Items.IRON_INGOT)
                && stack.getCount() + IRON_PER_PRODUCTION <= stack.getMaxCount()) {
                return true;
            }
        }
        return false;
    }

    private void produceIron() {
        int remaining = IRON_PER_PRODUCTION;
        for (int i = 0; i < items.size() && remaining > 0; i++) {
            ItemStack s = items.get(i);
            if (s.isOf(Items.IRON_INGOT)) {
                int space = s.getMaxCount() - s.getCount();
                int add = Math.min(space, remaining);
                s.increment(add);
                remaining -= add;
            }
        }
        for (int i = 0; i < items.size() && remaining > 0; i++) {
            if (items.get(i).isEmpty()) {
                int add = Math.min(64, remaining);
                items.set(i, new ItemStack(Items.IRON_INGOT, add));
                remaining -= add;
            }
        }
    }

    public boolean captureEntity(Entity entity) {
        if (entity instanceof ZombieEntity && !hasZombie) {
            hasZombie = true;
            entity.discard();
            markDirty();
            sync();
            return true;
        }
        if (entity instanceof VillagerEntity && !hasVillager) {
            hasVillager = true;
            entity.discard();
            markDirty();
            sync();
            return true;
        }
        return false;
    }

    public boolean hasZombie() { return hasZombie; }
    public boolean hasVillager() { return hasVillager; }

    @Override public int size() { return INVENTORY_SIZE; }
    @Override public boolean isEmpty() {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        return true;
    }
    @Override public ItemStack getStack(int slot) { return items.get(slot); }
    @Override public ItemStack removeStack(int slot, int amount) {
        ItemStack r = Inventories.splitStack(items, slot, amount);
        if (!r.isEmpty()) markDirty();
        return r;
    }
    @Override public ItemStack removeStack(int slot) {
        return Inventories.removeStack(items, slot);
    }
    @Override public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack())
            stack.setCount(getMaxCountPerStack());
        markDirty();
    }
    @Override public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }
    @Override public void clear() { items.clear(); }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.boxed.boxed_iron_farm");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new BoxedIronFarmScreenHandler(syncId, inv, this);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, items, lookup);
        nbt.putBoolean("HasZombie", hasZombie);
        nbt.putBoolean("HasVillager", hasVillager);
        nbt.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        Inventories.readNbt(nbt, items, lookup);
        hasZombie = nbt.getBoolean("HasZombie");
        hasVillager = nbt.getBoolean("HasVillager");
        tickCounter = nbt.getInt("TickCounter");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup lookup) {
        return createNbt(lookup);
    }

    private void sync() {
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
