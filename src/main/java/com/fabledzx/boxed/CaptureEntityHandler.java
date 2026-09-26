
package com.fabledzx.boxed;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
            player.getInventory().offerOrDrop(new ItemStack(BoxedMod.ZOMBIE_BOX));
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        // 空盒 + 村民 → 村民盒
        if (stack.isOf(BoxedMod.EMPTY_BOX.asItem())
            && entity instanceof VillagerEntity) {
            stack.decrement(1);
            player.getInventory().offerOrDrop(new ItemStack(BoxedMod.VILLAGER_BOX));
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        // 僵尸盒 + 村民 → 完整盒
        if (stack.isOf(BoxedMod.ZOMBIE_BOX.asItem())
            && entity instanceof VillagerEntity) {
            stack.decrement(1);
            player.getInventory().offerOrDrop(new ItemStack(BoxedMod.FULL_BOX));
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        // 村民盒 + 僵尸 → 完整盒
        if (stack.isOf(BoxedMod.VILLAGER_BOX.asItem())
            && entity instanceof ZombieEntity) {
            stack.decrement(1);
            player.getInventory().offerOrDrop(new ItemStack(BoxedMod.FULL_BOX));
            playSound(player);
            entity.discard();
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static void playSound(PlayerEntity player) {
        player.getWorld().playSound(null, player.getBlockPos(),
            SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.PLAYERS,
            1.0f, 1.0f);
    }
}
