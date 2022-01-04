package sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;

public interface IScrollElement extends IInfoElement {

    default void renderCover(boolean active, MatrixStack pMatrixStack){

        Minecraft.getInstance().getTextureManager().bind(ScrollRect.TEXTURE);

        if(active){
            AbstractGui.blit(pMatrixStack, getPosX(), getPosY(), 82, 0, 82, getHeight() - 3, 256, 256);
            AbstractGui.blit(pMatrixStack, getPosX(), getPosY() + getHeight() - 3, 82, 78, 82, 3, 256, 256);
        }
        else {
            AbstractGui.blit(pMatrixStack, getPosX(), getPosY(), 0, 0, 82, getHeight() - 3, 256, 256);
            AbstractGui.blit(pMatrixStack, getPosX(), getPosY() + getHeight() - 3, 0, 78, 82, 3, 256, 256);
        }
    }

    void setScrollRectY(int value);

    void setScrollRectEndY(int value);

    int getScrollRectEndY();

    int getScrollRectY();

    @Override
    default boolean isMouseOver(double mouseX, double mouseY) {
        return IInfoElement.super.isMouseOver(mouseX, mouseY) && mouseY >= getScrollRectY() && mouseY <= getScrollRectEndY();
    }
}
