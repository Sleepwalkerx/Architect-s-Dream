package sleepwalker.architectsdream.client.gui.blueprint_maker.screens.engine;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import sleepwalker.architectsdream.client.gui.widget.IntegerFieldWidget;
import sleepwalker.architectsdream.client.gui.blueprint_maker.widget.SimpleScrollRect;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScrollRectItemStacks extends SimpleScrollRect<ScrollRectItemStacks.Element> {

    private static final ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
    private static final FontRenderer font = Minecraft.getInstance().font;
    private final PlayerInventory playerInventory;
    private final List<ItemStack> cacheElements;

    private IntegerFieldWidget integerField;
    private int lastActiveElement, lastRelativeY;

    private final boolean countable;

    private static final String
        ITEM_NAME = "item",
        COUNT = "count",
        LIST_ITEMS = "list_items"
    ;

    public ScrollRectItemStacks(@Nonnull List<ItemStack> elements, boolean countable, int elementHeight, int width, int height, int x, int y) {
        super(Minecraft.getInstance(), new ArrayList<>(elements.size()), elementHeight, width, height, x, y);

        if(countable) {
            elements.forEach(itemStack -> this.elements.add(new ElementCountable(itemStack)));
        }
        else elements.forEach(itemStack -> this.elements.add(new Element(itemStack)));

        this.cacheElements = elements;
        this.countable = countable;
        playerInventory = Objects.requireNonNull(Minecraft.getInstance().player).inventory;
    }

    @Override
    protected void drawPanel(MatrixStack matrixStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
        if(countable && activeElement != null && integerField.getTextField().isFocused()){
            drawPanelSimple(matrixStack, entryRight, relativeY, tess, mouseX, mouseY);
        }
        else super.drawPanel(matrixStack, entryRight, relativeY, tess, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if(countable && activeElement != null && integerField.isOnTextField(mouseX) && integerField.mouseScrolled(mouseX, mouseY, scroll)) {
            return true;
        }
        else return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean mouseClicked(double x, double y, int code) {
        if(super.mouseClicked(x, y, code)){
            return true;
        }

        if(x >= this.left && x <= this.left + width && y >= this.top && y <= this.top + height){
            if(code == 1){
                if(activeElement != null){
                    elements.remove(activeElement.getKey().intValue());
                    cacheElements.remove(activeElement.getKey().intValue());

                    activeElement = null;

                    return true;
                }
            }
            else if(code == 0 && canAddItem()){
                ItemStack stack = playerInventory.getCarried().copy();
                cacheElements.add(stack);
                elements.add(new ElementCountable(stack));

                return true;
            }
        }

        return false;
    }

    protected boolean canAddItem(){
        return !playerInventory.getCarried().isEmpty() &&
        elements.stream().noneMatch(
            element -> element.getStack().getItem() == playerInventory.getCarried().getItem()
        );
    }

    protected static void readData(@Nonnull CompoundNBT data, @Nonnull List<ItemStack> itemStackList) {
        itemStackList.clear();

        if(data.contains(LIST_ITEMS, NBTTypes.LIST)){
            ListNBT listNBT = data.getList(LIST_ITEMS, NBTTypes.OBJECT);

            for(int i = 0; i < listNBT.size(); i++){
                CompoundNBT compoundNBT = listNBT.getCompound(i);

                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compoundNBT.getString(ITEM_NAME)));

                if(item != null){
                    itemStackList.add(new ItemStack(item, Math.max(1, compoundNBT.getInt(COUNT))));
                }
            }
        }
    }

    protected static void saveData(CompoundNBT fileIn, @Nonnull List<ItemStack> cacheElements){
        if(cacheElements.isEmpty()) return;

        ListNBT listNBT = new ListNBT();

        for(ItemStack stack : cacheElements){
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString(ITEM_NAME, stack.getItem().getRegistryName().toString());
            if(stack.getCount() != 1){
                compoundNBT.putInt(COUNT, stack.getCount());
            }

            listNBT.add(compoundNBT);
        }

        fileIn.put(LIST_ITEMS, listNBT);
    }

    protected class Element implements SimpleScrollRect.IElement {

        protected final ItemStack stack;

        public Element(ItemStack stack) {
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }

        @Override
        public void render(MatrixStack matrixStack, int entryRight, int relativeY, int left, double offset, Tessellator tess, int mouseX, int mouseY) {
            if(offset != -1){
                AbstractGui.fill(matrixStack, left, relativeY,left + width,relativeY + elementHeight, 0x80FFA500);
            }

            renderer.renderGuiItem(stack, left + 3, relativeY + 2);
            font.drawShadow(matrixStack, stack.getHoverName(), left + 23, relativeY + 6, stack.getDisplayName().getStyle().getColor().getValue());
        }
    }

    protected class ElementCountable extends Element {

        public ElementCountable(ItemStack stack){
            super(stack);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return integerField.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return integerField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
            return integerField.keyPressed(pKeyCode, pScanCode, pModifiers);
        }

        @Override
        public boolean charTyped(char word, int code) {
            return integerField.charTyped(word, code);
        }

        @Override
        public void render(MatrixStack matrixStack, int entryRight, int relativeY, int left, double offset, Tessellator tess, int mouseX, int mouseY) {
            if(offset != -1){
                AbstractGui.fill(matrixStack, left, relativeY,left + width,relativeY + elementHeight, 0x80FFA500);

                if(lastActiveElement != activeElement.getKey() || lastRelativeY != relativeY || integerField == null){
                    lastActiveElement = activeElement.getKey();
                    lastRelativeY = relativeY;

                    integerField = new IntegerFieldWidget(
                        new StringTextComponent("number"),
                        this::onIntFieldChanged,
                        stack.getCount()
                    )
                    .init(Minecraft.getInstance().font, left + 24, relativeY + 1);
                }

                integerField.render(matrixStack, mouseX, mouseY, 60);
            }

            renderer.renderGuiItem(stack, left + 3, relativeY + 2);
            renderer.renderGuiItemDecorations(font, stack, left + 3, relativeY + 2);
            font.drawShadow(matrixStack, stack.getHoverName(), left + (offset != -1 ? 84 : 23), relativeY + 6, stack.getDisplayName().getStyle().getColor().getValue());
        }

        protected boolean onIntFieldChanged(int value){
            if(value > 0){
                stack.setCount(value);
                return true;
            }
            else return false;
        }
    }
}
