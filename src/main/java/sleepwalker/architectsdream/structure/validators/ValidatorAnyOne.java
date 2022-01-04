package sleepwalker.architectsdream.structure.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.NonNullList;

import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.Blueprint.Structure;
import sleepwalker.architectsdream.structure.container.IVerifiable;

public class ValidatorAnyOne implements IValidator {

    protected final List<UBlockPos> positions;
    protected final List<IVerifiable> containers;

    public ValidatorAnyOne(List<UBlockPos> positions, List<IVerifiable> containers){
        this.containers = containers;
        this.positions = positions;
    }

    @Override
    public boolean matches(UBlockPos shiftPos, Structure structure, ItemUseContext itemContext) {
        return containers.stream().anyMatch(container -> 
            positions.stream().allMatch(pos -> verifyProperties(container, pos, shiftPos, structure, itemContext))
        );
    }

    @Override
    public @Nonnull Collection<UBlockPos> matchesShiftPos(Blueprint.Structure structure, ItemUseContext itemContext) {
        return containers.stream().anyMatch(container -> 
            container.verify(itemContext.getClickedPos(), structure, itemContext)) ?
            this.positions : Collections.emptyList()
        ;
    }

    @Nonnull
    @Override
    public IValidatorSerializer getSerializer() {
        // TODO: Make Serializer
        return null;
    }

    @Nonnull
    @Override
    public IValidator clone(NonNullList<UBlockPos> newPositions) {
        return new ValidatorAnyOne(Lists.newArrayList(positions), Lists.newArrayList(containers));
    }

    @Nonnull
    @Override
    public IValidator changeOfAxis(NonNullList<UBlockPos> newPositions) {
        return new ValidatorAnyOne(
            newPositions,
            this.containers.stream().map(IVerifiable::changeOfAxis).collect(Collectors.toList())
        );
    }

    @Nonnull
    @Override
    public Collection<UBlockPos> getPositions() {
        return positions;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(positions, containers);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ValidatorAnyOne){
            ValidatorAnyOne validator = (ValidatorAnyOne)obj;
            return equalLists(positions, validator.positions) && containers.equals(validator.containers);
        }
        else return false;
    }

    protected <T extends Comparable<? super T>> boolean equalLists(List<T> a, List<T> b){
        if(a == null && b == null)
            return true;
        else if(a == null || b == null || a.size() != b.size())
            return false;

        List<T> aSort = Lists.newArrayList(a);
        Collections.sort(aSort);

        List<T> bSort = Lists.newArrayList(b);
        Collections.sort(bSort);

        return aSort.equals(bSort);
    }

    @Nonnull
    @Override
    public Map<UBlockPos, IVerifiable> getEntities() {
        // TODO: UNREALIZED CLASS
        return null;
    }
}
