package sleepwalker.architectsdream.init;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.items.*;

public final class Items {
	private Items(){ throw new IllegalStateException("Utility class"); }
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
		ForgeRegistries.ITEMS, ArchitectsDream.MODID
	);

	public static final RegistryObject<Item> DarkDust = ITEMS.register(
		"dark_dust", () -> new Item(new Properties().tab(ArchitectsDream.mainItemGroup))
	);
	public static final RegistryObject<ItemBlueprint> Blueprint = ITEMS.register(
		"blueprint", () -> new ItemBlueprint(new Properties().stacksTo(1))
	);
	public static final RegistryObject<ItemBlueprintCreator> BlueprintCreator = ITEMS.register(
		"blueprint_creator", () -> new ItemBlueprintCreator(new Properties().stacksTo(1).tab(ArchitectsDream.mainItemGroup))
	);
}
