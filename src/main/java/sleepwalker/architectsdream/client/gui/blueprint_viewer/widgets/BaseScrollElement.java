package sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets;

import sleepwalker.architectsdream.client.gui.BaseGuiElement;

public abstract class BaseScrollElement extends BaseGuiElement implements IScrollElement {

    protected int scrollRectEndY, scrollRectY;

    protected BaseScrollElement(){

        height = 24;
        width = 82;
    }

    @Override
    public void setScrollRectY(int value) {
        scrollRectY = value;
    }

    @Override
    public void setScrollRectEndY(int value) {
        scrollRectEndY = value;
    }

    @Override
    public int getScrollRectEndY() {
        return scrollRectEndY;
    }

    @Override
    public int getScrollRectY() {
        return scrollRectY;
    }
}
