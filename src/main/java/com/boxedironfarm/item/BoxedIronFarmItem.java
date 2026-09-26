package com.boxedironfarm.item;

import com.boxedironfarm.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * 盒装刷铁机物品：对着指定生物右键使用，把僵尸/村民“装”进盒子里，
 * 并替换为对应的盒装刷铁机物品。
 */
public class BoxedIronFarmItem extends BlockItem {
	private final CaptureType captureType;

	public BoxedIronFarmItem(Block block, Settings settings, CaptureType captureType) {
		super(block, settings);
		this.captureType = captureType;
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		World world = user.getWorld();

		boolean canCapture = switch (captureType) {
			case EMPTY -> entity instanceof ZombieEntity || entity instanceof VillagerEntity;
			case ZOMBIE -> entity instanceof VillagerEntity;
			case VILLAGER -> entity instanceof ZombieEntity;
		};

		if (!canCapture) {
			return ActionResult.PASS;
		}

		// 客户端同样返回成功，以触发手臂挥动动画；实际捕捉只在服务端执行
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}

		ItemStack result;
		if (captureType == CaptureType.EMPTY) {
			// 空的：装进村民 -> 有村民的盒装刷铁机；装进僵尸 -> 有僵尸的盒装刷铁机
			result = new ItemStack(entity instanceof VillagerEntity
					? ModBlocks.VILLAGER_BOXED_IRON_FARM.asItem()
					: ModBlocks.ZOMBIE_BOXED_IRON_FARM.asItem());
		} else {
			// 有僵尸的 + 村民 = 完整的盒装刷铁机；有村民的 + 僵尸 = 完整的盒装刷铁机
			result = new ItemStack(ModBlocks.BOXED_IRON_FARM.asItem());
		}

		// 把生物装进盒子里（移除实体）
		entity.discard();

		if (stack.getCount() == 1) {
			user.setStackInHand(hand, result);
		} else {
			stack.decrement(1);
			user.getInventory().offerOrDrop(result);
		}
		return ActionResult.SUCCESS;
	}
}
