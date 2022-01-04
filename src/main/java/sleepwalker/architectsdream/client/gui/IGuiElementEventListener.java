package sleepwalker.architectsdream.client.gui;

import net.minecraft.client.gui.IGuiEventListener;
import sleepwalker.architectsdream.client.gui.IGuiElement;

public interface IGuiElementEventListener extends IGuiEventListener, IGuiElement {

    @Override
    default boolean isMouseOver(double mouseX, double mouseY){
        return mouseX >= getPosX() && getPosEndX() >= mouseX && mouseY >= getPosY() && getPosEndY() >= mouseY;
    }
}
