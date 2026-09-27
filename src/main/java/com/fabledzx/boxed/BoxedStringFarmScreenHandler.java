package com.fabledzx.boxed;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BoxedStringFarmScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public BoxedStringFarmScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(5));
    }

    public BoxedStringFarmScreenHandler(int syncId, PlayerInventory playerInventory,
                                         Inventory inventory) {
        super(BoxedMod.STRING_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        checkSize(inventory, 5);
        inventory.onOpen(playerInventory.player);

        for (int i = 0; i < 5; i++) {
            this.addSlot(new Slot(inventory, i, 44 + i * 18, 20));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                    col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 109));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            original = stack.copy();
            if (slotIndex < 5) {
                if (!this.insertItem(stack, 5, 41, true)) return ItemStack.EMPTY;
            } else {
                if (!this.insertItem(stack, 0, 5, false)) return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
            if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
            slot.onTakeItem(player, stack);
        }
        return original;
    }

    @Override public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
    @Override public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}