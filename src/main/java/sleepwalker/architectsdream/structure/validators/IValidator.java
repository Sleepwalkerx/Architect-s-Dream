package sleepwalker.architectsdream.structure.validators;

import java.util.Collection;
import java.util.Map;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.NonNullList;

import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.container.IVerifiable;

import javax.annotation.Nonnull;

public interface IValidator extends Comparable<IValidator> {

    @Nonnull
    Map<UBlockPos, IVerifiable> getEntities();

    @Nonnull
    Collection<UBlockPos> getPositions();

    /**
     * Clone Validator
    */
    @Nonnull
    IValidator clone(NonNullList<UBlockPos> newPositions);

    @Nonnull
    IValidator changeOfAxis(NonNullList<UBlockPos> newPositions);

    /**
     * Checks the structure for matching positions
    */
    boolean matches(UBlockPos shiftPos, Blueprint.Structure structure, ItemUseContext itemContext);


    /** 
     * Checks if the main block is one of the validator list.
     * If it does, it returns its coordinate as the offset point
     * @return Shift coordinate list
    */
    @Nonnull
    Collection<UBlockPos> matchesShiftPos(Blueprint.Structure structure, ItemUseContext itemContext);

    @Nonnull
    IValidatorSerializer getSerializer();

    default boolean verifyProperties(
        IVerifiable container,
        UBlockPos posIn,
        UBlockPos shiftPos,
        Blueprint.Structure structure,
        ItemUseContext itemContext
    ){

        return container.verify(
            itemContext.getClickedPos().subtract(shiftPos).offset(posIn),
                structure,
            itemContext
        );
    }

    @Override
    default int compareTo(IValidator o){
        return o.getSerializer().getRegistryName().compareTo(getSerializer().getRegistryName());
    }

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}
