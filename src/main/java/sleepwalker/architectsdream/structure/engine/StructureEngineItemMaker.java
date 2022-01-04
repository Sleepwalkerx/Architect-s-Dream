package sleepwalker.architectsdream.structure.engine;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.PlacementData;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

public class StructureEngineItemMaker extends BaseStructureEngine {

    @Nonnull
    protected final NonNullList<ItemStack> outputItems;

    public StructureEngineItemMaker(@Nonnull NonNullList<ItemStack> outputItems){
        this.outputItems = outputItems;
    }

    @Override
    public boolean formed(BlockPos shiftPos, Blueprint blueprint, PlacementData dataIn, ItemUseContext itemContext) {
        if(itemContext.getPlayer() == null) return false;

        return dataIn.getValidators().entrySet().stream().allMatch(entry -> {
            if (entry.getKey().isAssignableFrom(ContainerTypeBlock.class)) {
                for (IValidator validator : entry.getValue()) {
                    for (BlockPos posIn : validator.getPositions())
                        if (!itemContext.getLevel().destroyBlock(itemContext.getClickedPos().subtract(shiftPos).offset(posIn), false))
                            return false;
                }
                return true;
            }
            return false;
        }) && outputItems.stream().allMatch(itemStack ->
            itemContext.getLevel().addFreshEntity(
                new ItemEntity(
                    itemContext.getLevel(),
                    itemContext.getClickedPos().getX(),
                    itemContext.getClickedPos().getY(),
                    itemContext.getClickedPos().getZ(),
                    itemStack
                )
            )
        );
    }

    @Nonnull
    public NonNullList<ItemStack> getOutputItems() {
        return outputItems;
    }

    @Nullable
    @Override
    public ItemStack getIcon() {
        return outputItems.get(0);
    }

    @Override
    public IEngineSerializer<? extends BaseStructureEngine> getSerialize() {
        return SerializerManager.ENGINE_ITEM_MAKER;
    }
}
