package sleepwalker.architectsdream.serialize.validator;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.exception.NBTParseException;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.converters.UVector3iNBT;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.validators.IValidator;
import sleepwalker.architectsdream.structure.validators.ValidatorConst;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ValidatorSerialConst implements IValidatorSerializer {

    @Nullable
    @Override
    public INBT serialize(@Nonnull Map<UBlockPos, Integer> entities) {
        ListNBT listNBT = new ListNBT();

        entities.forEach((blockPos, index) -> {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putInt(R.Validator.PALETTE_STATE, index);
            compoundNBT.put(R.Validator.POSITION, UVector3iNBT.serialize(blockPos));
            listNBT.add(compoundNBT);
        });

        return listNBT.size() == 0 ? null : listNBT;
    }

    @Nonnull
    @Override
    public IValidator deserialize(@Nonnull INBT objectIn, @Nonnull List<IVerifiable> entities) throws NBTParseException {
        ListNBT listNBT = (ListNBT) objectIn;

        Map<UBlockPos, IVerifiable> map = Maps.newHashMapWithExpectedSize(listNBT.size());

        for(int i = 0; i < listNBT.size(); i++){
            CompoundNBT compoundNBT = listNBT.getCompound(i);

            int state = compoundNBT.getInt(R.Validator.PALETTE_STATE);

            if(state < 0 || state >= entities.size()){
                throw new NBTParseException(String.format(R.Exception.PALETTE_INDEX_OUT, state));
            }

            map.put(
                new UBlockPos(UVector3iNBT.deserialize(compoundNBT, R.Validator.POSITION)),
                entities.get(state)
            );
        }

        return new ValidatorConst(map);
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return R.Validator.CONST;
    }
}
