package com.boxedironfarm;

import com.boxedironfarm.block.BoxedIronFarmBlock;
import com.boxedironfarm.block.SimpleBoxedIronFarmBlock;
import com.boxedironfarm.item.BoxedIronFarmItem;
import com.boxedironfarm.item.CaptureType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class ModBlocks {
	private ModBlocks() {
	}

	public static final Block EMPTY_BOXED_IRON_FARM = register(
			"empty_boxed_iron_farm",
			SimpleBoxedIronFarmBlock::new,
			CaptureType.EMPTY);

	public static final Block ZOMBIE_BOXED_IRON_FARM = register(
			"zombie_boxed_iron_farm",
			SimpleBoxedIronFarmBlock::new,
			CaptureType.ZOMBIE);

	public static final Block VILLAGER_BOXED_IRON_FARM = register(
			"villager_boxed_iron_farm",
			SimpleBoxedIronFarmBlock::new,
			CaptureType.VILLAGER);

	public static final Block BOXED_IRON_FARM = register(
			"boxed_iron_farm",
			BoxedIronFarmBlock::new,
			null);

	private static AbstractBlock.Settings baseSettings() {
		return AbstractBlock.Settings.create()
				.mapColor(net.minecraft.block.MapColor.STONE_GRAY)
				.strength(1.5F, 3.0F)
				.sounds(BlockSoundGroup.GLASS)
				.nonOpaque();
	}

	private static Block register(String id, Function<AbstractBlock.Settings, Block> factory, CaptureType captureType) {
		// 1.21.5 起方块/物品构造时需要拿到注册键（用于战利品表键等）
		Identifier identifier = Identifier.of(BoxedIronFarmMod.MOD_ID, id);
		RegistryKey<Block> key = RegistryKey.of(Registries.BLOCK.getKey(), identifier);
		Block block = factory.apply(baseSettings().registryKey(key));
		Registry.register(Registries.BLOCK, key, block);

		RegistryKey<Item> itemKey = RegistryKey.of(Registries.ITEM.getKey(), identifier);
		Item item;
		if (captureType != null) {
			item = new BoxedIronFarmItem(block, new Item.Settings().registryKey(itemKey), captureType);
		} else {
			item = new BlockItem(block, new Item.Settings().registryKey(itemKey));
		}
		Registry.register(Registries.ITEM, itemKey, item);
		return block;
	}

	public static void register() {
		Registry.register(Registries.ITEM_GROUP, Identifier.of(BoxedIronFarmMod.MOD_ID, "boxed_iron_farm"),
				ItemGroup.create(ItemGroup.Row.TOP, 4)
						.displayName(Text.translatable("itemGroup.boxedironfarm"))
						.icon(() -> new ItemStack(BOXED_IRON_FARM))
						.entries((displayContext, entries) -> {
							entries.add(new ItemStack(EMPTY_BOXED_IRON_FARM));
							entries.add(new ItemStack(ZOMBIE_BOXED_IRON_FARM));
							entries.add(new ItemStack(VILLAGER_BOXED_IRON_FARM));
							entries.add(new ItemStack(BOXED_IRON_FARM));
						})
						.build());
	}
}
