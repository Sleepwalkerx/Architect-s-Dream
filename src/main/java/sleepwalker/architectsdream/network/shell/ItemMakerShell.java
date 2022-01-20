package sleepwalker.architectsdream.network.shell;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemMakerShell extends BlueprintShell {

    @Nonnull
    private final List<ItemStack> itemStacks;

    public ItemMakerShell(
            @Nonnull ResourceLocation id,
            @Nonnull Blueprint.Rarity rarity,
            @Nonnull Blueprint.Properties properties,
            @Nullable ItemStack icon,
            @Nonnull List<ItemStack> items
    ) {
        super(id, rarity, properties, icon);

        this.itemStacks = items;
    }

    @Nonnull
    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }
}
