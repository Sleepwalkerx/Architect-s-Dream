package sleepwalker.architectsdream.client;

import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.ArchitectsDream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public interface ISavable {

    @Nonnull
    INBT saveData();

    void readData(@Nullable INBT nbt);

    @Nonnull
    default ResourceLocation getRegistryName(){
        return new ResourceLocation(ArchitectsDream.MODID, getClass().getName().toLowerCase(Locale.ROOT));
    }
}
