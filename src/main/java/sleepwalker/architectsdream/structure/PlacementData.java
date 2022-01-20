package sleepwalker.architectsdream.structure;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.validators.IValidator;

public final class PlacementData {

    /**
     *  Set никогда не пустой
     */
    protected final Map<ResourceLocation, Set<IValidator>> validators;

    /**
     * Set - набор различных валидаторов констроля одного типа IVerifiable
    */
    public PlacementData(Map<ResourceLocation, Set<IValidator>> validators){
        this.validators = validators;
    }

    public @Nullable BlockPos matches(Blueprint.Structure structure, ItemUseContext itemContext) {
        for(Set<IValidator> shiftSet : validators.values()){
            for(IValidator validatorShift : shiftSet){
                for(UBlockPos shiftPos : validatorShift.matchesShiftPos(structure, itemContext)){
                    // ======== //

                    if(validators.values().stream().allMatch(
                        set -> 
                            set.stream().allMatch(validator -> 
                                validator.matches(shiftPos, structure, itemContext)
                            )
                        )
                    ) return shiftPos;
                }
            }
        }
        return null;
    }

    public Map<ResourceLocation, Set<IValidator>> getValidators(){
        return validators;
    }
}
