package com.fabledzx.boxed;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SleepRestrictionHandler {

    private static final int HORIZONTAL_RADIUS = 8;
    private static final int VERTICAL_RADIUS = 5;

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (!(state.getBlock() instanceof BedBlock)) return ActionResult.PASS;

            if (hasBoxedIronFarmNearby(player)) {
                player.sendMessage(
                    Text.translatable("block.boxed.cannot_sleep"), true);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }

    private static boolean hasBoxedIronFarmNearby(PlayerEntity player) {
        World world = player.getWorld();
        BlockPos center = player.getBlockPos();
        for (int dx = -HORIZONTAL_RADIUS; dx <= HORIZONTAL_RADIUS; dx++) {
            for (int dy = -VERTICAL_RADIUS; dy <= VERTICAL_RADIUS; dy++) {
                for (int dz = -HORIZONTAL_RADIUS; dz <= HORIZONTAL_RADIUS; dz++) {
                    BlockState s = world.getBlockState(center.add(dx, dy, dz));
                    if (s.getBlock() instanceof BoxedIronFarmBlock) return true;
                }
            }
        }
        return false;
    }
}