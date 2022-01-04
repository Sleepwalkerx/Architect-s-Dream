package sleepwalker.architectsdream.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public interface ISavableHandler {

    String COMPOUND_SAVABLE = "savable_objects";

    default void readSavableObjects(CompoundNBT orig){

        CompoundNBT nbt = orig.getCompound(COMPOUND_SAVABLE);

        getSavableObjects().forEach((name, savable) ->
            savable.readData(
                nbt.get(savable.getRegistryName().toString())
            )
        );
    }

    @Nonnull
    default CompoundNBT saveSavableObjects(CompoundNBT orig){

        CompoundNBT nbt = orig.getCompound(COMPOUND_SAVABLE);

        getSavableObjects().forEach((name, savable) ->
            nbt.put(savable.getRegistryName().toString(), savable.saveData())
        );

        return nbt;
    }

    default void addSavableObject(ISavable savable){
        getSavableObjects().put(savable.getRegistryName(), savable);
    }


    @Nonnull
    Map<ResourceLocation, ISavable> getSavableObjects();

}
