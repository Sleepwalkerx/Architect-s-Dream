package sleepwalker.architectsdream.network.shell;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemMakerShell extends BlueprintShell {

    @Nonnull
    private final List<ItemStack> itemStacks;

    public ItemMakerShell(ResourceLocation id, Blueprint.Rarity rarity, @Nullable ItemStack icon, @Nonnull List<ItemStack> items) {
        super(id, rarity, icon);

        this.itemStacks = items;
    }

    @Nonnull
    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }
}
