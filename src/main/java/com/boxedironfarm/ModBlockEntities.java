package com.boxedironfarm;

import com.boxedironfarm.block.BoxedIronFarmBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {
	private ModBlockEntities() {
	}

	public static final BlockEntityType<BoxedIronFarmBlockEntity> BOXED_IRON_FARM = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			Identifier.of(BoxedIronFarmMod.MOD_ID, "boxed_iron_farm"),
			FabricBlockEntityTypeBuilder.create(BoxedIronFarmBlockEntity::new, ModBlocks.BOXED_IRON_FARM).build());

	public static void register() {
		// 静态初始化即完成注册
	}
}
