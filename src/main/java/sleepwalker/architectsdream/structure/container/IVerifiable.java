package sleepwalker.architectsdream.structure.container;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockPos;
import sleepwalker.architectsdream.serialize.type.IPaletteTypeSerializer;
import sleepwalker.architectsdream.structure.Blueprint.Structure;

public interface IVerifiable {

    boolean verify(BlockPos posInWorld, Structure structure, ItemUseContext itemContext);

    IPaletteTypeSerializer<? extends IVerifiable> getSerializer();

    IVerifiable changeOfAxis();
}
