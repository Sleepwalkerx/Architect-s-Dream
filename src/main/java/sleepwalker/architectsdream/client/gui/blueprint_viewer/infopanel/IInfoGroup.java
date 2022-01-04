package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel;

import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.IScrollElement;

import java.util.List;

public interface IInfoGroup extends IInfoElement {

    List<? extends IScrollElement> getElements();

    void init();
}
