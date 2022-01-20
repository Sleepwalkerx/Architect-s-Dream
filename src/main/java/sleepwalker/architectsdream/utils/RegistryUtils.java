package sleepwalker.architectsdream.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.CustomScreenCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.screens.StructureViewerScreen;
import sleepwalker.architectsdream.client.gui.blueprint_creator.screens.engine.ScreenEngineItemMaker;
import sleepwalker.architectsdream.client.gui.blueprint_creator.screens.types.BlockTypeScreen;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.ValidatorMode;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModelProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block.BlockModelProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.engine.IEngineProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.engine.ItemMakerProvider;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.serialize.type.IPaletteTypeSerializer;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.DataType;
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

        registryPaletteType(R.BlockContainer.NAME, BlockModelProvider.PROVIDER, BlockTypeScreen::new);

        registryEngine(R.EngineItemMaker.NAME, ItemMakerProvider.PROVIDER, ScreenEngineItemMaker::new);
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
    public static void registryPaletteType(
        ResourceLocation name,
        IModelProvider provider,
        CustomScreenCreator screenConstructor
    ){
        ScreenBlueprintCreator.REGISTRY.put(name, Pair.of(DataType.PALETTE_TYPE, screenConstructor));
        ScreenBlueprintViewer.MODEL_PROVIDERS.put(provider.getTypeName(), provider);
    }

    public static <T extends BaseStructureEngine> void registryEngine(IEngineSerializer<T> serializer){
        SerializerManager.ENGINES.put(serializer.getRegistryName(), serializer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registryEngine(ResourceLocation name, IEngineProvider provider, CustomScreenCreator constructor){
        ScreenBlueprintCreator.REGISTRY.put(name, Pair.of(DataType.ENGINE, constructor));
        ScreenBlueprintViewer.ENGINES_PROVIDERS.put(provider.getSerializer(), provider);
    }

    public static String convert(@Nonnull ResourceLocation location){
        return location.getNamespace().equals(ArchitectsDream.MODID) ? location.getPath() : location.toString();
    }
}
