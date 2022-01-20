package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider;

import net.minecraft.client.gui.IRenderable;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowInfoPanel;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

public interface IModel extends IRenderable {

    @Nonnull
    UBlockPos getPos();

    boolean isVisible();

    void setVisible(boolean value);

    void showOnInfoPanel(WindowInfoPanel panel);
}
