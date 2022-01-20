package sleepwalker.architectsdream.client.gui.blueprint_creator.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.gui.ScrollPanel;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.BaseCustomScreen;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class ScrollListScreens extends ScrollPanel {

    private final FontRenderer font;
    private final List<Element> screens;
    private final BiConsumer<BaseCustomScreen, Integer> function;
    private final ScreenBlueprintCreator parent;

    public ScrollListScreens(
        ScreenBlueprintCreator parent,
        List<BaseCustomScreen> screens, BiConsumer<BaseCustomScreen, Integer> function,
        int width, int height, int x, int y
    ){
        super(Minecraft.getInstance(), width, height, y, x);
        this.font = Minecraft.getInstance().font;

        this.parent = parent;

        this.screens = Lists.newArrayListWithExpectedSize(screens.size());

        screens.forEach(this::addScreen);

        this.function = function;

        updateState();
    }

    public void addScreen(BaseCustomScreen screen){

        this.screens.add(new Element(screen));

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

            if(screens.get(i).screen == parent.getCurrent()){

                font.drawShadow(matrixStack,
                        screens.get(i).screen.getDisplayName(),
                        left + 2f,
                        relativeY, i == elementHover ? 0xf0ca35 : 0x24ef75
                );
            }
            else {

                font.drawShadow(matrixStack,
                        screens.get(i).screen.getDisplayName(),
                        left + 2f,
                        relativeY, i == elementHover ? 0xf0ca35 : 0xFFFFFF
                );
            }

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

        public Element(BaseCustomScreen screen) {
            this(screen, Collections.emptyList());
        }

        public Element(BaseCustomScreen screen, List<Element> children) {
            this.screen = screen;
            this.children = children;
        }
    }
}