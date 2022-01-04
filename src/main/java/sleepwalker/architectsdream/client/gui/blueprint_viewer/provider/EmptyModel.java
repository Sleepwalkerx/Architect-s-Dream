package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider;

import com.mojang.blaze3d.matrix.MatrixStack;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowInfoPanel;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

public class EmptyModel implements IModel {

    public static EmptyModel EMPTY = new EmptyModel();

    @Nonnull
    @Override
    public UBlockPos getPos() {
        return UBlockPos.ZERO;
    }

    @Override
    public IValidator getValidator() {
        return null;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setVisible(boolean value) { }

    @Override
    public IInfoElement getInfoElement() {
        return null;
    }

    @Override
    public void showOnInfoPanel(WindowInfoPanel panel) {

    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) { }
}
