package sleepwalker.architectsdream.utils;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.structure.PlacementData;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

public final class ValidatorUtils {
    private ValidatorUtils() { throw new IllegalStateException("Utility class"); }

    @Nonnull
    public static NonNullList<PlacementData> calculateStructureData(PlacementData base, UVector3i size){
        NonNullList<PlacementData> lPlacementData = NonNullList.create();

        lPlacementData.add(base);

        lPlacementData.add(createStructureDataOfFunc(base, size, ValidatorUtils::reflectionX, ValidatorUtils::cloneValidator));

        PlacementData validatorZ = createStructureDataOfFunc(base, size, ValidatorUtils::reflectionZ, ValidatorUtils::cloneValidator);

        lPlacementData.add(validatorZ);
        lPlacementData.add(createStructureDataOfFunc(validatorZ, size, ValidatorUtils::reflectionX, ValidatorUtils::cloneValidator));

        base = createStructureDataOfFunc(validatorZ, size, ValidatorUtils::clockWise90, ValidatorUtils::changeAxisValidator);

        size = new UVector3i(size.getZ(), size.getY(), size.getX());

        lPlacementData.add(base);
        lPlacementData.add(createStructureDataOfFunc(base, size, ValidatorUtils::reflectionX, ValidatorUtils::cloneValidator));

        validatorZ = createStructureDataOfFunc(base, size, ValidatorUtils::reflectionZ, ValidatorUtils::cloneValidator);

        lPlacementData.add(validatorZ);
        lPlacementData.add(createStructureDataOfFunc(validatorZ, size, ValidatorUtils::reflectionX, ValidatorUtils::cloneValidator));

        if(lPlacementData.stream().allMatch(data -> data.getValidators().equals(lPlacementData.get(0).getValidators()))){
            return NonNullList.of(lPlacementData.get(0), lPlacementData.get(0));
        }

        return lPlacementData;
    }

    @Nonnull
    private static PlacementData createStructureDataOfFunc(
        @Nonnull final PlacementData basePlacementData,
        final UVector3i baseSize,
        BiFunction<UBlockPos, UVector3i, UBlockPos> function,
        BiFunction<IValidator, NonNullList<UBlockPos>, IValidator> cloneFunc
    ){
        Map<Class<? extends IVerifiable>, Set<IValidator>> map = Maps.newHashMap();

        basePlacementData.getValidators().forEach((classIn, validators) -> {
            Set<IValidator> set = Sets.newHashSet();

            validators.forEach(validator -> {
                NonNullList<UBlockPos> lReversePositions = NonNullList.create();
                validator.getPositions().forEach(pos -> {
                    lReversePositions.add(function.apply(pos, baseSize));
                });
                set.add(cloneFunc.apply(validator, lReversePositions));
            });

            map.put(classIn, set);
        });

        return new PlacementData(map);
    }

    @Nonnull
    private static IValidator cloneValidator(@Nonnull IValidator validator, NonNullList<UBlockPos> lReversePositions){
        return validator.clone(lReversePositions);
    }

    @Nonnull
    private static IValidator changeAxisValidator(@Nonnull IValidator validator, NonNullList<UBlockPos> lReversePositions){
        return validator.changeOfAxis(lReversePositions);
    }

    @Nonnull
    private static UBlockPos clockWise90(@Nonnull UBlockPos position, @Nonnull UVector3i size){
        return new UBlockPos(size.getZ() - position.getZ() - 1, position.getY(), position.getX());
    }

    @Nonnull
    private static UBlockPos reflectionX(@Nonnull UBlockPos position, @Nonnull UVector3i size){
        return new UBlockPos(size.getX() - position.getX() - 1, position.getY(), position.getZ());
    }

    @Nonnull
    private static UBlockPos reflectionZ(@Nonnull UBlockPos position, @Nonnull UVector3i size){
        return new UBlockPos(position.getX(), position.getY(), size.getZ() - position.getZ() - 1);
    }
}