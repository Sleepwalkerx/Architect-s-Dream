package sleepwalker.architectsdream.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ContainerBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ContainerBlueprintCreator;

public final class Containers {
    private Containers(){}
    
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(
        ForgeRegistries.CONTAINERS, ArchitectsDream.MODID
    );

    public static final RegistryObject<ContainerType<ContainerBlueprintCreator>> TypeBlueprintMaker =
    CONTAINERS.register(
        "blueprint_template",
        () -> IForgeContainerType.create(ContainerBlueprintCreator::fromNetwork)
    );

    public static final RegistryObject<ContainerType<ContainerBlueprintViewer>> TypeBlueprint =
    CONTAINERS.register(
        "blueprint", 
        () -> IForgeContainerType.create(ContainerBlueprintViewer::fromNetwork)
    );
}
