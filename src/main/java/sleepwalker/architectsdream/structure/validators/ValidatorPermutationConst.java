package sleepwalker.architectsdream.structure.validators;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.NonNullList;

import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.Blueprint.Structure;
import sleepwalker.architectsdream.structure.container.IVerifiable;

public class ValidatorPermutationConst extends ValidatorAnyOne {

	public ValidatorPermutationConst(List<UBlockPos> positions, List<IVerifiable> containers){
		super(positions, containers);
    }

    @Override
    public boolean matches(UBlockPos shiftPos, Structure structure, ItemUseContext itemContext) {

		if(matchesConstProperties(shiftPos, structure, itemContext))
			return true;

        int[] indexes = new int[positions.size()];

		int i = 0;
		while (i < positions.size()) {
		    if (indexes[i] < i) {
		        swapValues(i % 2 == 0 ?  0: indexes[i], i);

				if(matchesConstProperties(shiftPos, structure, itemContext))
					return true;

		        indexes[i]++;
		        i = 0;
		    }
		    else {
		        indexes[i] = 0;
		        i++;
		    }
		}

        return false;
    }

    private void swapValues(int a, int b) {
		UBlockPos pos = positions.get(a);
	    positions.set(a, positions.get(b));
	    positions.set(b, pos);
	}

	private boolean matchesConstProperties(UBlockPos shiftPos, Blueprint.Structure structure, ItemUseContext itemContext){
		for(int k = 0; k < containers.size(); k++){
			if(verifyProperties(containers.get(k), positions.get(k), shiftPos, structure, itemContext))
				return false;
		}
		return true;
	}

	@Nonnull
	@Override
	public IValidatorSerializer getSerializer() {
		// TODO: MAKE
		return super.getSerializer();
	}

	@Override
    public @Nonnull List<UBlockPos> matchesShiftPos(Structure structure, ItemUseContext itemContext) {
		if(containers.stream().anyMatch(container -> container.verify(itemContext.getClickedPos(), structure, itemContext)))
        	return positions;
		else 
			return Collections.emptyList();
    }

	@Nonnull
	@Override
	public IValidator clone(NonNullList<UBlockPos> newPositions) {
		return new ValidatorPermutationConst(Lists.newArrayList(positions), Lists.newArrayList(containers));
	}

	@Nonnull
	@Override
	public IValidator changeOfAxis(NonNullList<UBlockPos> newPositions) {
		return new ValidatorPermutationConst(
				newPositions,
				this.containers.stream().map(IVerifiable::changeOfAxis).collect(Collectors.toList())
		);
	}
}
