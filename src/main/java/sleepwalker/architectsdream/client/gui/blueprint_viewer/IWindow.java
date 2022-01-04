package sleepwalker.architectsdream.client.gui.blueprint_viewer;

import com.mojang.blaze3d.matrix.MatrixStack;
import sleepwalker.architectsdream.client.gui.IGuiElementEventListener;

import javax.annotation.Nonnull;

public interface IWindow extends IGuiElementEventListener {

    void setHighlight(boolean value);

    void init();

    void renderLabel(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY);
}
