package sleepwalker.architectsdream.structure.container;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.type.IPaletteTypeSerializer;
import sleepwalker.architectsdream.structure.Blueprint.Structure;
import sleepwalker.architectsdream.structure.propertys.IComparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ContainerTypeBlock extends BaseContainerType<ContainerTypeBlock.BlockComparator, ContainerTypeBlock.Properties> {

    public static final BlockComparator BLOCK_STATE = (blockState, container, context) -> {

        if(container.getTags() != null || blockState.getBlock() == container.getBlock()){
            return !container.isHasProperties() || container.getBlockState().getValues().equals(blockState.getValues());
        }
        else return false;
    };

    public static final BlockComparator TAG = (blockState, properties, context) -> properties.getTags().is(blockState);

    @Override
    public IVerifiable changeOfAxis() {

        if(getBlockState().hasProperty(BlockStateProperties.AXIS)){

            Direction.Axis axis = getBlockState().getValue(BlockStateProperties.AXIS);

            if(axis != Direction.Axis.Y){
                return withNewState(BlockStateProperties.AXIS, axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
            }
        }

        if(getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_AXIS)){

            Direction.Axis axis = getBlockState().getValue(BlockStateProperties.HORIZONTAL_AXIS);

            return withNewState(BlockStateProperties.HORIZONTAL_AXIS, axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        }

        if(getBlockState().hasProperty(BlockStateProperties.ROTATION_16)){

            int value = getBlockState().getValue(BlockStateProperties.ROTATION_16);

            return withNewState(BlockStateProperties.ROTATION_16, (value + 4 % 16));
        }

        return super.changeOfAxis();
    }

    @Nonnull
    private <T extends Comparable<T>, V extends T> ContainerTypeBlock withNewState(Property<T> pProperty, V pValue){

        Properties newProp = new Properties(properties);

        newProp.setBlockState(newProp.blockState.setValue(pProperty, pValue), newProp.hasProperties);

        return new ContainerTypeBlock(comparators, newProp);
    }

    @Override
    public String toString() {
        if(getBlockState().hasProperty(BlockStateProperties.AXIS)){
            return getBlockState().getValue(BlockStateProperties.AXIS).toString();
        }
       else return getBlockState().getBlock().getName().getString();
    }

    public ContainerTypeBlock(NonNullList<BlockComparator> comparators, Properties properties) {
        super(comparators, properties);
    }

    @Override
    public boolean verify(BlockPos posInWorld, Structure structure, ItemUseContext itemContext) {

        return comparators.stream().allMatch(comparator ->
            comparator.matches(
                itemContext.getLevel().getBlockState(posInWorld), 
                this,
                itemContext
            )
        );
    }

    @Nonnull
    public Block getBlock() {
        return properties.blockState.getBlock();
    }

    @Nullable
    public BlockState getBlockState() {
        return properties.blockState;
    }

    @Nullable
    public BlockTagCollection getTags() {
        return properties.tags;
    }

    public boolean isHasProperties() {
        return properties.hasProperties;
    }

    @Override
    public IPaletteTypeSerializer<? extends IVerifiable> getSerializer() {
        return SerializerManager.TYPE_BLOCK;
    }

    public interface BlockComparator extends IComparator<BlockState, ContainerTypeBlock>{ }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof ContainerTypeBlock){

            ContainerTypeBlock container = (ContainerTypeBlock) obj;

            return Objects.equals(container.comparators, comparators) && Objects.equals(container.properties, properties);
        }

        return false;
    }

    public static class Properties {

        private BlockState blockState;
        private boolean hasProperties;

        private BlockTagCollection tags;

        public Properties(){}

        public Properties(@Nonnull Properties original){
            this.tags = original.tags;
            this.hasProperties = original.hasProperties;
            this.blockState = original.blockState;
        }

        public void setTags(BlockTagCollection tags){
            this.tags = tags;
        }

        public void setBlockState(BlockState blockState, boolean hasProperties) {
            this.blockState = blockState;
            this.hasProperties = hasProperties;
        }

        @Override
        public boolean equals(Object obj) {

            if(obj instanceof Properties){

                Properties properties = (Properties)obj;

                if(!blockState.getBlock().getRegistryName().equals(properties.blockState.getBlock().getRegistryName())){
                    return false;
                }
                if(hasProperties != properties.hasProperties){
                    return false;
                }
                if(hasProperties && !blockState.getValues().equals(properties.blockState.getValues())){
                    return false;
                }
                else return tags == properties.tags;
            }
            else return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(blockState.getBlock(), tags);
        }
    }

    public static class BlockTagCollection {

        @Nonnull
        private final Map<ResourceLocation,  ITag<Block>> tags;

        public BlockTagCollection(@Nonnull Map<ResourceLocation,  ITag<Block>> tags) {
            this.tags = tags;
        }

        @Nonnull
        public Map<ResourceLocation,  ITag<Block>> getTags() {
            return tags;
        }

        public boolean is(@Nonnull BlockState state){
            return tags.values().stream().anyMatch(state::is);
        }
    }
}
