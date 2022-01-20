package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.client.gui.widget.BaseScrollElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.ScrollRect;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowStructureViewer;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ValidatorInfoElement extends BaseScrollElement {

    protected final List<IModel> models;
    protected final IValidatorSerializer serializer;

    public ValidatorInfoElement(List<IModel> models, IValidatorSerializer serializer) {

        this.models = models;
        this.serializer = serializer;
    }

    public IValidatorSerializer getSerializer() {
        return serializer;
    }

    public void addModel(IModel model){
        models.add(model);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ValidatorInfoElement that = (ValidatorInfoElement) o;

        return serializer == that.serializer;
    }

    @Override
    public int hashCode() {
        return serializer.hashCode();
    }

    @Override
    public void showInStructureViewer(WindowStructureViewer viewer) {
        viewer.showModels(models.toArray(new IModel[0]));
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

        Minecraft.getInstance().getTextureManager().bind(ScrollRect.TEXTURE);

        if(isMouseOver(mouseX, mouseY)){
            AbstractGui.blit(matrixStack, posX, posY, 82, 0, 82, 24, 256, 256);
        }
        else {
            AbstractGui.blit(matrixStack, posX, posY, 0, 0, 82, 24, 256, 256);
        }

        //font.drawShadow(matrixStack, displayName, (posX + 23f) * 2, (posY + 10f) * 2, 0x000000);
    }
}
