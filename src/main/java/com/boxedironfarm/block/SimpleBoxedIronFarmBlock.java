package com.boxedironfarm.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockRenderType;

/**
 * 普通盒装刷铁机方块（空的 / 有僵尸的 / 有村民的）。
 * 这些变体本身不产铁、没有 GUI，只有“完整盒装刷铁机”才有方块实体。
 */
public class SimpleBoxedIronFarmBlock extends Block {
	public SimpleBoxedIronFarmBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends SimpleBoxedIronFarmBlock> getCodec() {
		return createCodec(SimpleBoxedIronFarmBlock::new);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
}
