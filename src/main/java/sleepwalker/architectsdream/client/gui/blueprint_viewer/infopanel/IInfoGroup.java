package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.IScrollElement;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IInfoGroup extends IInfoElement {

    List<? extends IScrollElement> getElements();

    void build();
}
