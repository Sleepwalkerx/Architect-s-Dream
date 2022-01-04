package sleepwalker.architectsdream.serialize.converters;

import net.minecraft.nbt.CompoundNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.exseption.NBTParseException;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.structure.engine.BaseStructureEngine;
import sleepwalker.architectsdream.utils.RegistryUtils;

import javax.annotation.Nonnull;

public class EngineNBT {

    private final static Logger logger = LogManager.getLogger();

    @Nonnull
    public static CompoundNBT serialize(@Nonnull BaseStructureEngine src){

        CompoundNBT main = new CompoundNBT();

        main.putString(R.NBTNames.ENGINE_NAME, RegistryUtils.convert(src.getSerialize().getRegistryName()));

        CompoundNBT properties = new CompoundNBT();

        cast(src, src.getSerialize(), properties);

        if(!properties.isEmpty()) {
            main.put(R.NBTNames.ENGINE_PROPERTIES, properties);
        }

        return main;
    }

    @SuppressWarnings("unchecked")
    private static  <T extends BaseStructureEngine> void cast(BaseStructureEngine src, @Nonnull IEngineSerializer<T> serializer, CompoundNBT properties){
        serializer.serialize((T)src, properties);
    }

    @Nonnull
    public static BaseStructureEngine deserialize(@Nonnull CompoundNBT engineNBT) throws NBTParseException {

        String funcName = engineNBT.getString(R.NBTNames.ENGINE_NAME);
        IEngineSerializer<?> serializer = SerializerManager.ENGINES.get(ArchitectsDream.location(funcName));

        if(serializer == null){
            logger.error(String.format(R.Exception.UNKNOWN_NAME, "ENGINE", funcName));
            return BaseStructureEngine.DEFAULT_ENGINE;
        }

        CompoundNBT properties = engineNBT.getCompound(R.NBTNames.ENGINE_PROPERTIES);

        return serializer.deserialize(properties);
    }
}
