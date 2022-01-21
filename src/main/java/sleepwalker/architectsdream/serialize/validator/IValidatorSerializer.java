package sleepwalker.architectsdream.serialize.validator;

import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.exception.NBTParseException;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IValidatorSerializer {

    @Nullable
    INBT serialize(@Nonnull Map<UBlockPos, Integer> entities);

    @Nonnull
    IValidator deserialize(@Nonnull INBT objectIn, @Nonnull List<IVerifiable> entities) throws NBTParseException;

    @Nonnull
    ResourceLocation getRegistryName();
}
