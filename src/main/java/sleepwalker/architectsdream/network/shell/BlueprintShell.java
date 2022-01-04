package sleepwalker.architectsdream.network.shell;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nullable;
import java.util.Objects;

//CLIENT SHELL
public class BlueprintShell {

    protected final ResourceLocation id;

    protected final Blueprint.Rarity rarity;

    @Nullable
    protected final ItemStack icon;

    public BlueprintShell(ResourceLocation id, Blueprint.Rarity rarity, @Nullable ItemStack icon) {

        this.rarity = rarity;
        this.id = id;
        this.icon = icon;
    }

    public Blueprint.Rarity getRarity() {
        return rarity;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Nullable
    public ItemStack getIcon() {
        return icon;
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
