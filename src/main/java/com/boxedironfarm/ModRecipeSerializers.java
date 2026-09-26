package com.boxedironfarm;

import com.boxedironfarm.recipe.BoxedIronFarmRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;

public final class ModRecipeSerializers {
	private ModRecipeSerializers() {
	}

	public static void register() {
		RecipeSerializer.register(BoxedIronFarmMod.MOD_ID + ":boxed_iron_farm", BoxedIronFarmRecipeSerializer.INSTANCE);
	}
}
