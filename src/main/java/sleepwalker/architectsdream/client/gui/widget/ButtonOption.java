package sleepwalker.architectsdream.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.IDisplayName;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ButtonOption<T extends IDisplayName> extends AbstractButton {
    private final List<T> values;
    private T selectedValue;
    private int selectedValueIndex;
    private final FontRenderer fontRenderer;

    @Nonnull
    private OnPressed<T> pressed = buttonOption -> { };

    public ButtonOption(int widthIn, int heightIn, int width, int height, String text, T startValue, @Nonnull List<T> values) {
        super(widthIn, heightIn, width, height, new StringTextComponent(text));
        this.selectedValueIndex = values.indexOf(startValue);
        selectedValue = startValue;
        this.values = values;
        this.fontRenderer = Minecraft.getInstance().font;
    }

    public void setPressed(@Nonnull OnPressed<T> pressed){
        this.pressed = pressed;
    }

    public ButtonOption(int widthIn, int heightIn, int width, int height, String text, T startValue, T[] values) {
        this(widthIn, heightIn ,width, height, text, startValue, Arrays.asList(values));
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(matrixStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        renderBg(matrixStack, minecraft, p_renderButton_1_, p_renderButton_2_);
        drawCenteredString(
            matrixStack,
            fontRenderer, 
            selectedValue.getDisplayName().getString(), 
            this.x + this.width / 2, 
            this.y + (this.height - 8) / 2, 
            0xffffff
        );
    }

    @FunctionalInterface
    public interface OnPressed<T extends IDisplayName> {
        void onPressed(ButtonOption<T> buttonOption);
    }

    public T getSelected(){
        return selectedValue;
    }

    @Override
    public void onPress() {
        selectedValue = values.get(++selectedValueIndex % values.size());

        pressed.onPressed(this);
    }
}
