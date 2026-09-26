
package com.fabledzx.boxed;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BoxedMod implements ModInitializer {
    public static final String MOD_ID = "boxed";

    private static AbstractBlock.Settings boxSettings(String name) {
        RegistryKey<Block> key = RegistryKey.of(
            RegistryKeys.BLOCK, Identifier.of(MOD_ID, name));
        return AbstractBlock.Settings.create()
            .registryKey(key)
            .strength(2.0f)
            .sounds(BlockSoundGroup.GLASS)
            .nonOpaque()
            .luminance(state -> 3);
    }

    // ===== 盒装刷铁机：4 个状态方块 =====
    public static final Block EMPTY_BOX = registerBlock("empty_box",
        new BoxedIronFarmBlock(boxSettings("empty_box")));
    public static final Block ZOMBIE_BOX = registerBlock("zombie_box",
        new BoxedIronFarmBlock(boxSettings("zombie_box")));
    public static final Block VILLAGER_BOX = registerBlock("villager_box",
        new BoxedIronFarmBlock(boxSettings("villager_box")));
    public static final Block FULL_BOX = registerBlock("full_box",
        new BoxedIronFarmBlock(boxSettings("full_box")));

    // ===== 盒装刷线机 =====
    public static final Block BOXED_STRING_FARM = registerBlock("boxed_string_farm",
        new BoxedStringFarmBlock(boxSettings("boxed_string_farm")));

    // ===== 方块实体类型 =====
    public static final BlockEntityType<BoxedIronFarmBlockEntity> BOX_ENTITY =
        Registry.register(Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "boxed_iron_farm"),
            FabricBlockEntityTypeBuilder.create(
                BoxedIronFarmBlockEntity::new,
                EMPTY_BOX, ZOMBIE_BOX, VILLAGER_BOX, FULL_BOX
            ).build());

    public static final BlockEntityType<BoxedStringFarmBlockEntity> STRING_FARM_ENTITY =
        Registry.register(Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "boxed_string_farm"),
            FabricBlockEntityTypeBuilder.create(
                BoxedStringFarmBlockEntity::new,
                BOXED_STRING_FARM
            ).build());

    // ===== ScreenHandler =====
    public static final ScreenHandlerType<BoxedIronFarmScreenHandler> BOX_SCREEN_HANDLER =
        Registry.register(Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "boxed_iron_farm"),
            new ScreenHandlerType<>(BoxedIronFarmScreenHandler::new,
                FeatureFlags.VANILLA_FEATURES));

    public static final ScreenHandlerType<BoxedStringFarmScreenHandler> STRING_SCREEN_HANDLER =
        Registry.register(Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "boxed_string_farm"),
            new ScreenHandlerType<>(BoxedStringFarmScreenHandler::new,
                FeatureFlags.VANILLA_FEATURES));

    @Override
    public void onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(EMPTY_BOX.asItem());
            entries.add(ZOMBIE_BOX.asItem());
            entries.add(VILLAGER_BOX.asItem());
            entries.add(FULL_BOX.asItem());
            entries.add(BOXED_STRING_FARM.asItem());
        });

        CaptureEntityHandler.register();
        SleepRestrictionHandler.register();
    }

    private static Block registerBlock(String name, Block block) {
        RegistryKey<Item> itemKey = RegistryKey.of(
            RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
        BlockItem item = new BlockItem(block, new Item.Settings().registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return Registry.register(Registries.BLOCK,
            Identifier.of(MOD_ID, name), block);
    }
}
