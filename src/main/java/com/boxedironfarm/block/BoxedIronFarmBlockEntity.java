package com.boxedironfarm.block;

import com.boxedironfarm.ModBlockEntities;
import com.boxedironfarm.screen.BoxedIronFarmScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 完整盒装刷铁机的方块实体：
 * - 5 格容器（漏斗式 GUI）
 * - 每 30 秒（600 tick）自动向容器内产出 4 个铁锭，装不下则掉落
 */
public class BoxedIronFarmBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {

	private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);

	public BoxedIronFarmBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.BOXED_IRON_FARM, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, BoxedIronFarmBlockEntity blockEntity) {
		blockEntity.serverTick();
	}

	private void serverTick() {
		if (world == null || world.isClient) {
			return;
		}
		// 每 600 tick（30 秒）产一次铁
		if (world.getTime() % 600 != 0) {
			return;
		}
		ItemStack iron = new ItemStack(Items.IRON_INGOT, 4);
		for (int i = 0; i < inventory.size() && !iron.isEmpty(); i++) {
			ItemStack slot = inventory.get(i);
			if (slot.isEmpty()) {
				inventory.set(i, iron);
				iron = ItemStack.EMPTY;
			} else if (slot.isOf(Items.IRON_INGOT) && slot.getCount() < slot.getMaxCount()) {
				int canAdd = Math.min(slot.getMaxCount() - slot.getCount(), iron.getCount());
				slot.increment(canAdd);
				iron.decrement(canAdd);
			}
		}
		if (!iron.isEmpty()) {
			// 容器满了，把多余的铁锭掉落在方块旁
			ItemEntity drop = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, iron);
			world.spawnEntity(drop);
		}
		markDirty();
	}

	// ---------- Inventory ----------

	@Override
	public int size() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		if (slot < 0 || slot >= inventory.size()) {
			return ItemStack.EMPTY;
		}
		return inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack result = Inventories.splitStack(inventory, slot, amount);
		if (!result.isEmpty()) {
			markDirty();
		}
		return result;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack result = Inventories.removeStack(inventory, slot);
		markDirty();
		return result;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > stack.getMaxCount()) {
			stack.setCount(stack.getMaxCount());
		}
		markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return Inventory.canPlayerUse(this, player);
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	// ---------- NamedScreenHandlerFactory ----------

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new BoxedIronFarmScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("container.boxedironfarm.boxed_iron_farm");
	}

	// ---------- NBT ----------

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		Inventories.readNbt(nbt, inventory, registryLookup);
	}

	@Override
	public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		Inventories.writeNbt(nbt, inventory, registryLookup);
	}
}
