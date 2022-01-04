package sleepwalker.architectsdream.structure.propertys;

import net.minecraft.item.ItemUseContext;
import sleepwalker.architectsdream.structure.container.IVerifiable;

@FunctionalInterface
public interface IComparator<E, C extends IVerifiable> {
    boolean matches(E entityIn, C container, ItemUseContext context);
}
