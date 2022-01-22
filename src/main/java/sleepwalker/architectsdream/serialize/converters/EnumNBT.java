package sleepwalker.architectsdream.serialize.converters;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import sleepwalker.architectsdream.utils.NBTTypes;
import sleepwalker.architectsdream.utils.NBTUtils;

import javax.annotation.Nonnull;

public class EnumNBT {

    public static <T extends Enum<T>> T deserialize(@Nonnull CompoundNBT compoundNBT, String key, @Nonnull T defaultValue, @Nonnull Class<T> enumClass){

        if(compoundNBT.contains(key, NBTTypes.STRING)){

            String name = compoundNBT.getString(key).toLowerCase();

            for(T enumValue : enumClass.getEnumConstants()){
                if(name.equals(enumValue.toString().toLowerCase())){
                    return enumValue;
                }
            }
        }

        return defaultValue;
    }

    @Nonnull
    public static <T extends Enum<T>> StringNBT serialize(@Nonnull T value){
        return StringNBT.valueOf(value.toString().toLowerCase());
    }
}
