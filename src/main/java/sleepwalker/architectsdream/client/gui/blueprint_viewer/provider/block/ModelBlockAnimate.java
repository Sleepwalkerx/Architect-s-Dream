package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

public class ModelBlockAnimate extends ModelBlock {

    private final float angel;
    protected ModelBlockAnimate(IValidator validator, UBlockPos pos, ContainerTypeBlock entity) {
        super(validator, pos, entity);

        if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){

            switch(state.getValue(BlockStateProperties.HORIZONTAL_FACING)){
                case EAST:
                    angel = 90;
                    break;

                case NORTH:
                    angel = 180;
                    break;

                case WEST:
                    angel = 270;
                    break;

                case DOWN:
                case UP:
                case SOUTH:
                default:
                    angel = 0;
                    break;
            }
        }
        else if(state.hasProperty(BlockStateProperties.ROTATION_16)){

            angel = (22.5f * state.getValue(BlockStateProperties.ROTATION_16)) % 360;
        }
        else {
            angel = 0;
        }
    }


    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float particle) {

        matrixStack.pushPose();

        matrixStack.translate(pos.getX(), pos.getY(), pos.getZ());

        matrixStack.translate(0.5f, 0, 0.5f);
        matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), angel, true));
        matrixStack.translate(-0.5f, 0, -0.5f);

        IRenderTypeBuffer.Impl rend = mc.renderBuffers().bufferSource();

        getItemStack().getItem().getItemStackTileEntityRenderer().renderByItem(
            getItemStack(),
            ItemCameraTransforms.TransformType.NONE,
            matrixStack,
            rend,
            15728880,
            OverlayTexture.NO_OVERLAY
        );

        rend.endBatch();

        matrixStack.popPose();
    }
}
