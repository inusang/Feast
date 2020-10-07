package org.watp.umc.feast.recipe;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.watp.umc.feast.Feast;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class OvenRecipe {
	
	private static final Item[] RECIPE_APPLE_PIE=new Item[]
			{Items.SUGAR,Items.EGG,Items.SUGAR,
			 Items.SUGAR,Items.APPLE,Items.SUGAR,
			 Feast.Items.WHEATMEAL,Feast.Items.BUTTER,Feast.Items.WHEATMEAL};
	
	private static final Item[] RECIPE_POTATO_BEEF_PIE=new Item[]
			{Feast.Items.WHEATMEAL,Feast.Items.SALT,Feast.Items.WHEATMEAL,
			 Items.BAKED_POTATO,Items.COOKED_BEEF,Items.BAKED_POTATO,
			 Feast.Items.WHEATMEAL,Items.EGG,Feast.Items.WHEATMEAL};
	
	private static final Item[] RECIPE_PIZZA_PART=new Item[]
			{Items.RED_MUSHROOM,Feast.Items.SALT,Items.BROWN_MUSHROOM,
			 Items.CARROT,Items.COOKED_BEEF,Items.KELP,
			 Feast.Items.WHEATMEAL,Feast.Items.CHEESE,Feast.Items.WHEATMEAL};
	
	private static final Item[] RECIPE_COD_PASTRY=new Item[]
			{Feast.Items.WHEATMEAL,Feast.Items.SALT,Feast.Items.WHEATMEAL,
			 Feast.Items.WHEATMEAL,Items.COOKED_COD,Feast.Items.WHEATMEAL,
			 Feast.Items.WHEATMEAL,Items.EGG,Feast.Items.WHEATMEAL};
	
	private static final Item[] RECIPE_SALMON_ROLL=new Item[]
			{Feast.Items.WHEATMEAL,Feast.Items.SALT,Feast.Items.WHEATMEAL,
			 Items.AIR,Items.COOKED_SALMON,Items.AIR,							//cabbage,cucumber
			 Feast.Items.WHEATMEAL,Items.EGG,Feast.Items.WHEATMEAL
			};
	
	public static Item match(@Nonnull()Item[] input) {
		if (Arrays.equals(input,RECIPE_APPLE_PIE)) {
			return Feast.Items.APPLE_PIE;
		}
		else if (Arrays.equals(input,RECIPE_POTATO_BEEF_PIE)) {
			return Feast.Items.POTATO_BEEF_PIE;
		}
		else if (Arrays.equals(input,RECIPE_PIZZA_PART)) {
			return Feast.Items.PIZZA_PART;
		}
		else if (Arrays.equals(input,RECIPE_COD_PASTRY)) {
			return Feast.Items.COD_BAR;
		}
		else if (Arrays.equals(input,RECIPE_SALMON_ROLL)) {
			return Feast.Items.SALMON_ROLL;
		}
		else {
			return null;
		}
	}
}
