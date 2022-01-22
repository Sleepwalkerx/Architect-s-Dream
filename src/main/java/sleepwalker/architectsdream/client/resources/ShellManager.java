package sleepwalker.architectsdream.client.resources;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.network.shell.BlueprintShell;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ShellManager {

    // Содержит Данные для CreativeTab, Jei ...
    private static Map<ResourceLocation, BlueprintShell> CLIENT_STORAGE = Collections.emptyMap();

    public static boolean isLoad;

    public static void loadClientStorage(@Nonnull List<BlueprintShell> shells){
        CLIENT_STORAGE = shells.stream().collect(Collectors.toMap(BlueprintShell::getId, blueprintShell -> blueprintShell));
        isLoad = true;
    }

    public static void clearStorage(){
        CLIENT_STORAGE.clear();
        isLoad = false;
    }

    public static Map<ResourceLocation, BlueprintShell> getClientStorage() {
        return CLIENT_STORAGE;
    }
}
