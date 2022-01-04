package sleepwalker.architectsdream.structure;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;

public enum EnumCondition {
    WHOLE,
    DEFECTIVE;

    @OnlyIn(Dist.CLIENT) 
    public final String translationKey;

    EnumCondition(){
        translationKey = String.format("%s.blueprint.condition.%s", ArchitectsDream.MODID, toString().toLowerCase());
    }
}
