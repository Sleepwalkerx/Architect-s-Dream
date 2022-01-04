package sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.BaseScrollItemElement;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public abstract class BaseScrollItemTooltips extends BaseScrollItemElement {

    @Nonnull
    protected List<Text> tooltips;

    public BaseScrollItemTooltips(ItemStack stack) {
        super(stack);

        tooltips = Collections.emptyList();
    }

    protected void calcHeight(){
        height = tooltips.size() * 7 + (tooltips.size() == 0 ? 24 : 26);
    }

    protected abstract void genTooltips();

    @Override
    public void render(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        pMatrixStack.pushPose();
        pMatrixStack.scale(0.5f, 0.5f, 1f);

        for(int i = 0; i < tooltips.size(); i++){
            font.drawShadow(pMatrixStack, tooltips.get(i).component,
                    (posX + 4f + tooltips.get(i).x) * 2,
                    ((posY + 24f) + (i * 7f)) * 2,
                    tooltips.get(i).color
            );
        }

        pMatrixStack.popPose();
    }

    protected static class Text {

        ITextComponent component;

        int x;

        int color;

        public Text(ITextComponent component, int x){
            this.component = component;
            color = component.getStyle().getColor() == null ? 0xffffff : component.getStyle().getColor().getValue();
            this.x = x;
        }

        public static Text of(ITextComponent component, int x){
            return new Text(component, x);
        }
    }
}
