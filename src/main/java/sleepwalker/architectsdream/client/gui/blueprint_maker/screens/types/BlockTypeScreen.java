package sleepwalker.architectsdream.client.gui.blueprint_maker.screens.types;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_maker.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen.BaseCustomScreen;
import sleepwalker.architectsdream.client.gui.blueprint_maker.utils.FileStructureCreator;
import sleepwalker.architectsdream.client.gui.blueprint_maker.utils.WorldRenderer;
import sleepwalker.architectsdream.client.gui.blueprint_maker.widget.SimpleScrollRect;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class BlockTypeScreen extends BaseCustomScreen {

    private SimpleScrollRect<BlockElement> scrollListBlocks;

    private SimpleScrollRect<BlockTagElement> scrollTags;

    private final int elementHeight = Minecraft.getInstance().font.lineHeight + 23;
    private final List<BlockElement> blocks;

    private final Set<BlockPos> cacheAirPositions;

    private CheckboxButton checkboxConsiderAir;
    private boolean cacheCheckboxConsider;

    private final ItemRenderer itemRenderer;
    private final FontRenderer font;
    private final Minecraft mc;

    private static final String
        PROPERTY_MODE = "property_mode",
        BLOCK_NAME = "block_name",
        TAGS_LIST = "tags",
        DATA_LIST = "data_blocks",
        CONSIDER_AIR = "consider_air"
    ;

    public static final String ID = "block_type";

    public BlockTypeScreen(ScreenBlueprintCreator parent, ResourceLocation id) {
        super(parent, id);

        this.mc = Minecraft.getInstance();
        this.itemRenderer = mc.getItemRenderer();
        font = mc.font;

        if(!WorldRenderer.getPoints().isEmpty()){

            blocks = Lists.newArrayList();
            cacheAirPositions = Sets.newHashSet();
            World world = getPlayerInventory().player.level;

            WorldRenderer.getPoints().forEach(pointData ->
                FileStructureCreator.WorldEnumerator.of(pointData.getMax(), pointData.getMin()).forEach(pos -> {
                    if(blocks.stream().anyMatch(blockElement -> blockElement.typeElements.containsKey(pos))){
                        return;
                    }

                    BlockState blockState = world.getBlockState(pos);

                    if(blockState.getBlock() == Blocks.AIR){
                        cacheAirPositions.add(pos);
                        return;
                    }

                    for(BlockElement element : blocks){
                        if(element.block.getBlock() == blockState.getBlock()){
                            element.typeElements.put(pos, blockState);
                            return;
                        }
                    }

                    Map<BlockPos, BlockState> map = Maps.newHashMapWithExpectedSize(1);

                    map.put(pos, blockState);

                    blocks.add(new BlockElement(map));
                })
            );
        }
        else {
            blocks = Collections.emptyList();
            cacheAirPositions = Collections.emptySet();
        }
    }

    @Override
    public void init() {

        scrollListBlocks = new SimpleScrollRect<>(
            mc, blocks, elementHeight,
            200, 173,
            5 + getLeftPos(), 5 + getTopPos()
        );
        getChildren().add(scrollListBlocks);

        checkboxConsiderAir = new CheckboxButton(
            getLeftPos() + 210, getTopPos() + 5,
            20, 20,
            new StringTextComponent(ScreenBlueprintCreator.i18n(ID, "consider_air.name")),
            cacheCheckboxConsider
        );

        scrollTags = new SimpleScrollRect<>(
                mc, new ArrayList<>(), 9,
                168, 100,
                getLeftPos() + 210, getTopPos() + 78
        );
        getChildren().add(scrollListBlocks);

        addButton(checkboxConsiderAir);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return scrollListBlocks.mouseClicked(pMouseX, pMouseY, pButton) || scrollTags.mouseClicked(pMouseX, pMouseY, pButton) || super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return scrollListBlocks.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) || scrollTags.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {

        scrollListBlocks.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        scrollTags.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
    }

    @Override
    public void readData(CompoundNBT data) {

        ListNBT listNBT = data.getList(DATA_LIST, NBTTypes.OBJECT);

        for(int i = 0; i < listNBT.size() && i < blocks.size(); i++){

            blocks.get(i).readData(listNBT.getCompound(i));
        }

        cacheCheckboxConsider = data.contains(CONSIDER_AIR) ? data.getBoolean(CONSIDER_AIR) : Config.CLIENT.considerAir.get();
    }

    @Override
    public void saveData(CompoundNBT fileIn) {

        ListNBT listNBT = new ListNBT();

        for(BlockElement element : blocks){
           listNBT.add(element.saveData());
        }

        fileIn.put(DATA_LIST, listNBT);

        if(checkboxConsiderAir != null && checkboxConsiderAir.selected()){
            fileIn.putBoolean(CONSIDER_AIR, true);
        }
    }

    @Override
    public void serializeTemplateStructure(TemplateFileStructure fileStructure) {

        if(blocks.size() == 0) return;

        TemplateFileStructure.Entity entity = new TemplateFileStructure.Entity();

        entity.palette = Lists.newArrayList();
        entity.validators = Maps.newHashMap();
        entity.id = R.BlockContainer.NAME;

        for(BlockElement blockElement : blocks){

            ContainerTypeBlock.Properties properties = new ContainerTypeBlock.Properties();
            NonNullList<ContainerTypeBlock.BlockComparator> comparators = NonNullList.create();

            if(!blockElement.tags.isEmpty()){

                Map<ResourceLocation, ITag<Block>> map = new HashMap<>();

                blockElement.tags.forEach(blockTagElement -> {

                    if(blockTagElement.isActive()){
                        map.put(blockTagElement.getName(), blockTagElement.getTag());
                    }
                });

                if(!map.isEmpty()){

                    properties.setTags(new ContainerTypeBlock.BlockTagCollection(map));

                    comparators.add(ContainerTypeBlock.TAG);
                }
            }

            if(blockElement.mode != PropertyMode.IGNORE){

                List<BlockState> listStates = new ArrayList<>();
                comparators.add(ContainerTypeBlock.BLOCK_STATE);

                blockElement.typeElements.forEach((blockPos, blockState) -> {

                    int index = listStates.indexOf(blockState);

                    if(index == -1){
                        index = entity.palette.size();
                        listStates.add(blockState);

                        ContainerTypeBlock.Properties properties1 = new ContainerTypeBlock.Properties(properties);
                        properties1.setBlockState(blockState, blockElement.mode == PropertyMode.CONSIDER);
                        entity.palette.add(new ContainerTypeBlock(comparators, properties1));
                    }
                    else index += entity.palette.size() - listStates.size();

                    IValidatorSerializer serializer = FileStructureCreator.getSerialize(blockPos);
                    Map<BlockPos, Integer> map = entity.validators.get(serializer);
                    if(map == null){
                        map = new HashMap<>();
                        map.put(convertPos(blockPos, fileStructure), index);
                        entity.validators.put(serializer, map);
                    }
                    else {
                        map.put(convertPos(blockPos, fileStructure), index);
                    }
                });
            }
            else {

                final int index = entity.palette.size();

                properties.setBlockState(blockElement.block, false);
                entity.palette.add(new ContainerTypeBlock(comparators, properties));

                blockElement.typeElements.forEach(((blockPos, blockState) -> {
                    IValidatorSerializer serializer = FileStructureCreator.getSerialize(blockPos);
                    Map<BlockPos, Integer> map = entity.validators.get(serializer);
                    if(map == null){
                        map = new HashMap<>();
                        map.put(convertPos(blockPos, fileStructure), index);
                        entity.validators.put(serializer, map);
                    }
                    else {
                        map.put(convertPos(blockPos, fileStructure), index);
                    }
                }));
            }
        }

        fileStructure.entities.add(entity);
    }

    @Nonnull
    private BlockPos convertPos(@Nonnull BlockPos pos, @Nonnull TemplateFileStructure fileStructure){
        return pos.subtract(fileStructure.minPos);
    }

    protected class BlockElement implements SimpleScrollRect.IElement {

        private final Map<BlockPos, BlockState> typeElements;

        private final ItemStack itemStack;
        private final BlockState block;
        private final boolean canChangePropertyMode;

        private final List<BlockTagElement> tags;

        private PropertyMode mode;

        public BlockElement(@Nonnull Map<BlockPos, BlockState> typeElements){
            this.typeElements = typeElements;
            this.block = typeElements.entrySet().iterator().next().getValue();

            Collection<ResourceLocation> tags = BlockTags.getAllTags().getMatchingTags(block.getBlock());

            if(tags.isEmpty()){

                this.tags = Collections.emptyList();
            }
            else {

                this.tags = new ArrayList<>(tags.size());

                for(ResourceLocation location : tags){

                    this.tags.add(new BlockTagElement(BlockTags.getAllTags().getTag(location), location));
                }
            }

            if(block.getProperties().isEmpty()){

                canChangePropertyMode = false;

                mode = PropertyMode.IGNORE;
            }
            else {

                canChangePropertyMode = true;

                mode = PropertyMode.CONSIDER_FOR_SCHEME;
            }

            this.itemStack = new ItemStack(block.getBlock(), typeElements.size());
        }

        @Override
        public void render(MatrixStack matrixStack, int entryRight, int relativeY, final int left, double offset, Tessellator tess, int mouseX, int mouseY) {

            itemRenderer.renderGuiItem(itemStack, left + 3, relativeY);

            font.drawShadow(matrixStack,
                "x" + typeElements.size(),
                left + 23f, relativeY + -1f, 0xFFFFFF
            );

            if(canChangePropertyMode){
                font.drawShadow(matrixStack,
                    "property:",
                    left + 23f, relativeY + 9f, 0xFFFFFF
                );
                font.drawShadow(matrixStack,
                    mode.name(),
                    left + 75f, relativeY + 9f,
                    TextFormatting.YELLOW.getColor()
                );
            }

            if(!tags.isEmpty()){
                font.drawShadow(
                    matrixStack,
                    "tag",
                    left + 23f, relativeY + 18f, 0xFFFFFF
                );
            }
        }

        protected CompoundNBT saveData(){

            CompoundNBT compoundNBT = new CompoundNBT();

            compoundNBT.putString(BLOCK_NAME, block.getBlock().getRegistryName().toString());

            compoundNBT.putString(PROPERTY_MODE, mode.name());

            ListNBT listNBT = new ListNBT();

            tags.forEach(blockTagElement -> listNBT.add(blockTagElement.saveData()));

            compoundNBT.put(TAGS_LIST, listNBT);

            return compoundNBT;
        }

        protected void readData(@Nonnull CompoundNBT compoundNBT){

            ResourceLocation location = new ResourceLocation(compoundNBT.getString(BLOCK_NAME));

            if(!block.getBlock().getRegistryName().equals(location)){
                return;
            }

            if(canChangePropertyMode){

                try {
                    mode = PropertyMode.valueOf(compoundNBT.getString(PROPERTY_MODE));
                }
                catch (IllegalArgumentException ignored){ }
            }

            ListNBT listNBT = compoundNBT.getList(TAGS_LIST, NBTTypes.INT);

            for(int i = 0; i < listNBT.size() && i < tags.size(); i++){

                tags.get(i).readData(listNBT.getInt(i));
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {

            if(scrollListBlocks.getActiveElement().getValue() % elementHeight < font.lineHeight + 15) {

                mode = PropertyMode.values()[((mode.ordinal() + 1) % PropertyMode.values().length)];
            }
            else if(!tags.isEmpty()) {

                scrollTags.swapElements(tags);
            }
            return true;
        }
    }

    public enum PropertyMode {
        CONSIDER_FOR_SCHEME,
        CONSIDER,
        IGNORE
    }
}