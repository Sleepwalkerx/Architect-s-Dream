package sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_maker.ContainerBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_maker.ScreenBlueprintCreator;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public abstract class BaseCustomScreen implements IGuiEventListener {

    protected final ScreenBlueprintCreator parent;
    protected final ResourceLocation registrationId;
    protected String displayName;

    @Nonnull
    protected final List<BaseCustomScreen> subScreens;

    protected BaseCustomScreen(ScreenBlueprintCreator parent, ResourceLocation registrationId){
        this(parent, registrationId, Collections.emptyList());
    }

    protected BaseCustomScreen(ScreenBlueprintCreator parent, ResourceLocation registrationId, @Nonnull List<BaseCustomScreen> subScreens){
        this.parent = parent;
        this.registrationId = registrationId;
        this.subScreens = subScreens;

        displayName = getLocalName(registrationId, "name");
    }

    public String getLocalName(@Nonnull ResourceLocation registrationId, String elementName){
        return ScreenBlueprintCreator.i18n(
            (registrationId.getNamespace().contains(ArchitectsDream.MODID) ? registrationId.getPath() : registrationId.getNamespace() + "." + registrationId.getPath()),
            elementName
        );
    }

    public void onSwitchScreen(){ }

    protected String i18n(String element){
        return getLocalName(registrationId, element);
    }

    public void init() { }

    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) { }

    public void resize(Minecraft minecraft, int width, int height) {
        onSwitchScreen();
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

    protected void subSaveData(CompoundNBT fileIn){
        if(subScreens.isEmpty()) return;

        ListNBT listNBT = new ListNBT();
        for(BaseCustomScreen screen : subScreens){
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString(R.BlueprintTemplate.SCREEN_NAME, screen.registrationId.toString());

            screen.saveData(compoundNBT);
            listNBT.add(compoundNBT);
        }

        fileIn.put(R.BlueprintTemplate.SCREENS_DATA, listNBT);
    }

    protected void subReadData(CompoundNBT data){
        if(subScreens.isEmpty()) return;

        ListNBT listNBT = data.getList(R.BlueprintTemplate.SCREENS_DATA, NBTTypes.OBJECT);

        for(int i = 0; i < listNBT.size(); i++){
            final CompoundNBT compoundNBT = listNBT.getCompound(i);
            final ResourceLocation name = new ResourceLocation(compoundNBT.getString(R.BlueprintTemplate.SCREEN_NAME));

            subScreens
                .stream()
                .filter(screen -> screen.registrationId.equals(name))
                .findFirst()
            .ifPresent(screen -> screen.readData(compoundNBT));
        }
    }

    @Nonnull
    public List<BaseCustomScreen> getSubScreens() {
        return subScreens;
    }

    public void saveData(CompoundNBT fileIn){ }
    public void readData(CompoundNBT data){ }

    public ResourceLocation getRegistrationId() {
        return registrationId;
    }

    public abstract void serializeTemplateStructure(TemplateFileStructure fileStructure);

    @Nonnull
    public String getDisplayName(){
        return displayName;
    }
}
