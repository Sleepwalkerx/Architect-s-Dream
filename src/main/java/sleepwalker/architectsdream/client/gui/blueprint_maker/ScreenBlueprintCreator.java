package sleepwalker.architectsdream.client.gui.blueprint_maker;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.IDisplayName;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.BaseCustomScreen;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.ScreenConstructor;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.ScreenPurpose;
import sleepwalker.architectsdream.client.gui.blueprint_maker.screens.MainSettingScreen;
import sleepwalker.architectsdream.client.gui.blueprint_maker.utils.FileStructureCreator;
import sleepwalker.architectsdream.client.gui.blueprint_maker.widget.ScrollListScreens;
import sleepwalker.architectsdream.network.PacketHandler;
import sleepwalker.architectsdream.network.PacketTempBlueprintToServer;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ScreenBlueprintCreator extends ContainerScreen<ContainerBlueprintCreator> {

    protected static final ResourceLocation BACKGROUND = new ResourceLocation(
        ArchitectsDream.MODID, 
        "textures/gui/blueprint_creator.png"
    );

    // CONTROLLERS
    public static final Map<ResourceLocation, Pair<ScreenPurpose, ScreenConstructor>> REGISTRY = Maps.newHashMap();

    protected final MainSettingScreen mainSettingScreen;

    protected final List<BaseCustomScreen> cacheScreens;

    @Nonnull
    protected BaseCustomScreen current;

    // UI
    protected ScrollListScreens listScreens;
    protected Button buttonCreateFile;

    private final List<IGuiEventListener> unremovableChildren = Lists.newArrayList();
    private final List<Widget> unremovableButtons = Lists.newArrayList();

    public static final String NAME = "blueprint_creator";

    public ScreenBlueprintCreator(ContainerBlueprintCreator screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        imageWidth = 382;
        imageHeight = 276;

        CompoundNBT mainData = screenContainer.getBlueprint().getOrCreateTag();

        mainSettingScreen = new MainSettingScreen(this, new ResourceLocation(ArchitectsDream.MODID, "main_setting"), mainData);

        current = mainSettingScreen;

        cacheScreens = Lists.newArrayList();
        cacheScreens.add(mainSettingScreen);

        if(mainData.contains(R.BlueprintTemplate.SCREENS_DATA, NBTTypes.LIST)){
            loadScreensData(mainData.getList(R.BlueprintTemplate.SCREENS_DATA, NBTTypes.OBJECT));
        }
    }

    protected void loadScreensData(@Nonnull ListNBT screensListNBT){
        for(int i = 0; i < screensListNBT.size(); i++){
            CompoundNBT screenNBT = screensListNBT.getCompound(i);
            ResourceLocation location = new ResourceLocation(screenNBT.getString(R.BlueprintTemplate.SCREEN_NAME));

            Pair<ScreenPurpose, ScreenConstructor> pair = REGISTRY.get(location);

            if(pair != null){
                BaseCustomScreen screen = pair.getValue().of(this, location);
                cacheScreens.add(screen);

                screen.readData(screenNBT);
            }
        }
    }

    /*private void parseSubScreens(@Nonnull BaseCustomScreen parent, ListNBT screensListNBT){
        if(!parent.getSubScreens().isEmpty()){
            parent.getSubScreens().forEach((name, constructor) -> {

                CompoundNBT dataScreen = new CompoundNBT();

                for(int i = 0; i < screensListNBT.size(); i++){
                    CompoundNBT compoundNBT = screensListNBT.getCompound(i);
                    ResourceLocation location = new ResourceLocation(compoundNBT.getString(R.BlueprintTemplate.SCREEN_NAME));

                    if(location == name){
                        dataScreen = compoundNBT;
                    }
                }

                BaseCustomScreen screen = constructor.of(this, name, dataScreen);
                parent.cacheSubScreens.add(screen);

                parseSubScreens(screen, dataScreen.getList(R.BlueprintTemplate.SCREENS_DATA, NBTTypes.OBJECT));
            });
        }
    }*/

    public void addScreen(@Nonnull BaseCustomScreen screen){
        cacheScreens.add(screen);
        listScreens.addScreen(screen);
    }

    @Override
    protected void init() {
        super.init();

        unremovableChildren.clear();
        unremovableButtons.clear();

        listScreens = new ScrollListScreens(
            minecraft, font,
            cacheScreens, this::sceneAction,
            91, 81, 95 + width / 2, 52 + height / 2
        );
        children.add(listScreens);

        buttonCreateFile = new Button(
            leftPos + 5,
            topPos + 250, 91, 20,
            new StringTextComponent(i18n("general",  "button_file_create")),
            this::createFile
        );
        addButton(buttonCreateFile);

        unremovableChildren.addAll(children);
        unremovableButtons.addAll(buttons);

        current.init();
    }

    protected void createFile(Button button){
        TemplateFileStructure templateFileStructure = new TemplateFileStructure();
        cacheScreens.forEach(screen -> screen.serializeTemplateStructure(templateFileStructure));

        FileStructureCreator.createBlueprintFile(templateFileStructure, mainSettingScreen);
    }

    private void sceneAction(BaseCustomScreen screen, int button){
        if(button == 1){
            if(screen != mainSettingScreen && screen.isIndependent()){
                cacheScreens.remove(screen);
                listScreens.removeScreen(screen);

                if(screen == current){
                    current = mainSettingScreen;
                }
            }
        }
        else if(screen != current){
            current.onSwitchScreen();

            children.clear();
            buttons.clear();

            children.addAll(unremovableChildren);
            buttons.addAll(unremovableButtons);

            screen.init();

            current = screen;
        }
    }

    @Override
    public void render(@Nonnull MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {

        renderBackground(pMatrixStack);

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        renderTooltip(pMatrixStack, pMouseX, pMouseY);

        listScreens.render(pMatrixStack, pMouseX,pMouseY, pPartialTicks);

        current.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
    }

    @Override
    protected void renderTooltip(@Nonnull MatrixStack pMatrixStack, int pX, int pY) {

        super.renderTooltip(pMatrixStack, pX, pY);

        current.renderTooltip(pMatrixStack, pX, pY);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack pMatrixStack, int pX, int pY) {
        current.renderLabels(pMatrixStack, pX, pY);
    }

    public PlayerInventory getPlayerInventory(){
        return inventory;
    }

    public void resizeSuper(Minecraft mc, int width, int height){
        super.resize(mc, width, height);
    }

    @Override
    public void resize(@Nonnull Minecraft pMinecraft, int pWidth, int pHeight) {
        current.resize(pMinecraft, pWidth, pHeight);
    }

    @Override
    public boolean isPauseScreen() {
        return current.isPauseScreen();
    }

    @Override
    public void tick() {
        current.tick();
        super.tick();
    }

    @Override
    public void onClose() {
        current.onClose();
        super.onClose();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return
            listScreens.mouseClicked(pMouseX, pMouseY, pButton) ||
            current.mouseClicked(pMouseX, pMouseY, pButton) ||
            super.mouseClicked(pMouseX, pMouseY, pButton)
        ;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return
            listScreens.mouseClicked(pMouseX, pMouseY, pButton) ||
            current.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) ||
            super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
        ;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return super.mouseScrolled(mouseX, mouseY, scroll) || current.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return super.mouseReleased(pMouseX, pMouseY, pButton) || current.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    protected boolean hasClickedOutside(double pMouseX, double pMouseY, int pGuiLeftIn, int pGuiTopIn, int pMouseButton) {
        return current.hasClickedOutside(pMouseX, pMouseY, pGuiLeftIn, pGuiTopIn, pMouseButton) || super.hasClickedOutside(pMouseX, pMouseY, pGuiLeftIn, pGuiTopIn, pMouseButton);
    }

    @Override
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        return current.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY) || super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }

    @Override
    protected void slotClicked(@Nonnull Slot pSlotIn, int pSlotId, int pMouseButton, @Nonnull ClickType pType) {
        current.slotClicked(pSlotIn, pSlotId, pMouseButton, pType);
        super.slotClicked(pSlotIn, pSlotId, pMouseButton, pType);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if(current.keyPressed(pKeyCode, pScanCode, pModifiers)){
            return true;
        }
        else return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return super.keyReleased(pKeyCode, pScanCode, pModifiers) || current.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char p_231042_1_, int p_231042_2_) {
        return super.charTyped(p_231042_1_, p_231042_2_) || current.charTyped(p_231042_1_, p_231042_2_);
    }

    @Override
    protected boolean checkHotbarKeyPressed(int pKeyCode, int pScanCode) {
        return current.checkHotbarKeyPressed(pKeyCode, pScanCode) || super.checkHotbarKeyPressed(pKeyCode, pScanCode);
    }

    @Override
    public void removed() {
        current.removed();

        CompoundNBT file = new CompoundNBT();
        mainSettingScreen.saveData(file);

        ListNBT screensData = new ListNBT();
        cacheScreens.forEach(screen -> {
            if(screen == mainSettingScreen) return;

            CompoundNBT screenNBT = new CompoundNBT();
            screenNBT.putString(R.BlueprintTemplate.SCREEN_NAME, screen.getRegistrationId().toString());
            screen.saveData(screenNBT);
            screensData.add(screenNBT);

        });

        file.put(R.BlueprintTemplate.SCREENS_DATA, screensData);

        if(!file.isEmpty()) {
            PacketHandler.INSTANCE.sendToServer(new PacketTempBlueprintToServer(
                file,
                getMenu().getHandIn()
            ));
        }

        super.removed();
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        minecraft.getTextureManager().bind(BACKGROUND);

        // Background
        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        current.renderBg(matrixStack, partialTicks, mouseX, mouseY);
    }

    public List<IGuiEventListener> getChildrenAction(){
        return children;
    }

    @Nonnull
    public <T extends Widget> T addButtonAction(@Nonnull T pButton) {
        return addButton(pButton);
    }

    @Nonnull
    public <T extends IGuiEventListener> T addWidgetAction(T widget) {
        return super.addWidget(widget);
    }

    @Nonnull
    public static String i18n(String screenName, String screenElement){
        return I18n.get(String.format("screen.%s.blueprint_creator.%s.%s", ArchitectsDream.MODID, screenName, screenElement));
    }

    public enum EnumFileFormat implements IDisplayName {
        JSON,
        NBT;

        @Override
        public ITextComponent getDisplayName() {
            return new StringTextComponent(toString());
        }
    }
}