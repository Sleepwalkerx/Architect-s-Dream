package sleepwalker.architectsdream.client.gui.blueprint_maker.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.BaseCustomScreen;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.ScreenConstructor;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.ScreenPurpose;
import sleepwalker.architectsdream.client.gui.blueprint_maker.utils.FileStructureCreator;
import sleepwalker.architectsdream.client.gui.blueprint_maker.utils.ValidatorMode;
import sleepwalker.architectsdream.client.gui.blueprint_maker.utils.WorldRenderer;
import sleepwalker.architectsdream.client.gui.blueprint_maker.widget.SimpleScrollRect;
import sleepwalker.architectsdream.client.gui.widget.ButtonOption;
import sleepwalker.architectsdream.client.gui.blueprint_maker.*;
import sleepwalker.architectsdream.client.gui.widget.IntegerFieldWidget;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.serialize.converters.EnumNBT;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.EnumCondition;
import sleepwalker.architectsdream.utils.NBTTypes;
import sleepwalker.architectsdream.utils.NBTUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class MainSettingScreen extends BaseCustomScreen {

    private TextFieldWidget
        registryID,
        nameField,
        authorName
    ;

    private final IntegerFieldWidget useOfNumber;

    protected ButtonOption<Blueprint.Rarity> rarityOption;
    protected ButtonOption<ValidatorMode> modeOption;
    protected ButtonOption<ScreenBlueprintCreator.EnumFileFormat> fileFormatOption;

    protected SimpleScrollRect<ScreenElement> scrollRect;

    protected final int elementHeight = Minecraft.getInstance().font.lineHeight + 1;
    
    protected final FontRenderer font;
    protected final Minecraft minecraft;

    // Cache
    protected String cacheRegID, cacheName, cacheAuthor;

    protected final List<Pair<ScreenPurpose, List<ScreenElement>>> scrollElements;

    protected Blueprint.Rarity cacheRarity;
    protected ValidatorMode cacheMode;
    protected ScreenBlueprintCreator.EnumFileFormat cacheFileFormat;

    public static final String ID = "main_setting";

    private int activeTab = 0;
    private int tabLeft, tabTop;

    private static final ResourceLocation TABS = new ResourceLocation(ArchitectsDream.MODID, "textures/gui/tabs.png");

    public MainSettingScreen(ScreenBlueprintCreator parent, ResourceLocation id, CompoundNBT data) {
        super(parent, id);

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        scrollElements = Lists.newArrayList(
            createFor(ScreenPurpose.PALETTE_TYPE),
            createFor(ScreenPurpose.ENGINE)
        );

        useOfNumber = new IntegerFieldWidget(
            new StringTextComponent("num_of_use"),
            (num) -> num >= -1,
            -1
        );

        readData(data);
    }

    @Override
    public void init() {
        registryID = new TextFieldWidget(
            font,
            getLeftPos() + 7, getTopPos() + 22,
            100, 12, new StringTextComponent("reg_id")
        );
        registryID.setTextColor(-1);
        registryID.setMaxLength(35);
        registryID.setFilter(str -> str.matches("[a-z0-9_]*"));
        registryID.setValue(cacheRegID);
        getChildren().add(registryID);

        tabLeft = getLeftPos() + 230;
        tabTop = getTopPos() + 7;

        nameField = new TextFieldWidget(
            font, getLeftPos() + 120, getTopPos() + 22,
            100, 12, new StringTextComponent("name")
        );
        nameField.setTextColor(-1);
        nameField.setFilter(str -> str.matches("[\\s\\w]*"));
        nameField.setResponder(name -> {
            if(Config.CLIENT.duplicateRegID.get()){
                String replaceText = nameField.getValue().replace(' ', '_');
                replaceText = replaceText.toLowerCase();
                registryID.setValue(replaceText);
            }
        });
        nameField.setMaxLength(35);
        nameField.setValue(cacheName);
        addWidget(nameField);

        rarityOption = new ButtonOption<>(
            getLeftPos() + 119, getTopPos() + 49,
            103, 20,
            "rarity",
            cacheRarity,
            Blueprint.Rarity.values()
        );
        addButton(rarityOption);

        modeOption = new ButtonOption<>(
            getLeftPos() + 119, getTopPos() + 86,
            103, 20,
            "mode",
            cacheMode,
            Lists.newArrayList(ValidatorMode.getValidators())
        );
        addButton(modeOption);

        authorName = new TextFieldWidget(
            font,
            getLeftPos() + 7, getTopPos() + 51,
            100, 12, new StringTextComponent("author_name")
        );

        authorName.setTextColor(-1);
        authorName.setValue(cacheAuthor);
        getChildren().add(authorName);

        fileFormatOption = new ButtonOption<>(
            getLeftPos() + 6, getTopPos() + 86, 103, 20,
            "FileFormat",
            cacheFileFormat,
            ScreenBlueprintCreator.EnumFileFormat.values()
        );
        addButton(fileFormatOption);

        useOfNumber.init(
            font,
            getLeftPos() + 7, getTopPos() + 123,
            80
        );

        scrollRect = new SimpleScrollRect<>(
            minecraft,
            Lists.newArrayList(scrollElements.get(activeTab).getValue()),
            elementHeight,
            137,
            146,
            getLeftPos() + 235,
            getTopPos() + 27
        );
    }

    private Pair<ScreenPurpose, List<ScreenElement>> createFor(ScreenPurpose purpose){
         return Pair.of(purpose, ScreenBlueprintCreator.REGISTRY
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().getKey() == purpose)
            .map(entry -> new ScreenElement(entry.getValue().getValue(), entry.getKey()))
        .collect(Collectors.toList()));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float particalTick) {
        RenderSystem.disableBlend();

        registryID.render(matrixStack, mouseX, mouseY, particalTick);
        nameField.render(matrixStack, mouseX, mouseY, particalTick);
        authorName.render(matrixStack, mouseX, mouseY, particalTick);
        scrollRect.render(matrixStack, mouseX, mouseY, particalTick);
        useOfNumber.render(matrixStack, mouseX, mouseY, particalTick);
    }

    @Override
    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        return useOfNumber.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {

        if(tabTop <= pMouseY && pMouseY < tabTop + 15){
            double tabIndex = (pMouseX - tabLeft) / tabWidth;

            if(tabIndex >= 0 && tabIndex < 2){
                activeTab = (int)tabIndex;
                scrollRect.swapElements(scrollElements.get(activeTab).getValue());
                return true;
            }
        }

        return scrollRect.mouseClicked(pMouseX, pMouseY, pButton) || useOfNumber.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return scrollRect.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) || useOfNumber.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int pScanCode, int pModifiers) {

        if(keyCode == 256){
            return false;
        }

        if(useOfNumber.keyPressed(keyCode, pScanCode, pModifiers) || useOfNumber.canConsumeInput()){
            return true;
        }
        else if(nameField.keyPressed(keyCode, pScanCode, pModifiers) || nameField.canConsumeInput()){
            return true;
        }
        else if(registryID.keyPressed(keyCode, pScanCode, pModifiers) || registryID.canConsumeInput()){
            return true;
        }
        else if(authorName.keyPressed(keyCode, pScanCode, pModifiers) || authorName.canConsumeInput()){
            return true;
        }
        else return false;
    }

    @Override
    public boolean charTyped(char symbol, int p_231042_2_) {

        if(useOfNumber.charTyped(symbol, p_231042_2_) || useOfNumber.canConsumeInput()){
            return true;
        }
        if(nameField.charTyped(symbol, p_231042_2_) || nameField.canConsumeInput()){
            return true;
        }
        else if(registryID.charTyped(symbol, p_231042_2_) || registryID.canConsumeInput()){
            return true;
        }
        else if(authorName.charTyped(symbol, p_231042_2_) || authorName.canConsumeInput()){
            return true;
        }
        else return super.charTyped(symbol, p_231042_2_);
    }

    @Override
    public void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.disableBlend();

        font.draw(matrixStack, i18n("title_reg_id"), 6, 11, 0x727272);
        font.draw(matrixStack, i18n("title_name"), 119, 11, 0x727272);
        font.draw(matrixStack, i18n("title_rarity"), 119, 40, 0x727272);
        font.draw(matrixStack, i18n("title_author"), 6, 40, 0x727272);
        font.draw(matrixStack, i18n("title_mode"), 119, 77, 0x727272);
        font.draw(matrixStack, i18n("title_file_format"), 6, 77, 0x727272);
        font.draw(matrixStack, i18n("title_num_of_use"), 6, 113, 0x727272);
        font.draw(matrixStack, "(-1 == infinity)", 6, 145, 0x727272);

        // табы
        for(int i = 0; i < scrollElements.size(); i++){
            AbstractGui.drawCenteredString(
                matrixStack,
                font,
                scrollElements.get(i).getKey().getDisplayName(),
                267 + i * tabWidth, 12, 0xFFFFFF
            );
        }
    }

    private static final int texWidth = 158, texHeight = 175, tabWidth = 74;

    @Override
    public void renderBg(MatrixStack pMatrixStack, float pPartialTicks, int pX, int pY) {

        minecraft.getTextureManager().bind(TABS);

        // Отрисовка неактивных табов - полностью
        for(int i = 0 ; i < 2; i++){
            if(i != activeTab){
                AbstractGui.blit(
                    pMatrixStack,
                    tabLeft + tabWidth * i, tabTop,
                    tabWidth, 1,
                    tabWidth, 18,
                    texWidth, texHeight
                );
            }
        }

        // background
        AbstractGui.blit(
            pMatrixStack,
            getLeftPos() + 230, getTopPos() + 22,
            0, 19,
            texWidth, 156,
            texWidth, texHeight
        );

        // отрисовка активного таба - полностью
        AbstractGui.blit(
            pMatrixStack,
            tabLeft + tabWidth * activeTab, tabTop,
            0, 1,
                tabWidth, 18,
            texWidth, texHeight
        );

        if(activeTab == 0){

            // Если самый левый край

            AbstractGui.blit(
                pMatrixStack,
                tabLeft, tabTop,
                148, 0,
                5, 19,
                texWidth, texHeight
            );
        }
        else {

            // Если самый правый край

            AbstractGui.blit(
                pMatrixStack,
                tabLeft + (tabWidth * (activeTab + 1)) - 5, tabTop,
                153, 0,
                5, 19,
                texWidth, texHeight
            );
        }
    }

    public ScreenBlueprintCreator.EnumFileFormat getFileFormatOption() {
        return fileFormatOption.getSelected();
    }

    public String getRegistryID() {
        return registryID.getValue();
    }

    public String getName() {
        return nameField.getValue();
    }

    @Override
    public void readData(CompoundNBT data) {

        cacheName = NBTUtils.getString(data, R.BlueprintTemplate.NAME, Config.CLIENT.name.get());
        cacheRegID = NBTUtils.getString(data, R.BlueprintTemplate.REGISTRATION_ID, Config.CLIENT.namespace.get());
        cacheAuthor = NBTUtils.getString(data, R.BlueprintTemplate.AUTHOR, Config.CLIENT.author.get());
        cacheMode = ValidatorMode.getValidator(new ResourceLocation(data.getString(R.BlueprintTemplate.VALIDATOR_MODE)));

        if(data.contains(R.Properties.NUMBER_OF_USE, NBTTypes.INT)){
            useOfNumber.setNumber(data.getInt(R.Properties.NUMBER_OF_USE));
        }

        if(cacheMode == null){
            cacheMode = ValidatorMode.CONST;
        }
        cacheRarity = EnumNBT.deserialize(
            data, R.BlueprintTemplate.RARITY,
            Blueprint.Rarity.SIMPLE,
            Blueprint.Rarity.class
        );
        cacheFileFormat = EnumNBT.deserialize(
            data, R.BlueprintTemplate.FILE_FORMAT,
            Config.CLIENT.fileFormat.get(),
            ScreenBlueprintCreator.EnumFileFormat.class
        );
    }

    @Override
    public void onSwitchScreen() {
        cacheName = nameField.getValue();
        cacheRegID = registryID.getValue();
        cacheAuthor = authorName.getValue();
        cacheMode = modeOption.getSelected();
        cacheRarity = rarityOption.getSelected();
        cacheFileFormat = fileFormatOption.getSelected();
    }

    @Override
    public void saveData(CompoundNBT fileIn) {

        fileIn.putInt(R.Properties.NUMBER_OF_USE, useOfNumber.getNumber());
        fileIn.putString(R.BlueprintTemplate.AUTHOR, authorName.getValue());
        fileIn.putString(R.BlueprintTemplate.NAME, nameField.getValue());
        fileIn.putString(R.BlueprintTemplate.REGISTRATION_ID, registryID.getValue());
        fileIn.putString(R.BlueprintTemplate.VALIDATOR_MODE, modeOption.getSelected().getSerializer().getRegistryName().toString());
        fileIn.put(R.BlueprintTemplate.RARITY, EnumNBT.serialize(rarityOption.getSelected()));
        fileIn.put(R.BlueprintTemplate.FILE_FORMAT, EnumNBT.serialize(fileFormatOption.getSelected()));
    }

    @Override
    public void serializeTemplateStructure(@Nonnull TemplateFileStructure fileStructure) {

        fileStructure.author = authorName.getValue();
        fileStructure.rarity = rarityOption.getSelected();

        if(useOfNumber.getNumber() != Blueprint.Properties.INFINITY){
            fileStructure.properties = new Blueprint.Properties(useOfNumber.getNumber(), EnumCondition.WHOLE);
        }

        calculatePosData(WorldRenderer.getPoints(), fileStructure);
    }

    protected final void calculatePosData(@Nonnull List<FileStructureCreator.PointTemplateData> list, TemplateFileStructure fileStructure){
        int
            maxZ = list.get(0).getMax().getZ(),
            maxY = list.get(0).getMax().getY(),
            maxX = list.get(0).getMax().getX(),
            minZ = list.get(0).getMin().getZ(),
            minY = list.get(0).getMin().getY(),
            minX = list.get(0).getMin().getX()
        ;

        for(int i = 1 ; i < list.size(); i++){
            if(list.get(i).getMax().getZ() > maxZ) maxZ = list.get(i).getMax().getZ();
            if(list.get(i).getMax().getY() > maxY) maxY = list.get(i).getMax().getY();
            if(list.get(i).getMax().getX() > maxX) maxX = list.get(i).getMax().getX();
            if(list.get(i).getMin().getZ() < minZ) minZ = list.get(i).getMin().getZ();
            if(list.get(i).getMin().getY() < minY) minY = list.get(i).getMin().getY();
            if(list.get(i).getMin().getX() < minX) minX = list.get(i).getMin().getX();
        }

        fileStructure.size = new Vector3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        fileStructure.minPos = new BlockPos(minX, minY, minZ);
        fileStructure.maxPos = new BlockPos(maxX, maxY, maxZ);
    }

    protected class ScreenElement implements SimpleScrollRect.IElement {

        private final ScreenConstructor element;
        private final ResourceLocation id;

        protected ScreenElement(ScreenConstructor element, ResourceLocation id){
            this.element = element;
            this.id = id;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            parent.addScreen(element.of(parent, id));
            return true;
        }

        @Override
        public void render(MatrixStack matrixStack, int entryRight, int relativeY, int left, double offset, Tessellator tess, int mouseX, int mouseY) {
            if(offset != -1){
                font.drawShadow(matrixStack,
                    "+ " + getLocalName(id,"name"),
                    left + 2f, relativeY, 0x00CC00
                );
            }
            else {
                font.drawShadow(matrixStack,
                    getLocalName(id,"name"),
                    left + 2f, relativeY, 0xFFFFFF
                );
            }
        }

        public ScreenConstructor getElement() {
            return element;
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}