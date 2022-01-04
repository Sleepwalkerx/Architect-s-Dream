package sleepwalker.architectsdream.client.gui;

import net.minecraft.client.gui.IRenderable;

public interface IGuiElement extends IRenderable {

    default void initGuiElement(int posX, int posY, int height, int width){

        setPosX(posX);
        setPosY(posY);
        setHeight(height);
        setWidth(width);

        setPosEndX(posX + width);
        setPosEndY(posY + height);
    }

    int getHeight();

    int getWidth();

    int getPosX();

    void setPosEndX(int posEndX);

    void setPosEndY(int posEndY);

    int getPosY();

    int getPosEndY();

    int getPosEndX();

    void setHeight(int height);

    void setWidth(int width);

    void setPosX(int posX);

    void setPosY(int posY);

    default boolean withinGuiElement(double mouseX, double mouseY){
        return mouseX >= getPosX() && getPosEndX() >= mouseX && mouseY >= getPosY() && getPosEndY() >= mouseY;
    }
}
