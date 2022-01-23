package sleepwalker.architectsdream.client.gui.blueprint_creator;

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
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.gui.IDisplayName;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.BaseCustomScreen;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.CustomScreenCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.screens.MainSettingScreen;
import sleepwalker.architectsdream.client.gui.blueprint_creator.screens.StructureViewerScreen;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.FileStructureCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.widget.ScrollListScreens;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.network.PacketHandler;
import sleepwalker.architectsdream.network.PacketTempBlueprintToServer;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.structure.DataType;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ScreenBlueprintCreator extends ContainerScreen<ContainerBlueprintCreator> {

    protected static final ResourceLocation BACKGROUND = new ResourceLocation(
        ArchitectsDream.MODID, 
        "textures/gui/blueprint_creator.png"
    );

    // CONTROLLERS
    public static final Map<ResourceLocation, Pair<DataType, CustomScreenCreator>> REGISTRY = Maps.newHashMap();

    protected final MainSettingScreen mainSettingScreen;

    protected final List<BaseCustomScreen> cacheScreens;

    @Nonnull
    protected BaseCustomScreen current;

    // UI
    protected ScrollListScreens listScreens;
    protected Button buttonCreateFile;

    private final List<IGuiEventListener> creatorChildren = Lists.newArrayList();
    private final List<Widget> creatorWidgets = Lists.newArrayList();

    public static final String NAME = "blueprint_creator";

    public ScreenBlueprintCreator(ContainerBlueprintCreator screenContainer, PlayerInventory inv, ITextComponent title) {
        super(screenContainer, inv, title);

        imageWidth = 382;
        imageHeight = 276;

        CompoundNBT mainData = screenContainer.getBlueprint().getOrCreateTag();

        mainSettingScreen = new MainSettingScreen(this, ArchitectsDream.namespace("main_setting"));

        current = mainSettingScreen;

        cacheScreens = Lists.newArrayList();

        cacheScreens.add(mainSettingScreen);
        cacheScreens.add(new StructureViewerScreen(this, R.RenderProperty.NAME));

        loadScreensData(mainData.getList(R.BlueprintCreator.SCREENS_DATA, NBTTypes.OBJECT));

        if(mainData.contains(R.BlueprintCreator.CURRENT_SCREEN, NBTTypes.STRING)){

            ResourceLocation id = new ResourceLocation(mainData.getString(R.BlueprintCreator.CURRENT_SCREEN));

            cacheScreens
                    .stream()
                    .filter(screen -> screen.getRegistrationId().equals(id))
                    .findFirst()
            .ifPresent(screen -> current = screen);
        }

        if(!mainData.contains(R.BlueprintCreator.FIRST_LOAD, NBTTypes.BOOLEAN) || mainData.getBoolean(R.BlueprintCreator.FIRST_LOAD)){

            Config.CLIENT.startComponentsKit.get().forEach(componentName -> {

                ResourceLocation location = new ResourceLocation(componentName);

                Pair<DataType, CustomScreenCreator> pair = REGISTRY.get(location);

                if(pair != null && cacheScreens.stream().noneMatch(screen -> screen.getRegistrationId().equals(location))){

                    cacheScreens.add(pair.getValue().of(this, location, pair.getKey()));
                }
            });
        }
    }

    protected void loadScreensData(@Nonnull ListNBT listNBT){

        int listIndex = 0;

        for (BaseCustomScreen screen : cacheScreens) {

            if (listNBT.size() > listIndex) {

                CompoundNBT compoundNBT;

                ResourceLocation location;

                do {

                    compoundNBT = listNBT.getCompound(listIndex);
                    location = new ResourceLocation(compoundNBT.getString(R.BlueprintCreator.SCREEN_NAME));

                    listIndex++;

                    if (location.equals(screen.getRegistrationId())) {

                        screen.readData(compoundNBT);

                        break;
                    }

                }
                while (listIndex != listNBT.size());


            } else {

                screen.readData(null);
            }
        }

        for(int i = listIndex; i < listNBT.size(); i++){

            CompoundNBT compoundNBT = listNBT.getCompound(i);

            ResourceLocation name = new ResourceLocation(compoundNBT.getString(R.BlueprintCreator.SCREEN_NAME));

            Pair<DataType, CustomScreenCreator> pair = REGISTRY.get(name);

            if(pair != null){

                BaseCustomScreen screen = pair.getValue().of(this, name, pair.getKey());

                screen.readData(compoundNBT);

                cacheScreens.add(screen);
            }
        }
    }

    @Nonnull
    public BaseCustomScreen getCurrent() {
        return current;
    }

    public void addScreen(@Nonnull BaseCustomScreen screen){
        cacheScreens.add(screen);
        listScreens.addScreen(screen);
    }

    @Override
    protected void init() {
        super.init();

        creatorChildren.clear();
        creatorWidgets.clear();

        listScreens = new ScrollListScreens(
            this,
            cacheScreens, this::sceneAction,
            91, 81, 95 + width / 2, 52 + height / 2
        );
        creatorChildren.add(listScreens);

        buttonCreateFile = new Button(
            leftPos + 5,
            topPos + 250, 91, 20,
            new StringTextComponent(i18n("general",  "button_file_create")),
            this::createFile
        );

        addCreatorWidget(buttonCreateFile);

        onSwitchScreen(current);
    }

    @OnlyIn(Dist.CLIENT)
    protected void createFile(Button button){

        try {

            TemplateFileStructure templateFileStructure = new TemplateFileStructure();

            cacheScreens.forEach(screen -> screen.serializeTemplateStructure(templateFileStructure));

            FileStructureCreator.createBlueprintFile(templateFileStructure, mainSettingScreen);
        }
        catch (IllegalArgumentException e){

            FileStructureCreator.sendMessage(false, mainSettingScreen.getRegistryID() + "." + mainSettingScreen.getFileFormatOption().toString().toLowerCase(Locale.ROOT));

            ArchitectsDream.LOGGER.error(e.getMessage());
        }
    }

    protected void addCreatorWidget(Widget widget){
        creatorWidgets.add(widget);
        creatorChildren.add(widget);
    }

    public List<BaseCustomScreen> getCacheScreens() {
        return cacheScreens;
    }

    public MainSettingScreen getMainSettingScreen() {
        return mainSettingScreen;
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

            current.preSwitchScreen();

            onSwitchScreen(screen);

            current = screen;
        }
    }

    protected void onSwitchScreen(@Nonnull BaseCustomScreen next){

        children.clear();
        buttons.clear();

        children.addAll(creatorChildren);
        buttons.addAll(creatorWidgets);

        next.init();
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

        ListNBT screensData = new ListNBT();

        cacheScreens.forEach(screen -> {

            CompoundNBT screenNBT = new CompoundNBT();

            screen.saveData(screenNBT);

            screenNBT.putString(R.BlueprintCreator.SCREEN_NAME, screen.getRegistrationId().toString());

            screensData.add(screenNBT);
        });

        CompoundNBT compoundNBT = new CompoundNBT();

        compoundNBT.putString(R.BlueprintCreator.CURRENT_SCREEN, current.getRegistrationId().toString());
        compoundNBT.putBoolean(R.BlueprintCreator.FIRST_LOAD, false);
        compoundNBT.putString(R.BlueprintCreator.NAME, mainSettingScreen.getName());

        if(!screensData.isEmpty()) {
            compoundNBT.put(R.BlueprintCreator.SCREENS_DATA, screensData);
        }

        PacketHandler.INSTANCE.sendToServer(new PacketTempBlueprintToServer(
                compoundNBT,
                getMenu().getHandIn()
        ));

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