package sleepwalker.architectsdream.serialize.converters;

import net.minecraft.nbt.CompoundNBT;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.EnumCondition;

import javax.annotation.Nonnull;

public final class BlueprintPropertiesSerializer {

    public static CompoundNBT DEFAULT = new CompoundNBT();

    public static Blueprint.Properties deserialize(@Nonnull CompoundNBT rulesNBT){

        int numberOfUse = rulesNBT.getInt(R.Properties.NUMBER_OF_USE);

        if(numberOfUse == 0){
            numberOfUse = -1;
        }

        Blueprint.Properties newProperties = new Blueprint.Properties(
            numberOfUse,
            EnumNBT.deserialize(rulesNBT, R.Properties.CONDITION, EnumCondition.WHOLE, EnumCondition.class)
        );

        if(newProperties.equals(Blueprint.Properties.DEFAULT)){
            return Blueprint.Properties.DEFAULT;
        }
        else return newProperties;
    }

    public static CompoundNBT serialize(Blueprint.Properties properties){

        if(properties == Blueprint.Properties.DEFAULT){
            return DEFAULT;
        }

        CompoundNBT nbtRules = new CompoundNBT();

        if(properties.getNumberOfUses() != Blueprint.Properties.INFINITY){
            nbtRules.putInt(R.Properties.NUMBER_OF_USE, properties.getNumberOfUses());
        }

        if(properties.getCondition() != EnumCondition.WHOLE){
            nbtRules.putString(R.Properties.CONDITION, properties.getCondition().name());
        }

        return nbtRules;
    }
}
