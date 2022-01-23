package sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.BaseGuiElement;
import sleepwalker.architectsdream.client.gui.IGuiElement;
import sleepwalker.architectsdream.client.gui.IGuiElementEventListener;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.utils.RenderUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScrollRect extends BaseGuiElement implements INestedGuiEventHandler, IGuiElementEventListener {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ArchitectsDream.MODID, "textures/gui/scroll_rect.png");

    protected final Minecraft client;

    private int scrollPanelHeight;

    protected final List<IScrollElement> elements = new ArrayList<>();

    protected final ScrollBar scrollBar;

    protected final String name;

    protected final String displayName;

    protected final float displayNameSize;

    public ScrollRect(int scrollBarWidth, int scrollBarHeight, double scrollBarStep, String name) {

        this.client = Minecraft.getInstance();

        this.name = name;

        this.displayName = I18n.get(String.format("screen.%s.%s.scroll_%s.name", ArchitectsDream.MODID, R.ScreenName.SCREEN_BLUEPRINT_VIEWER, name));

        displayNameSize = displayName.length() > 15 ? (displayName.length() > 20 ? 0.7f : 0.8f) : 1f;

        scrollBar = new ScrollBar(this, scrollBarWidth, scrollBarHeight, scrollBarStep);
    }

    public void clear(){

        scrollBar.resetAndLocked();

        scrollPanelHeight = 0;

        elements.clear();
    }

    public float getDisplayNameSize() {
        return displayNameSize;
    }

    public void loadElements(){

        scrollPanelHeight = elements.stream().mapToInt(IGuiElement::getHeight).sum() - (height / 4) + 1;

        scrollBar.setLocked(scrollPanelHeight <= height);

        for(IScrollElement element : elements){

            element.setPosX(posX);

            element.setScrollRectY(posY);
            element.setScrollRectEndY(posEndY);
        }

        scrollBar.recalculatePositions();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void init(int indentBar){

        scrollBar.setPosX(posEndX + indentBar);

        scrollBar.init();

        if(elements.isEmpty()){

            scrollBar.setLocked(true);

            return;
        }

        loadElements();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double value) {

        if(INestedGuiEventHandler.super.mouseScrolled(mouseX, mouseY, value)){
            return true;
        }

        scrollBar.scrollBarMove((int)value * -scrollBar.getScrollBarStep());

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || INestedGuiEventHandler.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(!isMouseOver(mouseX,mouseY)){
            return false;
        }

        if(scrollBar.mouseClicked(mouseX, mouseY, button) || INestedGuiEventHandler.super.mouseClicked(mouseX, mouseY, button)){
            return true;
        }

        if(mouseX > scrollBar.getPosX()){

            scrollBar.scrollBarMove(mouseY - scrollBar.getPosY() - (scrollBar.getHeight() / 2.0));

            return true;
        }
        else return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        focused = null;
        return scrollBar.mouseReleased(mouseX, mouseY, button) || INestedGuiEventHandler.super.mouseReleased(mouseX, mouseY, button);
    }

    @Nonnull
    @Override
    public List<? extends IGuiEventListener> children() {
        return elements;
    }

    @Override
    public boolean isDragging() {
        return true;
    }

    @Override
    public void setDragging(boolean pDragging) { }

    private IGuiEventListener focused;

    @Nullable
    @Override
    public IGuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener focused) {
        this.focused = focused;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.startGlScissor(posX, posY, width, height);

        RenderSystem.disableDepthTest();

        elements.forEach(t -> t.render(matrixStack, mouseX, mouseY, tick));

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        scrollBar.render(matrixStack, mouseX, mouseY, tick);
    }

    public void renderTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick){
        elements.forEach(t -> t.renderTooltips(matrixStack, mouseX, mouseY, tick));
    }

    public List<IScrollElement> getElements() {
        return elements;
    }

    public int getScrollPanelHeight() {
        return scrollPanelHeight;
    }
}
