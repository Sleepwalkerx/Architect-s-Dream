package sleepwalker.architectsdream.client.gui.blueprint_creator.widget;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.gui.ScrollPanel;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleScrollRect<T extends SimpleScrollRect.IElement> extends ScrollPanel {

    protected final List<T> elements;
    protected int elementHeight;

    protected Pair<Integer, Double> activeElement;

    public SimpleScrollRect(
        Minecraft client, List<T> elements, int elementHeight,
        int width, int height, int x, int y
    ){
        super(client, width, height, y, x);
        this.elements = elements;
        this.elementHeight = elementHeight;
    }

    @Nonnull
    public static <T extends IElement> SimpleScrollRect<T> of(Minecraft client, List<T> elements, int elementHeight, int width, int height, int x, int y){
        return new SimpleScrollRect<>(client, elements, elementHeight, width, height, x, y);
    }

    /*protected void correct(){
        int max = this.getContentHeight() - (this.height - this.border);
        if (max < 0) max /= 2;
        if (this.scrollDistance < 0.0F) this.scrollDistance = 0.0F;
        if (this.scrollDistance > max) this.scrollDistance = max;
    }*/

    public void swapElements(List<T> elements){
        this.elements.clear();
        this.elements.addAll(elements);
    }

    @Override
    public int getContentHeight(){
        return elements.size() * elementHeight;
    }

    @Override
    protected int getScrollAmount(){
        return elementHeight * 2;
    }

    @Override
    protected void drawPanel(MatrixStack matrixStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY){
        activeElement = findFocusElement(mouseX, mouseY);
        drawPanelSimple(matrixStack, entryRight, relativeY, tess, mouseX, mouseY);
    }

    public Pair<Integer, Double> getActiveElement() {
        return activeElement;
    }

    protected void drawPanelSimple(MatrixStack matrixStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY){
        if(activeElement == null){
            for(T element : elements){
                element.render(
                        matrixStack, entryRight, relativeY, left,
                        -1,  tess, mouseX, mouseY
                );
                relativeY += elementHeight;
            }
        }
        else {
            for(int i = 0; i < elements.size(); i++){
                elements.get(i).render(
                        matrixStack, entryRight, relativeY, left,
                        i == activeElement.getLeft() ? activeElement.getValue() : -1, tess, mouseX, mouseY
                );
                relativeY += elementHeight;
            }
        }
    }

    @Nullable
    protected Pair<Integer, Double> findFocusElement(int mouseX, int mouseY) {
        if(mouseX < left || mouseX > left + width) return null;

        double offset = (mouseY - top) + scrollDistance + elementHeight - 1;

        if (offset <= 0) {
            return null;
        }

        int index = (int) (offset / elementHeight) - 1;

        if(index >= 0 && index < elements.size()){
            return Pair.of(index, offset);
        }
        else return null;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(super.keyPressed(pKeyCode, pScanCode, pModifiers)){
            return true;
        }
        else return activeElement != null && elements.get(activeElement.getKey()).keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char word, int code) {
        return activeElement != null && elements.get(activeElement.getKey()).charTyped(word, code);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        if(super.keyReleased(pKeyCode, pScanCode, pModifiers)){
            return true;
        }
        else return activeElement != null && elements.get(activeElement.getKey()).keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if(super.mouseClicked(mouseX, mouseY, button)){
            return true;
        }
        else return activeElement != null && elements.get(activeElement.getKey()).mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(elements.size() != 0 && super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)){
            return true;
        }
        else return activeElement != null && elements.get(activeElement.getKey()).mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public interface IElement extends IGuiEventListener {
        void render(MatrixStack matrixStack, int entryRight, int relativeY, final int left, double offset, Tessellator tess, int mouseX, int mouseY);
    }
}