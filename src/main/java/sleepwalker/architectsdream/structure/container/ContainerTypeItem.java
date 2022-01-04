package sleepwalker.architectsdream.structure.container;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import sleepwalker.architectsdream.serialize.type.IPaletteTypeSerializer;
import sleepwalker.architectsdream.structure.Blueprint.Structure;
import sleepwalker.architectsdream.structure.propertys.IComparator;
import sleepwalker.architectsdream.structure.propertys.NumericOperation;

import java.util.Objects;

public class ContainerTypeItem extends BaseContainerType<IComparator<ItemEntity, ContainerTypeItem>, ContainerTypeItem.Properties> {

    public static final IComparator<ItemEntity, ContainerTypeItem> ITEM =
        (itemEntity, properties, context) ->
        itemEntity.getItem().getItem().getItem().getRegistryName().equals(properties.getItem())
    ;

    public static final IComparator<ItemEntity, ContainerTypeItem> COUNT =
        (itemEntity, properties, context) ->
        properties.getCount().equals(itemEntity.getItem().getCount())
    ;

    public ContainerTypeItem(NonNullList<IComparator<ItemEntity, ContainerTypeItem>> comparators, Properties properties) {
        super(comparators, properties);
    }

    public Item getItem(){
        return properties.item;
    }

    public NumericOperation getCount(){
        return properties.count;
    }

    @Override
    public boolean verify(BlockPos posInWorld, Structure structure, ItemUseContext itemContext) {
        return itemContext.getLevel().getEntitiesOfClass(
            ItemEntity.class, 
            new AxisAlignedBB(
                -structure.getSize().getX(),
                -structure.getSize().getY(),
                -structure.getSize().getZ(),
                structure.getSize().getX(),
                structure.getSize().getY(),
                structure.getSize().getZ()
            ), 
            entityItem -> comparators.stream().allMatch(
                comparator -> entityItem.position().equals(posInWorld) 
                && 
                comparator.matches(entityItem, this, itemContext)
            )
        ).isEmpty();
    }

    @Override
    public IPaletteTypeSerializer<? extends IVerifiable> getSerializer() {
        return null;
    }

    public static class Properties {
        public Item item;
        public NumericOperation count;

        public Properties setItem(Item item){
            this.item = item;
            return this;
        }

        public Properties setCount(NumericOperation count) {
            this.count = count;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Properties that = (Properties) o;
            return Objects.equals(item, that.item) && Objects.equals(count, that.count);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, count);
        }
    }
}
