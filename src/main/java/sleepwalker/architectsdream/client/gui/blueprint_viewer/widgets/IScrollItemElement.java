package sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IScrollItemElement extends IScrollElement {

    @Nonnull
    ItemStack getItemStack();

    default int getItemPosX(){
        return getPosX() + 3;
    }

    default int getItemPosY(){
        return getPosY() + 3;
    }

    default boolean isOverItem(int mouseX, int mouseY){
        return mouseX >= getItemPosX() && mouseX <= getItemPosX() + 16 && getItemPosY() <= mouseY && mouseY <= getItemPosY() + 16;
    }
}
