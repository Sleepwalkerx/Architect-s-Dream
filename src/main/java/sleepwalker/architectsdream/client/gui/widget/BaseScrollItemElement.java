package sleepwalker.architectsdream.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import sleepwalker.architectsdream.ModStates;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.IScrollItemElement;
import sleepwalker.architectsdream.client.gui.widget.BaseScrollElement;
import sleepwalker.architectsdream.plugins.jei.JeiSleepPlugin;

import javax.annotation.Nonnull;

public abstract class BaseScrollItemElement extends BaseScrollElement implements IScrollItemElement {

    protected final ItemStack itemStack;

    protected final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    protected final FontRenderer font = Minecraft.getInstance().font;

    protected BaseScrollItemElement(ItemStack itemStack) {

        this.itemStack = itemStack;

        height = 24;

        width = 82;
    }


    @Nonnull
    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {

        if(ModStates.JEI && button == 0 && isOverItem((int)x, (int)y)){

            IFocus<ItemStack> focus = JeiSleepPlugin.runtime.getRecipeManager().createFocus(IFocus.Mode.OUTPUT, getItemStack());

            JeiSleepPlugin.runtime.getRecipesGui().show(focus);

            return true;
        }

        return false;
    }

    @Override
    public void render(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {

        renderCover(isMouseOver(pMouseX, pMouseY), pMatrixStack);

        itemRenderer.renderGuiItemDecorations(font, itemStack, getItemPosX(), getItemPosY());
        itemRenderer.renderGuiItem(itemStack, getItemPosX(), getItemPosY());

        pMatrixStack.pushPose();
        pMatrixStack.scale(0.5f, 0.5f, 1f);

        font.drawShadow(pMatrixStack, itemStack.getHoverName(), (posX + 23f) * 2, (posY + 10f) * 2, 0xffffff);

        pMatrixStack.popPose();
    }
}
