package org.watp.umc.feast.registry;

import org.watp.umc.feast.Feast;
import org.watp.umc.feast.item.*;
import org.watp.umc.feast.item.food.*;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS=DeferredRegister.create(ForgeRegistries.ITEMS,Feast.MODID);
	
	public static RegistryObject<Item> OVEN=
			ITEMS.register("oven",() -> new BlockItem(BlockRegistry.blockOven.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
	public static RegistryObject<Item> DAIRY_MACHINE=
			ITEMS.register("dairy_machine",() -> new BlockItem(BlockRegistry.blockDairyMachine.get(),new Item.Properties().group(ItemGroup.DECORATIONS)));
	public static RegistryObject<Item> SALT_ORE=
			ITEMS.register("salt_ore",() -> new BlockItem(BlockRegistry.blockSaltOre.get(),new Item.Properties().group(ItemGroup.DECORATIONS)));
	
	public static RegistryObject<Item> WHEATMEAL=ITEMS.register("wheatmeal",WheatmealItem::new);
	public static RegistryObject<Item> SALT=ITEMS.register("salt",SaltItem::new);

	public static RegistryObject<Item> COOKED_EGG=ITEMS.register("cooked_egg",CookedEggItem::new);
	public static RegistryObject<Item> CREAM=ITEMS.register("cream",CreamItem::new);
	public static RegistryObject<Item> BUTTER=ITEMS.register("butter",ButterItem::new);
	public static RegistryObject<Item> CHEESE=ITEMS.register("cheese",CheeseItem::new);
	public static RegistryObject<Item> APPLE_PIE=ITEMS.register("apple_pie",ApplePieItem::new);
	public static RegistryObject<Item> POTATO_BEEF_PIE=ITEMS.register("potato_beef_pie",PotatoBeefPieItem::new);
	public static RegistryObject<Item> PIZZA_PART=ITEMS.register("pizza_part",PizzaPartItem::new);
	public static RegistryObject<Item> COD_BAR=ITEMS.register("cod_bar",CodBarItem::new);
	public static RegistryObject<Item> SALMON_ROLL=ITEMS.register("salmon_roll",SalmonRollItem::new);

	public static RegistryObject<Item> CABBAGE_SEEDS=ITEMS.register("cabbage_seeds", CabbageSeedsItem::new);
	//public static RegistryObject<Item> CABBAGE=
	//		ITEMS.register("cabbage", () -> new BlockItem(BlockRegistry.blockCabbage.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
}