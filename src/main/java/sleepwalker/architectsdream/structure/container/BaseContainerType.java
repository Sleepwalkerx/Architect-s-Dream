package sleepwalker.architectsdream.structure.container;

import net.minecraft.util.NonNullList;

import sleepwalker.architectsdream.structure.propertys.IComparator;

public abstract class BaseContainerType<C, P> implements IVerifiable {

    protected NonNullList<C> comparators;
    protected final P properties;

    protected BaseContainerType(NonNullList<C> comparators, P properties){
        this.comparators = comparators;
        this.properties = properties;
    }

    @Override
    public IVerifiable changeOfAxis() {
        return this;
    }
}
