package sleepwalker.architectsdream.structure;

import sleepwalker.architectsdream.ArchitectsDream;

public enum EnumCondition {
    WHOLE,
    DEFECTIVE;

    public final String translationKey;

    EnumCondition(){
        translationKey = String.format("%s.blueprint.condition.%s", ArchitectsDream.MODID, toString().toLowerCase());
    }
}
