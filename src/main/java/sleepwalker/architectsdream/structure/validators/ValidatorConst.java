package sleepwalker.architectsdream.structure.validators;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.NonNullList;

import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.container.IVerifiable;

public class ValidatorConst implements IValidator {

    protected Map<UBlockPos, IVerifiable> entities;

    public ValidatorConst(Map<UBlockPos, IVerifiable> entities){
        this.entities = entities;
    }

    @Override
    public boolean matches(UBlockPos shiftPos, Blueprint.Structure structure, ItemUseContext itemContext) {

        for(Entry<UBlockPos, IVerifiable> entry : entities.entrySet()){
            if(!verifyProperties(entry.getValue(), entry.getKey(), shiftPos, structure, itemContext)){
                return false;
            }
        }

        return true;
    }

    @Override
    public @Nonnull List<UBlockPos> matchesShiftPos(Blueprint.Structure structure, ItemUseContext itemContext) {

        List<UBlockPos> shiftPositions = Lists.newArrayList();

        for(Entry<UBlockPos, IVerifiable> entry : entities.entrySet()){
            if(entry.getValue().verify(itemContext.getClickedPos(), structure, itemContext))
                shiftPositions.add(entry.getKey());
        }

        return shiftPositions;
    }

    @Nonnull
    @Override
    public IValidatorSerializer getSerializer() {
        return SerializerManager.VALIDATOR_CONST;
    }

    @Nonnull
    @Override
    public IValidator clone(NonNullList<UBlockPos> newPositions) {

        Map<UBlockPos, IVerifiable> newEntities = Maps.newHashMap();

        int i = 0;
        for(IVerifiable container : entities.values()){
            newEntities.put(newPositions.get(i), container);
            i++;
        }

        return new ValidatorConst(newEntities);
    }

    @Nonnull
    @Override
    public IValidator changeOfAxis(NonNullList<UBlockPos> newPositions) {

        Map<UBlockPos, IVerifiable> newEntities = Maps.newHashMap();

        int i = 0;
        for(IVerifiable container : entities.values()){
            newEntities.put(newPositions.get(i), container.changeOfAxis());
            i++;
        }

        return new ValidatorConst(newEntities);
    }

    @Nonnull
    @Override
    public Collection<UBlockPos> getPositions() { return entities.keySet(); }

    @Nonnull
    @Override
    public Map<UBlockPos, IVerifiable> getEntities() {
        return entities;
    }

    @Override
    public int hashCode() { return entities.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ValidatorConst){
            ValidatorConst validator = (ValidatorConst)obj;

            if(validator.entities.size() != entities.size())
                return false;

            return entities.equals(validator.entities);
        }
        else return false;
    }
}
