package org.watp.umc.feast.recipe;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.watp.umc.feast.Feast;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OvenRecipe {
	private static Map<Item, List<Item>> recipes=new Object2ObjectLinkedOpenHashMap<>();

	private static final List<Item> RECIPE_APPLE_PIE= Arrays.asList(
			Items.SUGAR, Items.EGG, Items.SUGAR,
			Items.SUGAR, Items.APPLE, Items.SUGAR,
			Feast.Items.WHEATMEAL, Feast.Items.BUTTER, Feast.Items.WHEATMEAL
	);

	private static final List<Item> RECIPE_POTATO_BEEF_PIE= Arrays.asList(
			Feast.Items.WHEATMEAL, Feast.Items.SALT, Feast.Items.WHEATMEAL,
			Items.BAKED_POTATO, Items.COOKED_BEEF, Items.BAKED_POTATO,
			Feast.Items.WHEATMEAL, Feast.Items.COOKED_EGG, Feast.Items.WHEATMEAL
	);

	private static final List<Item> RECIPE_PIZZA_PART= Arrays.asList(
			Items.RED_MUSHROOM, Feast.Items.SALT, Items.BROWN_MUSHROOM,
			Items.CARROT, Items.COOKED_BEEF, Items.KELP,
			Feast.Items.WHEATMEAL, Feast.Items.CHEESE, Feast.Items.WHEATMEAL
	);

	private static final List<Item> RECIPE_COD_PASTRY= Arrays.asList(
			Feast.Items.WHEATMEAL, Feast.Items.SALT, Feast.Items.WHEATMEAL,
			Feast.Items.WHEATMEAL, Items.COOKED_COD, Feast.Items.WHEATMEAL,
			Feast.Items.WHEATMEAL, Feast.Items.COOKED_EGG, Feast.Items.WHEATMEAL);

	private static final List<Item> RECIPE_SALMON_ROLL= Arrays.asList(
			Feast.Items.WHEATMEAL,Feast.Items.SALT,Feast.Items.WHEATMEAL,
			Items.AIR,Items.COOKED_SALMON,Items.AIR,							//cabbage,cucumber
			Feast.Items.WHEATMEAL, Feast.Items.COOKED_EGG,Feast.Items.WHEATMEAL
	);

	static {
		recipes.put(Feast.Items.APPLE_PIE, RECIPE_APPLE_PIE);
		recipes.put(Feast.Items.POTATO_BEEF_PIE, RECIPE_POTATO_BEEF_PIE);
		recipes.put(Feast.Items.PIZZA_PART, RECIPE_PIZZA_PART);
		recipes.put(Feast.Items.COD_BAR, RECIPE_COD_PASTRY);
		recipes.put(Feast.Items.SALMON_ROLL, RECIPE_SALMON_ROLL);
	}

	public static Item match(@Nonnull() List<Item> inputs) {
		for (Map.Entry<Item, List<Item>> entry : recipes.entrySet()) {
			if (inputs.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

}
