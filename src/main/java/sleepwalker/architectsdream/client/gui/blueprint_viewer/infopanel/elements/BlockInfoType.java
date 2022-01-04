package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.elements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import sleepwalker.architectsdream.client.gui.BaseGuiElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block.ModelBlock;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.BaseScrollItemElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.IScrollItemElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.ScrollRect;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowStructureViewer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlockInfoType extends BaseScrollItemElement {

    private final List<BlockInfoElement> elements;

    protected final List<Pair<String, Integer>> list = new ArrayList<>();

    private boolean selected;

    public BlockInfoType(@Nonnull ModelBlock model) {
        super(model.getItemStack());

        this.elements = Lists.newArrayList(new BlockInfoElement(model.getEntity()));

        this.width = 82;
        this.height = 24;
    }

    public void addElement(ModelBlock modelBlock){

        if(elements.stream().noneMatch(
            blockInfoElement -> blockInfoElement.add(modelBlock.getEntity()))
        ){

            elements.add(new BlockInfoElement(modelBlock.getEntity()));
        }

        itemStack.grow(1);
    }

    @Override
    public void setActive(boolean value) {

        selected = value;

        if(selected){
            showBlockInfoElements();
        }
        else ScreenBlueprintViewer.activeViewer().getInfoPanel().getEntityInfo().clear();
    }

    public void createTooltip(){

        list.add(Pair.of(itemStack.getHoverName().getString(), 0xFFFFFF));

        if(elements.stream().anyMatch(e -> e.getContainer().getTags() != null)){

            list.add(Pair.of("* has tags", 0xfdf401));
        }

        if(elements.stream().anyMatch(e -> e.getContainer().isHasProperties())){

            list.add(Pair.of("* has properties", 0xfdf401));
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {

        if(isMouseOver(pMouseX, pMouseY)){

            if(super.mouseReleased(pMouseX, pMouseY, pButton)){
                return true;
            }

            if(!ScreenBlueprintViewer.activeViewer().getInfoPanel().isActiveElement(this)){

                ScreenBlueprintViewer.activeViewer().getInfoPanel().setActiveElement(this);

                return true;
            }
        }

        return false;
    }

    private void showBlockInfoElements(){

        ScrollRect rect = ScreenBlueprintViewer.activeViewer().getInfoPanel().getEntityInfo();

        rect.clear();

        rect.getElements().addAll(elements);

        rect.loadElements();
    }

    @Override
    public void showInStructureViewer(WindowStructureViewer viewer) {
        //viewer.showModels(models.toArray(new IModel[0]));
    }

    @Override
    public boolean canShowInStructureViewer() {
        return true;
    }

    @Override
    public boolean canShowInInfoPanel() {
        return true;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        renderCover(selected || isMouseOver(mouseX, mouseY), matrixStack);

        itemRenderer.renderGuiItemDecorations(font, itemStack, getItemPosX(), getItemPosY());
        itemRenderer.renderGuiItem(itemStack, getItemPosX(), getItemPosY());

        matrixStack.pushPose();
        matrixStack.scale(0.5f, 0.5f, 1f);

        int localRelativeY = list.size() / 2 * 6;

        if(list.size() % 2 == 0) localRelativeY -= 3;

        for (Pair<String, Integer> pair : list) {
            font.drawShadow(matrixStack, pair.getKey(), (posX + 23f) * 2, (posY + 10f - localRelativeY) * 2, pair.getValue());
            localRelativeY -= 6;
        }

        matrixStack.popPose();
    }
}
