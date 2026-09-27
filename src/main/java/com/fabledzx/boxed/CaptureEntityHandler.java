package com.fabledzx.boxed;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CaptureEntityHandler {

    public static void register() {
        UseEntityCallback.EVENT.register(CaptureEntityHandler::onUseEntity);
    }

    private static ActionResult onUseEntity(
            PlayerEntity player, World world, Hand hand,
            Entity entity, @Nullable EntityHitResult hitResult) {

        if (world.isClient()) return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);

        // 空盒 + 僵尸 → 僵尸盒
        if (stack.isOf(BoxedMod.EMPTY_BOX.asItem())
            && entity instanceof ZombieEntity) {
            stack.decrement(1);
            ItemStack result = new ItemStack(BoxedMod.ZOMBIE_BOX);
            writeState(result, true, false);
            player.getInventory().offerOrDrop(result);
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        // 空盒 + 村民 → 村民盒
        if (stack.isOf(BoxedMod.EMPTY_BOX.asItem())
            && entity instanceof VillagerEntity) {
            stack.decrement(1);
            ItemStack result = new ItemStack(BoxedMod.VILLAGER_BOX);
            writeState(result, false, true);
            player.getInventory().offerOrDrop(result);
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        // 僵尸盒 + 村民 → 完整盒
        if (stack.isOf(BoxedMod.ZOMBIE_BOX.asItem())
            && entity instanceof VillagerEntity) {
            stack.decrement(1);
            ItemStack result = new ItemStack(BoxedMod.FULL_BOX);
            writeState(result, true, true);
            player.getInventory().offerOrDrop(result);
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        // 村民盒 + 僵尸 → 完整盒
        if (stack.isOf(BoxedMod.VILLAGER_BOX.asItem())
            && entity instanceof ZombieEntity) {
            stack.decrement(1);
            ItemStack result = new ItemStack(BoxedMod.FULL_BOX);
            writeState(result, true, true);
            player.getInventory().offerOrDrop(result);
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    /**
     * 把“盒子里装了谁”写入物品 NBT（放置时 getPlacementState 会读取它来设置方块状态），
     * 否则捕捉出来的盒子放置后依然是空盒状态、永远不会产铁。
     */
    private static void writeState(ItemStack stack, boolean zombie, boolean villager) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("HasZombie", zombie);
        nbt.putBoolean("HasVillager", villager);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static void playSound(PlayerEntity player) {
        player.getWorld().playSound(null, player.getBlockPos(),
            SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.PLAYERS,
            1.0f, 1.0f);
    }
}