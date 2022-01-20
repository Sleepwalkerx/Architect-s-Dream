package sleepwalker.architectsdream.client.gui.blueprint_viewer.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.IWindow;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.widget.viewer.StructureViewer;
import sleepwalker.architectsdream.structure.RenderProperty;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class WindowStructureViewer extends StructureViewer implements IWindow {

    private final String displayName;

    private final ScreenBlueprintViewer viewer;

    private final FontRenderer font = Minecraft.getInstance().font;

    public WindowStructureViewer() {

        super();

        this.viewer = ScreenBlueprintViewer.activeViewer();

        this.displayName = I18n.get(String.format("screen.%s.%s.window_structure_viewer.name", ArchitectsDream.MODID, R.ScreenName.SCREEN_BLUEPRINT_VIEWER));
    }

    @Override
    public void init(RenderProperty property) {
        super.init(property);

        viewer.addSavableObject(getCamera());
    }

    @Override
    public void setHighlight(boolean value) {

    }

    @Override
    public void renderLabel(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        drawStringCenter(matrixStack, posX - viewer.getGuiLeft() + (width / 2f), posY - viewer.getGuiTop() - font.lineHeight - 5, displayName);
    }

    private void drawStringCenter(@Nonnull MatrixStack matrixStack, float x, float y, String text){
        font.draw(matrixStack, text, x - font.width(text) / 2f, y, 0x666666);
    }
}
