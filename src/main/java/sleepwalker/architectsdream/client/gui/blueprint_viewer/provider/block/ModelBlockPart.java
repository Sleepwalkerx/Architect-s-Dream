package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.BaseModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowInfoPanel;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;


public class ModelBlockPart extends BaseModel<ContainerTypeBlock> {

    protected ModelBlockPart(IValidator validator, UBlockPos pos, ContainerTypeBlock entity) {
        super(validator, pos, entity);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) { }

    @Override
    public IInfoElement getInfoElement() {
        return null;
    }

    @Override
    public void showOnInfoPanel(WindowInfoPanel panel) {

    }
}
