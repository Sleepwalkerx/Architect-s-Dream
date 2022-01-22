package sleepwalker.architectsdream.client.gui.blueprint_creator.utils;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.IDisplayName;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.utils.RegistryUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public final class ValidatorMode implements IDisplayName {
    public static final ValidatorMode CONST = new ValidatorMode(0.5f, 0.7f, 1.0f, SerializerManager.VALIDATOR_CONST);

    private static final Map<ResourceLocation, ValidatorMode> registry = Maps.newHashMap();

    @Nonnull
    public static Collection<ValidatorMode> getValidators() {
        return registry.values();
    }

    public static ValidatorMode getValidator(ResourceLocation regName){
        return registry.get(regName);
    }

    public static void addValidator(float r, float g, float b, @Nonnull IValidatorSerializer serializer){
        registry.put(serializer.getRegistryName(), new ValidatorMode(r, g, b, serializer));
    }

    public static void addValidator(ResourceLocation registrationName, ValidatorMode validatorMode){
        registry.put(registrationName, validatorMode);
    }

    private final float r, g, b;
    private final IValidatorSerializer serializer;

    public ValidatorMode(float r, float g, float b, IValidatorSerializer serializer){
        this.r = r;
        this.g = g;
        this.b = b;
        this.serializer = serializer;
    }

    public IValidatorSerializer getSerializer(){
        return serializer;
    }

    @Nonnull
    public ResourceLocation getRegistryName(){
        return serializer.getRegistryName();
    }

    public float getB() {
        return b;
    }

    public float getG() {
        return g;
    }

    public float getR() {
        return r;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(
            String.format("screen.%s.%s.main_setting.validator_mode.%s", ArchitectsDream.MODID, ScreenBlueprintCreator.NAME, RegistryUtils.convert(serializer.getRegistryName()))
        );
    }
}
