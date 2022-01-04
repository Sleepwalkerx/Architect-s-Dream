package sleepwalker.architectsdream.client.gui.blueprint_viewer.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import sleepwalker.architectsdream.client.ISavable;
import sleepwalker.architectsdream.client.gui.BaseGuiElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.IWindow;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoGroup;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.IScrollElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.ScrollRect;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class WindowInfoPanel extends BaseGuiElement implements IWindow, INestedGuiEventHandler, ISavable {

    private final ScrollRect ingredients;
    private final ScrollRect entityInfo;
    private final ScrollRect resultPanel;

    private final FontRenderer font = Minecraft.getInstance().font;

    private IInfoElement activeElement;

    private final ScreenBlueprintViewer viewer;

    public WindowInfoPanel(){

        ingredients = new ScrollRect(6, 23, 4.3, "ingredients");
        entityInfo = new ScrollRect(6, 23, 4.0, "object_information");
        resultPanel = new ScrollRect(6, 23, 4.0, "result_creation");

        viewer = ScreenBlueprintViewer.activeViewer();
    }

    private final List<IGuiEventListener> children = new ArrayList<>();

    private final List<IInfoGroup> groups = new ArrayList<>();

    private final List<IScrollElement> results = new ArrayList<>();

    public void addInfoGroup(IInfoGroup group){
        groups.add(group);
    }

    public void addResultElement(IScrollElement element){
        results.add(element);
    }

    public void showInfoElement(IInfoElement element){

    }

    @Override
    public void setHighlight(boolean value) {

    }

    @Override
    public void init() {

        ingredients.clear();
        resultPanel.clear();
        children.clear();

        groups.forEach(group -> {

            //ingredients.getElements().add(group);

            ingredients.getElements().addAll(group.getElements());
        });

        results.forEach(element -> {
            resultPanel.getElements().add(element);
        });

        ScreenBlueprintViewer viewer = ScreenBlueprintViewer.activeViewer();

        children.add(ingredients);
        ingredients.initGuiElement(viewer.getGuiLeft() + 316, viewer.getGuiTop() + 25, 96, 82);

        children.add(entityInfo);
        entityInfo.initGuiElement(viewer.getGuiLeft() + 13, viewer.getGuiTop() + 25, 96, 82);

        children.add(resultPanel);
        resultPanel.initGuiElement(viewer.getGuiLeft() + 13, viewer.getGuiTop() + 143, 80, 82);

        ingredients.init(1);
        entityInfo.init(1);
        resultPanel.init(1);

        viewer.addSavableObject(this);
    }

    @Override
    public void renderLabel(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {

        drawStringScroll(matrixStack, ingredients);

        drawStringScroll(matrixStack, entityInfo);

        drawStringScroll(matrixStack, resultPanel);
    }

    private void drawStringScroll(@Nonnull MatrixStack matrixStack, @Nonnull ScrollRect rect){
        drawStringCenter(matrixStack, rect.getPosX() - viewer.getGuiLeft() + (rect.getWidth() / 2f), rect.getPosY() - viewer.getGuiTop() - font.lineHeight - 2, rect);
    }

    private void drawStringCenter(@Nonnull MatrixStack matrixStack, float x, float y, @Nonnull ScrollRect rect){
        rendFont(matrixStack,x - font.width(rect.getDisplayName()) / 2f, y, rect);
    }

    private void rendFont(MatrixStack stack, float x, float y, @Nonnull ScrollRect rect){

        if(rect.getDisplayNameSize() != 1f){

            stack.pushPose();
            stack.translate((x + (font.width(rect.getDisplayName()) / 2f)) * (1f - rect.getDisplayNameSize()), (y + (font.lineHeight / 2f))  * (1f - rect.getDisplayNameSize()), 0);
            stack.scale(rect.getDisplayNameSize(), rect.getDisplayNameSize(), 1);
            font.draw(stack, rect.getDisplayName(), x, y, 0x666666);
            stack.popPose();
        }
        else {

            font.draw(stack, rect.getDisplayName(), x, y, 0x666666);
        }
    }

    @Nonnull
    @Override
    public List<? extends IGuiEventListener> children() {
        return children;
    }

    private IGuiEventListener focused;
    @Nullable
    @Override
    public IGuiEventListener getFocused() {
        return focused;
    }

    public ScrollRect getEntityInfo() {
        return entityInfo;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        focused = null;
        return INestedGuiEventHandler.super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    public void setActiveElement(IInfoElement activeElement) {

        if(this.activeElement != null){
            this.activeElement.setActive(false);
        }

        activeElement.setActive(true);

        this.activeElement = activeElement;
    }

    public boolean isActiveElement(IInfoElement element){
        return activeElement == element;
    }

    @Override
    public void setDragging(boolean pDragging) { }

    @Override
    public boolean isDragging() {
        return true;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener pListener) {
        focused = pListener;
    }

    @Override
    public void render(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {

        ingredients.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        entityInfo.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        resultPanel.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
    }

    @Nonnull
    @Override
    public INBT saveData() {
        return IntNBT.valueOf(activeElement != null ? ingredients.getElements().indexOf(activeElement) : -1);
    }

    @Override
    public void readData(@Nullable INBT nbt) {

        if(nbt == null){
            return;
        }

        int index = ((IntNBT)nbt).getAsInt();

        if(index == -1 || index >= ingredients.getElements().size()){
            return;
        }

        activeElement = ingredients.getElements().get(index);

        activeElement.setActive(true);
    }
}
