package sleepwalker.architectsdream;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import sleepwalker.architectsdream.plugins.jei.JeiSleepPlugin;

import javax.annotation.Nonnull;

public final class ModStates {

    public static boolean JEI;
    private static final String JEI_NAME = "jei";

    public static void check(){
        JEI = isLoad(JEI_NAME);
    }

    public static void loadClient(){

        if(JEI){
            MinecraftForge.EVENT_BUS.register(JeiSleepPlugin.class);
        }
    }

    private static boolean isLoad(@Nonnull String name){
        return ModList.get().isLoaded(name);
    }
}
