package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.group;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.BaseGuiElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoGroup;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.IScrollElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.ScrollRect;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SimpleInfoGroup<T extends IScrollElement> extends BaseGuiElement implements IInfoGroup {

    protected final ITextComponent displayName;

    protected final List<T> elements = new ArrayList<>();

    private static final FontRenderer font = Minecraft.getInstance().font;

    public SimpleInfoGroup(String name) {

        displayName = new TranslationTextComponent(String.format("scene.%s.info_group.%s", ArchitectsDream.MODID, name));

        height = 24;
        width = 82;
    }

    public List<? extends IScrollElement> getElements() {
        return elements;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        Minecraft.getInstance().getTextureManager().bind(ScrollRect.TEXTURE);

        if(isMouseOver(mouseX, mouseY)){
            AbstractGui.blit(matrixStack, posX, posY, 82, 0, 82, 24, 256, 256);
        }
        else {
            AbstractGui.blit(matrixStack, posX, posY, 0, 0, 82, 24, 256, 256);
        }

        font.drawShadow(matrixStack, displayName, (posX + 23f) * 2, (posY + 10f) * 2, 0x000000);
    }

    @Override
    public void build() {

    }

    @Override
    public boolean canShowInStructureViewer() {
        return false;
    }

    @Override
    public boolean canShowInInfoPanel() {
        return false;
    }
}
