package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel;

import sleepwalker.architectsdream.client.gui.IGuiElementEventListener;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowInfoPanel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowStructureViewer;

public interface IInfoElement extends IGuiElementEventListener {

    default void showInStructureViewer(WindowStructureViewer viewer){}

    boolean canShowInStructureViewer();

    default void showInInfoPanel(WindowInfoPanel panel){
        panel.showInfoElement(this);
    }

    boolean canShowInInfoPanel();

    default void setActive(boolean value){}
}
