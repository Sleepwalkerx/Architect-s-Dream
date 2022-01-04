package sleepwalker.architectsdream.client.gui.blueprint_maker.screens.types;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.IntNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.client.gui.blueprint_maker.widget.SimpleScrollRect;

import javax.annotation.Nonnull;
import java.util.Map;

public class BlockTagElement implements SimpleScrollRect.IElement {

    @Nonnull
    private final ITag<Block> tag;

    @Nonnull
    private final ResourceLocation name;

    private boolean isActive;

    public BlockTagElement(@Nonnull ITag<Block> tag, @Nonnull ResourceLocation name) {
        this.tag = tag;
        this.name = name;
    }

    @Override
    public void render(MatrixStack matrixStack, int entryRight, int relativeY, int left, double offset, Tessellator tess, int mouseX, int mouseY) {

        Minecraft.getInstance().font.drawShadow(matrixStack, name.toString(), left + 4f, relativeY + -1f, isActive ? 0x5ce88a : 0xffffff);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        isActive = !isActive;

        return true;
    }

    public boolean isActive() {
        return isActive;
    }

    @Nonnull
    public ResourceLocation getName() {
        return name;
    }

    @Nonnull
    public ITag<Block> getTag() {
        return tag;
    }

    protected IntNBT saveData(){
        return IntNBT.valueOf(isActive ? 1 : 0);
    }

    protected void readData(int id){
        isActive = id == 1;
    }
}
