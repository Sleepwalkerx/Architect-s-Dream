package sleepwalker.architectsdream.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_maker.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.ScreenConstructor;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.ScreenPurpose;
import sleepwalker.architectsdream.client.gui.blueprint_maker.screens.engine.ScreenEngineItemMaker;
import sleepwalker.architectsdream.client.gui.blueprint_maker.screens.types.BlockTypeScreen;
import sleepwalker.architectsdream.client.gui.blueprint_maker.utils.ValidatorMode;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoGroup;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.ITypeProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block.BlockTypeProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.engine.IEngineProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.engine.ItemMakerProvider;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.serialize.type.IPaletteTypeSerializer;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.engine.BaseStructureEngine;

import javax.annotation.Nonnull;

public final class RegistryUtils {

    private RegistryUtils(){}

    public static void initCommon() {

        registryValidator(SerializerManager.VALIDATOR_CONST);

        registryPaletteType(SerializerManager.TYPE_BLOCK);

        registryEngine(SerializerManager.ENGINE_ITEM_MAKER);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initClient(){

        registryValidator(ValidatorMode.CONST);

        registryPaletteType(R.BlockContainer.NAME, BlockTypeProvider.PROVIDER, ScreenPurpose.PALETTE_TYPE, BlockTypeScreen::new);

        registryEngine(R.EngineItemMaker.NAME, ScreenPurpose.ENGINE, ScreenEngineItemMaker::new, new ItemMakerProvider());
    }



    // ====================
    public static void registryValidator(IValidatorSerializer serializer){
        SerializerManager.VALIDATORS.put(serializer.getRegistryName(), serializer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registryValidator(ValidatorMode validatorMode){
        ValidatorMode.addValidator(validatorMode.getRegistryName(), validatorMode);
    }

    public static <T extends IVerifiable> void registryPaletteType(IPaletteTypeSerializer<T> serializer){
        SerializerManager.TYPES.put(serializer.getRegistryName(), serializer);
    }

    @OnlyIn(Dist.CLIENT)
    public static <E extends IInfoGroup, T extends IVerifiable> void registryPaletteType(
        ResourceLocation name,
        ITypeProvider<T, E> provider,
        ScreenPurpose purpose,
        ScreenConstructor screenConstructor
    ){
        ScreenBlueprintCreator.REGISTRY.put(name, Pair.of(purpose, screenConstructor));
        ScreenBlueprintViewer.MODEL_PROVIDERS.put(provider.getTypeClass(), provider);
    }

    public static <T extends BaseStructureEngine> void registryEngine(IEngineSerializer<T> serializer){
        SerializerManager.ENGINES.put(serializer.getRegistryName(), serializer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registryEngine(ResourceLocation name, ScreenPurpose purpose, ScreenConstructor constructor, IEngineProvider provider){
        ScreenBlueprintCreator.REGISTRY.put(name, Pair.of(purpose, constructor));
        ScreenBlueprintViewer.ENGINES_PROVIDERS.put(provider.getSerializer(), provider);
    }

    public static String convert(@Nonnull ResourceLocation location){
        return location.getNamespace().equals(ArchitectsDream.MODID) ? location.getPath() : location.toString();
    }
}
