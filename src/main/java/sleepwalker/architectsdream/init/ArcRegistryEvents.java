package sleepwalker.architectsdream.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.loot.functions.BlueprintDropLootModifier;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = ArchitectsDream.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArcRegistryEvents {

    @SubscribeEvent
    public static void registerModifierEvent(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event){

        reg(event.getRegistry(),
                "piglin_bartering",
                "zombified_piglin",
                "magma_cube",
                "witch",
                "player",
                "nether_bridge"
        );
    }

    private static void reg(IForgeRegistry<GlobalLootModifierSerializer<?>> registry, @Nonnull String... names){

        for(String name : names){
            registry.register(new BlueprintDropLootModifier.Serializer().setRegistryName(new ResourceLocation(ArchitectsDream.MODID, name)));
        }
    }
}
