package sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ContainerBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.structure.DataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public abstract class BaseCustomScreen implements IGuiEventListener {

    @Nonnull
    protected final ScreenBlueprintCreator parent;

    @Nonnull
    protected final ResourceLocation registrationId;

    @Nonnull
    protected final DataType type;

    protected String displayName;

    protected BaseCustomScreen(@Nonnull ScreenBlueprintCreator parent, @Nonnull ResourceLocation registrationId, @Nonnull DataType type){
        this.parent = parent;
        this.type = type;
        this.registrationId = registrationId;

        displayName = getLocalName(registrationId, "name");
    }

    public String getLocalName(@Nonnull ResourceLocation registrationId, String elementName){
        return ScreenBlueprintCreator.i18n(
            (registrationId.getNamespace().contains(ArchitectsDream.MODID) ? registrationId.getPath() : registrationId.getNamespace() + "." + registrationId.getPath()),
            elementName
        );
    }

    public void preSwitchScreen(){ }

    protected String i18n(String element){
        return getLocalName(registrationId, element);
    }

    public void init() { }

    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) { }

    public void resize(Minecraft minecraft, int width, int height) {
        preSwitchScreen();
        parent.resizeSuper(minecraft, width, height);
    }

    public void renderTooltip(MatrixStack pMatrixStack, int pX, int pY) { }

    protected void addButtons(@Nonnull Widget[] buttons){
        for(Widget widget : buttons){
            addButton(widget);
        }
    }

    public boolean isIndependent(){
        return true;
    }

    public void renderLabels(MatrixStack pMatrixStack, int pX, int pY) { }

    public void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) { }


    public boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeftIn, int pGuiTopIn, int pMouseButton) { return false; }

    public boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) { return false; }

    public void slotClicked(Slot pSlotIn, int pSlotId, int pMouseButton, ClickType pType) { }

    public boolean checkHotbarKeyPressed(int pKeyCode, int pScanCode) { return false; }

    public void removed() { }

    public boolean isPauseScreen() { return false; }

    public void tick() { }

    public void onClose() { }

    protected <T extends IGuiEventListener> T addWidget(T widget){ return parent.addWidgetAction(widget); }

    protected <T extends Widget> T addButton(T pButton) {
        return parent.addButtonAction(pButton);
    }

    protected List<IGuiEventListener> getChildren(){
        return parent.getChildrenAction();
    }

    protected PlayerInventory getPlayerInventory(){
        return parent.getPlayerInventory();
    }

    protected ContainerBlueprintCreator getMenu(){
        return parent.getMenu();
    }

    protected ItemStack getBlueprint(){
        return parent.getMenu().getBlueprint();
    }

    protected int getLeftPos(){
        return parent.getGuiLeft();
    }

    protected int getTopPos(){
        return parent.getGuiTop();
    }

    protected int getWidth(){
        return parent.width;
    }

    protected int getHeight(){
        return parent.height;
    }

    public void saveData(@Nonnull CompoundNBT fileIn){

    }

    public void readData(@Nullable CompoundNBT fileIn){

    }

    @Nonnull
    public DataType getType() {
        return type;
    }

    @Nonnull
    public ResourceLocation getRegistrationId() {
        return registrationId;
    }

    public abstract void serializeTemplateStructure(TemplateFileStructure fileStructure);

    @Nonnull
    public String getDisplayName(){
        return displayName;
    }
}
