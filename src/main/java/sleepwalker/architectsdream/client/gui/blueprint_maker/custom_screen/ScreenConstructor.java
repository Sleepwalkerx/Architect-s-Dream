package sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_maker.ScreenBlueprintCreator;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ScreenConstructor {
    BaseCustomScreen of(@Nonnull ScreenBlueprintCreator parent, @Nonnull ResourceLocation registrationID);
}
