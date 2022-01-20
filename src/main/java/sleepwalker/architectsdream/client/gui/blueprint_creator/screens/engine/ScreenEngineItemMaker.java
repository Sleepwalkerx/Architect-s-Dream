package sleepwalker.architectsdream.client.gui.blueprint_creator.screens.engine;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.BaseCustomScreen;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.structure.DataType;
import sleepwalker.architectsdream.structure.engine.StructureEngineItemMaker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ScreenEngineItemMaker extends BaseCustomScreen {

    protected ScrollRectItemStacks scrollRectItemStacks;
    protected List<ItemStack> cacheItemList = new ArrayList<>();

    public ScreenEngineItemMaker(ScreenBlueprintCreator parent, ResourceLocation registrationId, DataType dataType) {
        super(parent, registrationId, dataType);
    }

    @Override
    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        scrollRectItemStacks.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
    }

    @Override
    public void init() {
        scrollRectItemStacks = new ScrollRectItemStacks(
            cacheItemList,
            true,
            20, 200, 173,
            getLeftPos() + 5, getTopPos() + 5
        );
        addWidget(scrollRectItemStacks);
    }

    @Override
    public boolean charTyped(char word, int code) {
        return scrollRectItemStacks.charTyped(word, code);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return scrollRectItemStacks.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return scrollRectItemStacks.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return scrollRectItemStacks.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void saveData(@Nonnull CompoundNBT fileIn) {
        ScrollRectItemStacks.saveData(fileIn, cacheItemList);
    }

    @Override
    public void readData(@Nullable CompoundNBT fileIn) {

        if(fileIn != null){

            ScrollRectItemStacks.readData(fileIn, cacheItemList);
        }
    }

    private final FontRenderer font = Minecraft.getInstance().font;

    @Override
    public void renderLabels(MatrixStack pMatrixStack, int pX, int pY) {
        font.drawShadow(pMatrixStack, "<- Output Items", 210, 5, 0xa0a0a0);
    }

    @Override
    public void serializeTemplateStructure(TemplateFileStructure fileStructure) {

        if(cacheItemList.isEmpty()) {
            fileStructure.engine = new StructureEngineItemMaker(NonNullList.withSize(1, new ItemStack(Blocks.AIR)));
        }
        else {

            NonNullList<ItemStack> outputItems = NonNullList.of(cacheItemList.get(0), cacheItemList.toArray(new ItemStack[0]));

            fileStructure.engine = new StructureEngineItemMaker(outputItems);
        }
    }
}