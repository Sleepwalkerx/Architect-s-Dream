package sleepwalker.architectsdream.plugins.jei;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class ItemMakerRecipe {

    private final List<ItemStack> items;
    private final ITextProperties description;
    private final ItemStack blueprintItem;

    public ItemMakerRecipe(List<ItemStack> items, ItemStack blueprintItem){

        this.items = items;
        this.description = new StringTextComponent("TEST TEST");
        this.blueprintItem = blueprintItem;
    }

    public ITextProperties getDescription() {
        return description;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public ItemStack getBlueprintItem() {
        return blueprintItem;
    }
}
