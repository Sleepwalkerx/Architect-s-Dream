package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.BaseModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowInfoPanel;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

public class ModelBlock extends BaseModel<ContainerTypeBlock> {

    protected final BlockPos pos;
    protected final BlockState state;
    protected final ItemStack stackIn;
    protected final Minecraft mc;

    protected IInfoElement element;

    protected ModelBlock(IValidator validator, UBlockPos pos, ContainerTypeBlock entity) {
        super(validator, pos, entity);

        this.pos = pos;
        this.state = entity.getBlockState();
        this.mc = Minecraft.getInstance();
        stackIn = new ItemStack(state.getBlock());
    }

    public ItemStack getStackIn() {
        return stackIn;
    }

    public void setInfoElement(IInfoElement element){
        this.element = element;
    }

    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        matrixStack.translate(pos().getX(), pos().getY(), pos().getZ());

        IRenderTypeBuffer.Impl rend = mc.renderBuffers().bufferSource();

        mc.getBlockRenderer().renderSingleBlock(
            state,
            matrixStack,
            rend,
            15728880,
            OverlayTexture.NO_OVERLAY
        );
        rend.endBatch();

        matrixStack.translate(-pos().getX(), -pos().getY(), -pos().getZ());
    }

    public BlockPos pos() { return pos; }
    public ItemStack getItemStack() { return stackIn; }

    @Override
    public IInfoElement getInfoElement() {
        return element;
    }

    @Override
    public void showOnInfoPanel(WindowInfoPanel panel) {

    }
}
