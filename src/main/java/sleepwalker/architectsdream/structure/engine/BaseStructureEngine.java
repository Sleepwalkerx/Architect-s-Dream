package sleepwalker.architectsdream.structure.engine;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockPos;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.PlacementData;

import javax.annotation.Nullable;

public abstract class BaseStructureEngine {

    public static final BaseStructureEngine DEFAULT_ENGINE = new BaseStructureEngine() {
        @Override
        public boolean formed(BlockPos shiftPos, Blueprint blueprint, PlacementData dataIn, ItemUseContext itemContext) {
            return true;
        }

        @Override
        public IEngineSerializer<? extends BaseStructureEngine> getSerialize() {
            return SerializerManager.ENGINE_DEFAULT;
        }
    };

    @Nullable
    public ItemStack getIcon(){
        return null;
    }

    public abstract boolean formed(BlockPos shiftPos, Blueprint blueprint, PlacementData dataIn, ItemUseContext itemContext);

    public abstract IEngineSerializer<? extends BaseStructureEngine> getSerialize();
}
