package sleepwalker.architectsdream.structure.engine;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.PlacementData;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class StructureEngineItemMaker extends BaseStructureEngine {

    @Nonnull
    protected final NonNullList<ItemStack> outputItems;

    public StructureEngineItemMaker(@Nonnull NonNullList<ItemStack> outputItems){
        this.outputItems = outputItems;
    }

    @Override
    public boolean formed(BlockPos shiftPos, Blueprint blueprint, PlacementData dataIn, ItemUseContext itemContext) {

        if(itemContext.getPlayer() == null) {
            return false;
        }

        for(Map.Entry<ResourceLocation, Set<IValidator>> entry : dataIn.getValidators().entrySet()){

            if(entry.getKey().equals(R.BlockContainer.NAME)){

                for(IValidator validator : entry.getValue()){

                    for(BlockPos posIn : validator.getPositions()){

                        if(!itemContext.getLevel().destroyBlock(itemContext.getClickedPos().subtract(shiftPos).offset(posIn), false)){

                            return false;
                        }
                    }
                }
            }

            ArchitectsDream.LOGGER.warn("Unsupported type spawn");

            return false;
        }

        for(ItemStack stack : outputItems){

            ItemStack copy = stack.copy();

            if(itemContext.getPlayer().addItem(copy)){
                return true;
            }

            ItemEntity item = new ItemEntity(
                itemContext.getLevel(),
                itemContext.getClickedPos().getX(),
                itemContext.getClickedPos().getY(),
                itemContext.getClickedPos().getZ(),
                copy
            );

            if(!itemContext.getLevel().addFreshEntity(item)){
                return false;
            }
        }

        return true;
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
