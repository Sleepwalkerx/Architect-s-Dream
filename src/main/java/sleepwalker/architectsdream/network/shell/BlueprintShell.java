package sleepwalker.architectsdream.network.shell;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

//CLIENT SHELL
public class BlueprintShell {

    @Nonnull
    protected final ResourceLocation id;

    @Nonnull
    protected final Blueprint.Rarity rarity;

    @Nonnull
    protected final Blueprint.Properties defaultProperties;

    @Nullable
    protected final ItemStack icon;

    public BlueprintShell(@Nonnull ResourceLocation id, @Nonnull Blueprint.Rarity rarity, @Nonnull Blueprint.Properties defaultProperties, @Nullable ItemStack icon) {

        this.rarity = rarity;
        this.id = id;
        this.icon = icon;
        this.defaultProperties = defaultProperties;
    }

    @Nonnull
    public Blueprint.Rarity getRarity() {
        return rarity;
    }

    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Nullable
    public ItemStack getIcon() {
        return icon;
    }

    @Nonnull
    public Blueprint.Properties getDefaultProperties() {
        return defaultProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlueprintShell that = (BlueprintShell) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
