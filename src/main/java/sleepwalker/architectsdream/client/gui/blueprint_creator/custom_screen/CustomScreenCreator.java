package sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.structure.DataType;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface CustomScreenCreator {
    BaseCustomScreen of(@Nonnull ScreenBlueprintCreator parent, @Nonnull ResourceLocation registrationID, DataType type);
}
