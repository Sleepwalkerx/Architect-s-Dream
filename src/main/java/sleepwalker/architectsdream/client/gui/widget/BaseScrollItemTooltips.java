package sleepwalker.architectsdream.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import sleepwalker.architectsdream.client.gui.widget.BaseScrollItemElement;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public abstract class BaseScrollItemTooltips extends BaseScrollItemElement {

    @Nonnull
    protected List<SimpleTooltipText> tooltips;

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

            tooltips.get(i).render(pMatrixStack, font, (posX + 4f + tooltips.get(i).localPosX) * 2, ((posY + 24f) + (i * 7f)) * 2);
        }

        pMatrixStack.popPose();
    }

    public static class SimpleTooltipText {

        private final String text;

        private final int localPosX;

        private final int color;

        public SimpleTooltipText(@Nonnull String text, int localPosX){
            this(text, localPosX, 0xffffff);
        }

        public SimpleTooltipText(@Nonnull String text, TextFormatting formatting){
            this(text, 0, formatting);
        }

        public SimpleTooltipText(@Nonnull String text, int localPosX, TextFormatting formatting){
            this(text, localPosX, formatting.getColor());
        }

        public SimpleTooltipText(@Nonnull String text, int localPosX, int color){

            this.text = text;
            this.localPosX = localPosX;
            this.color = color;
        }

        public int getLocalPosX() {
            return localPosX;
        }

        public void render(MatrixStack stack, @Nonnull FontRenderer font, float x, float y){
            font.drawShadow(stack, text, x, y, color);
        }
    }
}
