package org.watp.umc.feast.recipe;

import org.watp.umc.feast.Feast;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class MRMRecipe {
	public static Item match(Item input) {
		if (input==Items.MILK_BUCKET) {
			return Feast.Items.CREAM;
		}
		else if (input==Feast.Items.CREAM) {
			return Feast.Items.BUTTER;
		}
		else if (input==Feast.Items.BUTTER) {
			return Feast.Items.CHEESE;
		}
		else {
			return null;
		}
	}
}
