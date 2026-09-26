package com.boxedironfarm.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;

/**
 * 盒装刷铁机配方的序列化器：完全复用原版“有序合成”配方的编解码，
 * 只是把读出来的 ShapedRecipe 包装成 BoxedIronFarmRecipe。
 */
public class BoxedIronFarmRecipeSerializer implements RecipeSerializer<BoxedIronFarmRecipe> {

	public static final BoxedIronFarmRecipeSerializer INSTANCE = new BoxedIronFarmRecipeSerializer();

	private final MapCodec<BoxedIronFarmRecipe> codec = ShapedRecipe.Serializer.CODEC
			.xmap(BoxedIronFarmRecipe::new, BoxedIronFarmRecipe::getShaped);

	private final PacketCodec<RegistryByteBuf, BoxedIronFarmRecipe> packetCodec = ShapedRecipe.Serializer.PACKET_CODEC
			.xmap(BoxedIronFarmRecipe::new, BoxedIronFarmRecipe::getShaped);

	@Override
	public MapCodec<BoxedIronFarmRecipe> codec() {
		return codec;
	}

	@Override
	public PacketCodec<RegistryByteBuf, BoxedIronFarmRecipe> packetCodec() {
		return packetCodec;
	}
}
