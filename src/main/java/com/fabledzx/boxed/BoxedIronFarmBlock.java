package com.fabledzx.boxed;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BoxedIronFarmBlock extends BlockWithEntity {
    public static final MapCodec<BoxedIronFarmBlock> CODEC =
        createCodec(BoxedIronFarmBlock::new);
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_ZOMBIE = BooleanProperty.of("has_zombie");
    public static final BooleanProperty HAS_VILLAGER = BooleanProperty.of("has_villager");

    public BoxedIronFarmBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(HAS_ZOMBIE, false)
            .with(HAS_VILLAGER, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HAS_ZOMBIE, HAS_VILLAGER);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = getDefaultState()
            .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());

        ItemStack stack = ctx.getStack();
        NbtComponent custom = stack.getOrDefault(
            DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = custom.copyNbt();
        if (nbt.getBoolean("HasZombie", false)) state = state.with(HAS_ZOMBIE, true);
        if (nbt.getBoolean("HasVillager", false)) state = state.with(HAS_VILLAGER, true);
        return state;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoxedIronFarmBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;
        return (w, pos, st, be) -> {
            if (be instanceof BoxedIronFarmBlockEntity farm) {
                farm.tick(w, pos, st);
            }
        };
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world,
                                  BlockPos pos, PlayerEntity player,
                                  BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BoxedIronFarmBlockEntity farm) {
                player.openHandledScreen(farm);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void afterBreak(World world, PlayerEntity player, BlockPos pos,
                              BlockState state, @Nullable BlockEntity blockEntity,
                              ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (world.isClient()) return;

        if (blockEntity instanceof BoxedIronFarmBlockEntity farm) {
            for (int i = 0; i < farm.size(); i++) {
                ItemStack s = farm.getStack(i);
                if (!s.isEmpty()) {
                    Block.dropStack(world, pos, s.copy());
                    farm.setStack(i, ItemStack.EMPTY);
                }
            }
            farm.markDirty();
        }

        boolean silk = hasSilkTouch(tool);
        boolean z = state.get(HAS_ZOMBIE);
        boolean v = state.get(HAS_VILLAGER);

        ItemStack drop;
        if (silk && z && v) {
            drop = new ItemStack(BoxedMod.FULL_BOX);
            writeStateTag(drop, true, true);
        } else if (silk && z) {
            drop = new ItemStack(BoxedMod.ZOMBIE_BOX);
            writeStateTag(drop, true, false);
        } else if (silk && v) {
            drop = new ItemStack(BoxedMod.VILLAGER_BOX);
            writeStateTag(drop, false, true);
        } else {
            drop = new ItemStack(BoxedMod.EMPTY_BOX);
        }

        Block.dropStack(world, pos, drop);
    }

    private static void writeStateTag(ItemStack stack, boolean z, boolean v) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("HasZombie", z);
        nbt.putBoolean("HasVillager", v);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static boolean hasSilkTouch(ItemStack tool) {
        if (tool.isEmpty()) return false;
        return tool.getEnchantments().getEnchantments().stream()
                .anyMatch(e -> e.matchesKey(Enchantments.SILK_TOUCH));
    }

    @Override
    protected void onStateReplaced(BlockState state, World world,
                                    BlockPos pos, BlockState newState,
                                    boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BoxedIronFarmBlockEntity farm && !farm.isEmpty()) {
                ItemScatterer.spawn(world, pos, farm);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}