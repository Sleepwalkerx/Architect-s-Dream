package sleepwalker.architectsdream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import sleepwalker.architectsdream.client.gui.BlueprintOverlayGui;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.WorldRenderer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.resources.ShellManager;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.init.Containers;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.items.ItemBlueprint;
import sleepwalker.architectsdream.items.ItemBlueprintCreator;
import sleepwalker.architectsdream.listeners.PlayerClientEventListener;
import sleepwalker.architectsdream.listeners.PlayerEventListener;
import sleepwalker.architectsdream.network.PacketHandler;
import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.utils.BlueprintUtils;
import sleepwalker.architectsdream.utils.RegistryUtils;

import javax.annotation.Nonnull;

@Mod(ArchitectsDream.MODID)
public final class ArchitectsDream {

    public static final String MODID = "architectsdream";
    public static final DefaultArtifactVersion STRUCTURE_VERSION = new DefaultArtifactVersion("1.0.0");
    
    public static final ItemGroup mainItemGroup = new ItemGroup(MODID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.DarkDust.get());
        }
    };

    public static final ItemGroup blueprintItemGroup = new ItemGroup("blueprints") {

        @Override
        public void fillItemList(@Nonnull NonNullList<ItemStack> items) {

            ShellManager.getClientStorage().forEach((resourceLocation, structureShell) -> {

                items.add(BlueprintUtils.setBlueprintToItem(resourceLocation));
            });
        }

        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return BlueprintUtils.setBlueprintToItem(Blueprint.DEFAULT.getID());
        }
    };

    public static final Logger LOGGER = LogManager.getLogger();
    
    public ArchitectsDream() {

        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::commonRegistry);
        eventBus.addListener(this::clientRegistry);

        Items.ITEMS.register(eventBus);
        Containers.CONTAINERS.register(eventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::addReloadListeners);

        MinecraftForge.EVENT_BUS.register(PlayerEventListener.class);
    }

    private void addReloadListeners(@Nonnull final AddReloadListenerEvent event){
        event.addListener(new BlueprintManager());
    }

    private void commonRegistry(final FMLCommonSetupEvent event){

        ModStates.check();

        MinecraftForge.EVENT_BUS.register(ItemBlueprintCreator.class);

        RegistryUtils.initCommon();
        PacketHandler.init();
    }

    @Nonnull
    public static ResourceLocation location(@Nonnull String origin){
        return origin.indexOf(':') != -1 ? new ResourceLocation(origin) : new ResourceLocation(ArchitectsDream.MODID, origin);
    }

    @Nonnull
    public static ResourceLocation namespace(@Nonnull String path){
        return new ResourceLocation(MODID, path);
    }

    private void clientRegistry(@Nonnull final FMLClientSetupEvent event){

        ModStates.loadClient();

        MinecraftForge.EVENT_BUS.register(new WorldRenderer(Minecraft.getInstance()));
        MinecraftForge.EVENT_BUS.register(new BlueprintOverlayGui());
        MinecraftForge.EVENT_BUS.register(PlayerClientEventListener.class);

        ScreenManager.register(Containers.TypeBlueprintMaker.get(), ScreenBlueprintCreator::new);
        ScreenManager.register(Containers.TypeBlueprint.get(), ScreenBlueprintViewer::new);

        RegistryUtils.initClient();

        event.enqueueWork(() -> {
            ItemModelsProperties.register(
                Items.Blueprint.get(), 
                new ResourceLocation(R.Blueprint.RARITY),
                (stack, worldIn, entityIn) ->
                BlueprintUtils.getBlueprintRarity(stack).ordinal()
            );

            ItemModelsProperties.register(
                Items.Blueprint.get(), 
                new ResourceLocation(R.Properties.CONDITION),
                (stack, worldIn, entityIn) ->
                BlueprintUtils.getItemStackClientProperties(stack).getCondition().ordinal()
            );
        });
    }
}
