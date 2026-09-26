package com.boxedironfarm.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * 盒装刷铁机的合成配方。
 * 外观上是一张“有序合成”配方（玻璃 + 原石 + 漏斗 + 岩浆桶），
 * 但会在合成时把岩浆桶作为“余料”返还一个空桶给玩家。
 */
public class BoxedIronFarmRecipe implements CraftingRecipe {

	private final ShapedRecipe shaped;

	public BoxedIronFarmRecipe(ShapedRecipe shaped) {
		this.shaped = shaped;
	}

	public ShapedRecipe getShaped() {
		return shaped;
	}

	@Override
	public boolean matches(CraftingRecipeInput input, World world) {
		return shaped.matches(input, world);
	}

	@Override
	public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
		return shaped.craft(input, lookup);
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return BoxedIronFarmRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<CraftingRecipe> getType() {
		return RecipeType.CRAFTING;
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return shaped.getCategory();
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		return shaped.getIngredientPlacement();
	}

	@Override
	public RecipeBookCategory getRecipeBookCategory() {
		return shaped.getRecipeBookCategory();
	}

	@Override
	public String getGroup() {
		return shaped.getGroup();
	}

	@Override
	public List<RecipeDisplay> getDisplays() {
		return shaped.getDisplays();
	}

	// 1.21.5 中 getIngredients 不再属于 CraftingRecipe 接口，保留用于兼容
	public List<Optional<Ingredient>> getIngredients() {
		return shaped.getIngredients();
	}

	@Override
	public boolean showNotification() {
		return shaped.showNotification();
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return shaped.isIgnoredInRecipeBook();
	}

	/**
	 * 合成时返还空桶：把输入格中的岩浆桶替换为空桶。
	 */
	@Override
	public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
		DefaultedList<ItemStack> remainders = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
		for (int i = 0; i < input.size(); i++) {
			if (input.getStackInSlot(i).isOf(Items.LAVA_BUCKET)) {
				remainders.set(i, new ItemStack(Items.BUCKET));
			}
		}
		return remainders;
	}
}
