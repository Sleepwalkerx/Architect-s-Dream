package sleepwalker.architectsdream.serialize.converters;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.vector.Vector3i;
import sleepwalker.architectsdream.exception.NBTParseException;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.R;

public class UVector3iNBT {

    public static UVector3i deserialize(CompoundNBT compoundNBT, String key){
        if(compoundNBT.contains(key, 9)){
            ListNBT listNbt = compoundNBT.getList(key, 3);

            if(listNbt.size() == 3){
                return new UVector3i(listNbt.getInt(0), listNbt.getInt(1), listNbt.getInt(2));
            }
            else throw new NBTParseException("Vector3i must have 3 numbers");
        }
        else if(compoundNBT.contains(key, 11)){
            int[] arrayNBT = compoundNBT.getIntArray(key);

            if(arrayNBT.length == 3){
                return new UVector3i(arrayNBT[0], arrayNBT[1], arrayNBT[2]);
            }
            else throw new NBTParseException("Vector3i must have 3 numbers");
        }
        else throw new NBTParseException(String.format(R.Exception.UNSUPPORTED_TYPE, key));
    }

    public static IntArrayNBT serialize(Vector3i vector3i){
        return new IntArrayNBT(new int[]{ vector3i.getX(), vector3i.getY(), vector3i.getZ() });
    }
}
