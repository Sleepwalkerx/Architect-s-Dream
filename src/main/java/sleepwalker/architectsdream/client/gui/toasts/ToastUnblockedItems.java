package sleepwalker.architectsdream.client.gui.toasts;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import sleepwalker.architectsdream.ArchitectsDream;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ToastUnblockedItems implements IToast {
    private final ITextComponent title = new TranslationTextComponent("structure.action_item_create.toast.title");
    private final ItemStack[] itemStacks;
    private long lastChanged;
    private boolean hasNewOutputs;

    private static final ResourceLocation background = new ResourceLocation(ArchitectsDream.MODID, "textures/gui/toast.png");

    public ToastUnblockedItems(ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    @Nonnull
    public Visibility render(@Nonnull MatrixStack matrixStack, @Nonnull ToastGui toastGui, long delta) {
        if (this.hasNewOutputs) {
            this.lastChanged = delta;
            this.hasNewOutputs = false;
        }
        if (itemStacks.length == 0) {
            return IToast.Visibility.HIDE;
        } else {
            toastGui.getMinecraft().getTextureManager().bind(background);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            toastGui.blit(matrixStack, 0, 0, 0, 32, width(), height());
            toastGui.getMinecraft().font.draw(matrixStack, title, 30.0F, 7.0F, -11534256);
            toastGui.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(
                itemStacks[(int)(delta / Math.max(1L, 5000L / (long)itemStacks.length) % (long)itemStacks.length)],
                8,
                8
            );
            return delta - this.lastChanged >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
        }
    }
}
