package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block;

import net.minecraft.block.*;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoGroup;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.group.BlockInfoGroup;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.EmptyModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModelProvider;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.container.IVerifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BlockModelProvider implements IModelProvider {

    public static final BlockModelProvider PROVIDER = new BlockModelProvider();

    @Override
    public ResourceLocation getTypeName() {
        return R.BlockContainer.NAME;
    }

    @Nonnull
    @Override
    public IModel createModel(@Nonnull IVerifiable entity, UBlockPos pos) {

        ModelBlock block;
        BlockState blockstate;

        ContainerTypeBlock typeBlock = (ContainerTypeBlock) entity;

        if(typeBlock.getBlockState() != null) {

            blockstate = typeBlock.getBlockState();

        } else {

            if(typeBlock.getTags() != null){

                blockstate = typeBlock.getTags().getTags().values().iterator().next().getValues().get(0).defaultBlockState();
            }
            else blockstate = Blocks.AIR.defaultBlockState();
        }

        if(blockstate.getRenderShape() == BlockRenderType.INVISIBLE && blockstate.getBlock() instanceof ContainerBlock){

            block = new ModelBlockAnimate(pos, typeBlock);
        }
        else if(blockstate.getRenderShape() == BlockRenderType.ENTITYBLOCK_ANIMATED && blockstate.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){

            if(blockstate.getBlock().getClass().isAssignableFrom(BedBlock.class) && blockstate.getValue(BlockStateProperties.BED_PART) == BedPart.FOOT){
                return EmptyModel.EMPTY;
            }

            block = new ModelBlockAnimate(pos, typeBlock);
        }
        else {

            block = new ModelBlock(pos, typeBlock);
        }

        return block;
    }

    @Nullable
    @Override
    public IInfoGroup createGroup(@Nonnull List<IModel> models) {

        BlockInfoGroup blockInfoGroup = new BlockInfoGroup(getTypeName().getPath());

        models.forEach(iModel -> {

            if(iModel instanceof ModelBlock){

                blockInfoGroup.addElement((ModelBlock) iModel);
            }
        });

        return blockInfoGroup;
    }
}
