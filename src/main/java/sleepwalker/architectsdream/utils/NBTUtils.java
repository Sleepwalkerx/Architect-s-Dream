package sleepwalker.architectsdream.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.exseption.NBTParseException;

public final class NBTUtils {

    private NBTUtils() { }

    public static String getString(@Nonnull CompoundNBT compoundNBT, String key){
        return getString(compoundNBT, key, null);
    }

    public static String getString(@Nonnull CompoundNBT compoundNBT, String key, String defaultValue){
        if(compoundNBT.contains(key, 8)){
            return compoundNBT.getString(key);
        }
        else return defaultValue;
    }

    public static <T extends Enum<T>> T getEnum(@Nonnull CompoundNBT compoundNBT, String key, Class<T> enumClass){
        return getEnum(compoundNBT, key, enumClass, null);
    }

    public static <T extends Enum<T>> T getEnum(@Nonnull CompoundNBT compoundNBT, String key, Class<T> enumClass, T defaultValue){
        if(compoundNBT.contains(key, 3)){
            return enumClass.getEnumConstants()[compoundNBT.getInt(key)];
        }
        else return defaultValue;
    }

    public static JsonElement parseToJson(@Nonnull INBT nbt){
        return readType(nbt);
    }

    @Nullable
    private static JsonElement readType(@Nonnull INBT nbt){

        switch (nbt.getId()){

            case 1:
                return new JsonPrimitive(((ByteNBT)nbt).getAsNumber());

            case 2:
                return new JsonPrimitive(((ShortNBT)nbt).getAsNumber());

            case 3:
                return new JsonPrimitive(((IntNBT)nbt).getAsNumber());

            case 4:
                return new JsonPrimitive(((LongNBT)nbt).getAsNumber());

            case 5:
                return new JsonPrimitive(((FloatNBT)nbt).getAsNumber());

            case 6:
                return new JsonPrimitive(((DoubleNBT)nbt).getAsNumber());

            case 7:
                JsonArray byteArray = new JsonArray();
                for(byte value : ((ByteArrayNBT)nbt).getAsByteArray()){
                    byteArray.add(value);
                }
                return byteArray;

            case 8:
                return new JsonPrimitive(nbt.getAsString());

            case 9:
                JsonArray jsonArray = new JsonArray();
                for(INBT element : ((ListNBT)nbt)){
                    jsonArray.add(readType(element));
                }
                return jsonArray;

            // ListNBt
            case 10:
                JsonObject jsonObject = new JsonObject();
                CompoundNBT compoundNBT = (CompoundNBT) nbt;

                for(String key : compoundNBT.getAllKeys()){
                    jsonObject.add(key, readType(compoundNBT.get(key)));
                }

                return jsonObject;

            case 11:
                JsonArray intArray = new JsonArray();
                for(int value : ((IntArrayNBT)nbt).getAsIntArray()){
                    intArray.add(value);
                }
                return intArray;

            case 12:
                JsonArray longArray = new JsonArray();
                for(long value : ((LongArrayNBT)nbt).getAsLongArray()){
                    longArray.add(value);
                }
                return longArray;

            default:
                return null;
        }
    }
}
