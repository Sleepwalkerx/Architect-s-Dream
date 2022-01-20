package sleepwalker.architectsdream.client.gui.blueprint_creator.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.BaseCustomScreen;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.CustomScreenCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.FileStructureCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.ValidatorMode;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.WorldRenderer;
import sleepwalker.architectsdream.client.gui.blueprint_creator.widget.SimpleScrollRect;
import sleepwalker.architectsdream.client.gui.widget.ButtonOption;
import sleepwalker.architectsdream.client.gui.widget.IntegerFieldWidget;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.serialize.converters.EnumNBT;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.DataType;
import sleepwalker.architectsdream.structure.EnumCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    protected final Map<DataType, List<ScreenElement>> scrollElements;

    protected final List<DataType> names;

    protected Blueprint.Rarity cacheRarity;
    protected ValidatorMode cacheMode;
    protected ScreenBlueprintCreator.EnumFileFormat cacheFileFormat;

    private int activeTab = 0;
    private int tabLeft, tabTop;

    private static final ResourceLocation TABS = new ResourceLocation(ArchitectsDream.MODID, "textures/gui/tabs.png");

    public MainSettingScreen(ScreenBlueprintCreator parent, ResourceLocation id) {
        super(parent, id, DataType.GENERAL_DATA);

        minecraft = Minecraft.getInstance();
        font = minecraft.font;

        scrollElements = new HashMap<>();
        names = new ArrayList<>();

        createFor(DataType.PALETTE_TYPE);
        createFor(DataType.ENGINE);

        useOfNumber = new IntegerFieldWidget(
            new StringTextComponent("num_of_use"),
            (num) -> num >= -1,
            -1
        );
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
        registryID.setResponder(s -> cacheRegID = s);
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

            cacheName = name;

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
        rarityOption.setPressed(buttonOption -> cacheRarity = buttonOption.getSelected());
        addButton(rarityOption);

        modeOption = new ButtonOption<>(
            getLeftPos() + 119, getTopPos() + 86,
            103, 20,
            "mode",
            cacheMode,
            Lists.newArrayList(ValidatorMode.getValidators())
        );
        modeOption.setPressed(buttonOption -> cacheMode = buttonOption.getSelected());
        addButton(modeOption);

        authorName = new TextFieldWidget(
            font,
            getLeftPos() + 7, getTopPos() + 51,
            100, 12, new StringTextComponent("author_name")
        );
        authorName.setResponder(s -> cacheAuthor = s);
        authorName.setTextColor(-1);
        authorName.setValue(cacheAuthor);
        getChildren().add(authorName);

        fileFormatOption = new ButtonOption<>(
            getLeftPos() + 6, getTopPos() + 86, 103, 20,
            "FileFormat",
            cacheFileFormat,
            ScreenBlueprintCreator.EnumFileFormat.values()
        );
        fileFormatOption.setPressed(buttonOption -> cacheFileFormat = buttonOption.getSelected());
        addButton(fileFormatOption);

        useOfNumber.init(
            font,
            getLeftPos() + 7, getTopPos() + 123,
            80
        );

        scrollRect = new SimpleScrollRect<>(
            minecraft,
            Lists.newArrayList(scrollElements.get(names.get(activeTab))),
            elementHeight,
            137,
            146,
            getLeftPos() + 235,
            getTopPos() + 27
        );

        getChildren().add(scrollRect);
    }

    private void createFor(DataType type){

        List<ScreenElement> elements = new ArrayList<>();

        ScreenBlueprintCreator.REGISTRY.forEach((location, pair) -> {

            if(pair.getKey() == type){
                elements.add(new ScreenElement(pair.getValue(), location, type));
            }
        });

        scrollElements.put(type, elements);

        names.add(type);
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
                scrollRect.swapElements(scrollElements.get(names.get(activeTab)));
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


        for(int i = 0; i < names.size(); i++){

            AbstractGui.drawCenteredString(
                matrixStack,
                font,
                names.get(i).getDisplayName(),
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
        return cacheFileFormat;
    }

    public String getRegistryID() {
        return cacheRegID;
    }

    public String getName() {
        return cacheName;
    }

    @Override
    public void readData(@Nullable CompoundNBT fileIn) {

        if(fileIn == null){

            cacheName = Config.CLIENT.name.get();
            cacheRegID = Config.CLIENT.namespace.get();
            cacheAuthor = Config.CLIENT.author.get();
            cacheMode = ValidatorMode.CONST;
            cacheRarity = Blueprint.Rarity.SIMPLE;
            cacheFileFormat = Config.CLIENT.fileFormat.get();
        }
        else {

            cacheName = fileIn.getString(R.BlueprintCreator.NAME);
            cacheRegID = fileIn.getString(R.BlueprintCreator.REGISTRATION_ID);
            cacheAuthor = fileIn.getString(R.BlueprintCreator.AUTHOR);
            cacheMode = ValidatorMode.getValidator(new ResourceLocation(fileIn.getString(R.BlueprintCreator.VALIDATOR_MODE)));

            if(cacheMode == null){
                cacheMode = ValidatorMode.CONST;
            }

            useOfNumber.setNumber(fileIn.getInt(R.Properties.NUMBER_OF_USE));

            cacheRarity = EnumNBT.deserialize(fileIn, R.BlueprintCreator.RARITY, Blueprint.Rarity.SIMPLE, Blueprint.Rarity.class);
            cacheFileFormat = EnumNBT.deserialize(fileIn, R.BlueprintCreator.FILE_FORMAT, Config.CLIENT.fileFormat.get(), ScreenBlueprintCreator.EnumFileFormat.class);
        }
    }

    @Override
    public void saveData(@Nonnull CompoundNBT fileIn) {

        fileIn.putInt(R.Properties.NUMBER_OF_USE, useOfNumber.getNumber());
        fileIn.putString(R.BlueprintCreator.AUTHOR, cacheAuthor);
        fileIn.putString(R.BlueprintCreator.NAME, cacheName);
        fileIn.putString(R.BlueprintCreator.REGISTRATION_ID, cacheRegID);
        fileIn.putString(R.BlueprintCreator.VALIDATOR_MODE, cacheMode.getSerializer().getRegistryName().toString());
        fileIn.put(R.BlueprintCreator.RARITY, EnumNBT.serialize(cacheRarity));
        fileIn.put(R.BlueprintCreator.FILE_FORMAT, EnumNBT.serialize(cacheFileFormat));
    }

    @Override
    public void serializeTemplateStructure(@Nonnull TemplateFileStructure fileStructure) {

        fileStructure.author = cacheAuthor;
        fileStructure.rarity = cacheRarity;

        if(useOfNumber.getNumber() != Blueprint.Properties.INFINITY){
            fileStructure.properties = new Blueprint.Properties(useOfNumber.getNumber(), EnumCondition.WHOLE);
        }

        if(WorldRenderer.getPoints().isEmpty()){
            throw new IllegalArgumentException("The structure must have a size and starting points");
        }

        calculatePosData(WorldRenderer.getPoints(), fileStructure);
    }

    public final void calculatePosData(@Nonnull List<FileStructureCreator.PointTemplateData> list, TemplateFileStructure fileStructure){
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

        fileStructure.size = new UVector3i(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        fileStructure.minPos = new BlockPos(minX, minY, minZ);
        fileStructure.maxPos = new BlockPos(maxX, maxY, maxZ);
    }

    protected class ScreenElement implements SimpleScrollRect.IElement {

        private final CustomScreenCreator element;
        private final DataType type;
        private final ResourceLocation id;

        protected ScreenElement(CustomScreenCreator element, ResourceLocation id, DataType type){
            this.element = element;
            this.id = id;
            this.type = type;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            parent.addScreen(element.of(parent, id, type));
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

        public DataType getType() {
            return type;
        }

        public CustomScreenCreator getElement() {
            return element;
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}