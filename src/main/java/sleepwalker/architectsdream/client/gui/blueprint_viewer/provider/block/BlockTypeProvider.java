package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block;

import net.minecraft.block.*;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.group.BlockInfoGroup;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.EmptyModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.ITypeProvider;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class BlockTypeProvider implements ITypeProvider<ContainerTypeBlock, BlockInfoGroup> {

    public static final BlockTypeProvider PROVIDER = new BlockTypeProvider();

    @Nonnull
    @Override
    public Class<ContainerTypeBlock> getTypeClass() {
        return ContainerTypeBlock.class;
    }


    @Nullable
    @Override
    public BlockInfoGroup createTypeGroup() {
        return new BlockInfoGroup("blocks");
    }

    @Nonnull
    @Override
    public IModel createModel(ContainerTypeBlock entity, UBlockPos pos, IValidator validator, BlockInfoGroup group) {

        ModelBlock block;
        BlockState blockstate;

        if(entity.getBlockState() != null) {

            blockstate = entity.getBlockState();

        } else {

            if(entity.getTags() != null){

                blockstate = entity.getTags().getTags().values().iterator().next().getValues().get(0).defaultBlockState();
            }
            else blockstate = Blocks.AIR.defaultBlockState();
        }

        if(blockstate.getRenderShape() == BlockRenderType.INVISIBLE && blockstate.getBlock() instanceof ContainerBlock){

            block = new ModelBlockAnimate(validator, pos, entity);
        }
        else if(blockstate.getRenderShape() == BlockRenderType.ENTITYBLOCK_ANIMATED && blockstate.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){

            if(blockstate.getBlock().getClass().isAssignableFrom(BedBlock.class) && blockstate.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT){
                return EmptyModel.EMPTY;
            }

            block = new ModelBlockAnimate(validator, pos, entity);
        }
        else {

            block = new ModelBlock(validator, pos, entity);
        }

        block.setInfoElement(group.addElement(block));

        return block;
    }
}
