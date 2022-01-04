package sleepwalker.architectsdream.client.gui.blueprint_maker.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.gui.ScrollPanel;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.BaseCustomScreen;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ScrollListScreens extends ScrollPanel {

    private final FontRenderer font;
    private final List<Element> screens;
    private final BiConsumer<BaseCustomScreen, Integer> function;

    public ScrollListScreens(
        Minecraft client, FontRenderer font,
        List<BaseCustomScreen> screens, BiConsumer<BaseCustomScreen, Integer> function,
        int width, int height, int x, int y
    ){
        super(client, width, height, y, x);
        this.font = font;

        this.screens = Lists.newArrayListWithExpectedSize(screens.size());

        screens.forEach(this::addScreen);

        this.function = function;

        updateState();
    }

    private void parseSubScreens(@Nonnull List<Element> children, BaseCustomScreen parent, final int indent, final int indentFactor){
        if(!parent.getSubScreens().isEmpty()){
            parent.getSubScreens().forEach(screen -> {
                Element element = new Element(screen, indent);
                screens.add(element);
                children.add(element);
                parseSubScreens(children, screen, indent + indentFactor, indentFactor);
            });
        }
    }

    public void addScreen(BaseCustomScreen screen){
        if(screen.getSubScreens().isEmpty()){
            this.screens.add(new Element(screen, 0));
        }
        else{
            Element element = new Element(screen, 0, new ArrayList<>());
            this.screens.add(element);
            parseSubScreens(element.children, screen, 10, 10);
        }

        updateState();
    }

    public void removeScreen(BaseCustomScreen screen){
        Element element = screens.stream().filter(element1 -> element1.screen == screen).findFirst().get();
        screens.removeAll(element.children);

        screens.remove(element);

        updateState();
    }

    public void updateState(){
        int max = this.getContentHeight() - (this.height - this.border);
        if (max < 0) max /= 2;
        if (this.scrollDistance < 0.0F) this.scrollDistance = 0.0F;
        if (this.scrollDistance > max) this.scrollDistance = max;
    }

    @Override
    public int getContentHeight(){
        return screens.size() * elementHeight();
    }

    private int elementHeight(){
        return font.lineHeight + 1;
    }

    @Override
    protected int getScrollAmount(){
        return font.lineHeight * 3;
    }

    private int findFocusElement(int mouseX, int mouseY) {
        if(mouseX < left || mouseX > left + width) return -1;

        double offset = (mouseY - top) + border + scrollDistance + 1;

        if (offset <= 0) {
            return -1;
        }

        return (int) (offset / elementHeight()) - 1;
    }

    @Override
    protected void drawPanel(MatrixStack matrixStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY){
        int elementHover = findFocusElement(mouseX, mouseY);

        for(int i = 0; i < screens.size(); i++){
            font.drawShadow(matrixStack,
                screens.get(i).screen.getDisplayName(),
                left + 2f + screens.get(i).indent,
                relativeY, i == elementHover ? 0xf0ca35 : 0xFFFFFF
            );
            relativeY += elementHeight();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int index = findFocusElement((int) mouseX, (int) mouseY);
        if(index >= 0 && index < screens.size()){
            function.accept(screens.get(index).screen, button);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static class Element {

        protected final BaseCustomScreen screen;
        protected final List<Element> children;
        protected final int indent;

        public Element(BaseCustomScreen screen, int indent) {
            this(screen, indent, Collections.emptyList());
        }

        public Element(BaseCustomScreen screen, int indent, List<Element> children) {
            this.screen = screen;
            this.indent = indent;
            this.children = children;
        }
    }
}