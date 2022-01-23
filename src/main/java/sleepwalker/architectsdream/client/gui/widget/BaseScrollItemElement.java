package sleepwalker.architectsdream.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import sleepwalker.architectsdream.ModStates;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.IScrollItemElement;
import sleepwalker.architectsdream.client.gui.widget.BaseScrollElement;
import sleepwalker.architectsdream.plugins.jei.JeiSleepPlugin;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class BaseScrollItemElement extends BaseScrollElement implements IScrollItemElement {

    protected final ItemStack itemStack;

    protected final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    protected final FontRenderer font = Minecraft.getInstance().font;

    protected final String hoverName;

    protected BaseScrollItemElement(@Nonnull ItemStack itemStack) {

        this.itemStack = itemStack;

        hoverName = convert(itemStack.getHoverName().getString());

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

        if(itemStack.getItem() == Items.AIR){
            return false;
        }

        if(ModStates.JEI && button == 0 && isOverItem((int)x, (int)y)){


            IFocus<ItemStack> focus = JeiSleepPlugin.runtime.getRecipeManager().createFocus(IFocus.Mode.OUTPUT, getItemStack());

            JeiSleepPlugin.runtime.getRecipesGui().show(focus);

            return true;
        }

        return false;
    }

    private String convert(@Nonnull String text){

        if(font.width(text) > 108){

            text = font.plainSubstrByWidth(text, 108);

            text = text.substring(0, text.length() - 1);

            text = text + "...";
        }

        return text;
    }

    @Override
    public void render(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {

        renderCover(isMouseOver(pMouseX, pMouseY), pMatrixStack);

        itemRenderer.renderGuiItemDecorations(font, itemStack, getItemPosX(), getItemPosY());
        itemRenderer.renderGuiItem(itemStack, getItemPosX(), getItemPosY());

        pMatrixStack.pushPose();
        pMatrixStack.scale(0.5f, 0.5f, 1f);

        font.drawShadow(pMatrixStack, hoverName, (posX + 23f) * 2, (posY + 10f) * 2, 0xffffff);

        pMatrixStack.popPose();
    }

    @Override
    public void renderTooltips(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        if(isOverItem(mouseX, mouseY)){

            ScreenBlueprintViewer viewer = ScreenBlueprintViewer.activeViewer();

            FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);

            net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(itemStack);

            List<ITextComponent> list = viewer.getTooltipFromItem(itemStack);

            if(ModStates.JEI){
                list.add(1, new StringTextComponent("[" + ScreenBlueprintViewer.screenI18n("jei_item_tooltip") + "]").withStyle(TextFormatting.YELLOW));
            }

            viewer.renderWrappedToolTip(matrixStack, list, mouseX, mouseY, (font == null ? this.font : font));

            net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
        }
    }
}
