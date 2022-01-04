package sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import sleepwalker.architectsdream.client.ISavable;
import sleepwalker.architectsdream.client.gui.BaseGuiElement;
import sleepwalker.architectsdream.client.gui.IGuiElement;
import sleepwalker.architectsdream.client.gui.IGuiElementEventListener;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;

import javax.annotation.Nonnull;

public class ScrollBar extends BaseGuiElement implements ISavable, IGuiElementEventListener {

    private float scrollNormalizeValue;

    private int scrollBarMaxY, scrollBarMinY;

    private double doublePosY;

    private final double scrollBarStep;

    private final ScrollRect scrollRect;

    private boolean locked;

    public ScrollBar(ScrollRect scrollRect, int width, int height, double scrollBarStep){
        this.width = width;
        this.height = height;
        this.scrollRect = scrollRect;
        this.scrollBarStep = scrollBarStep;
    }

    public void init(){

        super.setPosY(scrollRect.getPosY());

        scrollBarMinY = scrollRect.getPosY();
        scrollBarMaxY = scrollRect.getPosEndY() - height;

        ScreenBlueprintViewer.activeViewer().addSavableObject(this);
    }

    public void resetAndLocked(){

        reset();

        locked = true;
    }

    public void reset(){
        posY = scrollBarMinY;
        doublePosY = scrollBarMinY;
        scrollNormalizeValue = 0;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        Minecraft.getInstance().getTextureManager().bind(ScrollRect.TEXTURE);

        AbstractGui.blit(matrixStack, posX, posY, 164, 0, 6,23, 256, 256);
    }

    @Override
    public void setPosX(int posX) {

        super.setPosX(posX);

        scrollRect.setPosEndX(posEndX);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public double getScrollBarStep() {
        return scrollBarStep;
    }

    public void setPosY(int posY) {

        doublePosY = posY;

        super.setPosY(MathHelper.clamp(posY, scrollBarMinY, scrollBarMaxY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

        scrollBarMove(deltaY);

        return true;
    }

    void recalculatePositions(){

        int relativeY = posY + getRelativeY();

        for(IGuiElement element : scrollRect.getElements()){

            element.setPosY(relativeY);

            relativeY += element.getHeight();
        }
    }

    public void scrollBarMove(double value){

        if(locked) return;

        doublePosY = MathHelper.clamp(doublePosY + value, scrollBarMinY, scrollBarMaxY);

        super.setPosY((int)doublePosY);

        scrollNormalizeValue = ((float)doublePosY - scrollBarMinY) / (scrollBarMaxY - scrollBarMinY);

        recalculatePositions();
    }

    private int getRelativeY(){
        return (int) (-scrollNormalizeValue * scrollRect.getScrollPanelHeight());
    }

    @Nonnull
    @Override
    public INBT saveData() {
        return FloatNBT.valueOf(scrollNormalizeValue);
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {

        ResourceLocation location = ISavable.super.getRegistryName();

        return new ResourceLocation(location.getNamespace(), location.getPath() + "_" + scrollRect.name);
    }

    @Override
    public void readData(@Nullable INBT nbt) {

        if(nbt != null){
            scrollNormalizeValue = ((FloatNBT)nbt).getAsFloat();
        }
        else scrollNormalizeValue = 0f;

        doublePosY = scrollNormalizeValue * (scrollBarMaxY - scrollBarMinY) + scrollBarMinY;
        super.setPosY((int)doublePosY);

        recalculatePositions();
    }
}
