package sleepwalker.architectsdream;

import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;

public final class ModStates {

    public static boolean JEI;
    private static final String JEI_NAME = "jei";

    public static void check(){
        JEI = isLoad(JEI_NAME);
    }

    private static boolean isLoad(@Nonnull String name){
        return ModList.get().isLoaded(name);
    }
}
