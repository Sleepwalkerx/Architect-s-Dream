package sleepwalker.architectsdream.client.gui.blueprint_viewer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.ISavable;
import sleepwalker.architectsdream.client.ISavableHandler;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoGroup;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModelProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.engine.IEngineProvider;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.SliderStructureViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowInfoPanel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowStructureViewer;
import sleepwalker.architectsdream.network.PacketBlueprintToServer;
import sleepwalker.architectsdream.network.PacketHandler;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ScreenBlueprintViewer extends ContainerScreen<ContainerBlueprintViewer> implements ISavableHandler, INestedGuiEventHandler {

   private final Map<ResourceLocation, ISavable> savableObjects = new HashMap<>();


   private static final ResourceLocation
           BACKGROUND = new ResourceLocation(ArchitectsDream.MODID,"textures/gui/blueprint.png")
   ;

   public static final Map<ResourceLocation, IModelProvider> MODEL_PROVIDERS = Maps.newHashMap();

   public static final Map<IEngineSerializer<?>, IEngineProvider> ENGINES_PROVIDERS = Maps.newHashMap();

   private final String displayAuthor, displayRarity, displayEngineType;

   private String rarityName = "";

   private int rarityColor = TextFormatting.WHITE.getColor();

   private String displayEngineDo = IEngineProvider.DEFAULT.getEngineTypeDo();


   //============

   private static ScreenBlueprintViewer activeViewer;

   private final WindowStructureViewer structureViewer;

   private final WindowInfoPanel infoPanel;

   private SliderStructureViewer structLevel;

   private final Collection<IWindow> windows = new ArrayList<>();

   public ScreenBlueprintViewer(ContainerBlueprintViewer screenContainer, PlayerInventory inv, ITextComponent titleIn) {

      super(screenContainer, inv, titleIn);

      activeViewer = this;

      displayAuthor = screenI18n("blueprint_author", "name") + ':';
      displayRarity = screenI18n("blueprint_rarity", "name") + ':';
      displayEngineType = screenI18n("blueprint_engine_type", "name") + ':';

      font = Minecraft.getInstance().font;

      imageWidth = 418;
      imageHeight = 232;

      infoPanel = new WindowInfoPanel();
      structureViewer = new WindowStructureViewer();

      setDragging(true);

      initData();
   }

   @Nonnull
   public static String screenI18n(String tx1, String tx2){
      return I18n.get(String.format("screen.%s.%s.%s.%s", ArchitectsDream.MODID, R.ScreenName.SCREEN_BLUEPRINT_VIEWER, tx1, tx2));
   }

   @Nonnull
   public static String screenI18n(String tx1, String tx2, String tx3){
      return I18n.get(String.format("screen.%s.%s.%s.%s.%s", ArchitectsDream.MODID, R.ScreenName.SCREEN_BLUEPRINT_VIEWER, tx1, tx2, tx3));
   }

   private void initData(){

      structureViewer.prepare(getBlueprint().getStructure().getSize());

      initBlueprint();
   }

   public static ScreenBlueprintViewer activeViewer(){
      return activeViewer;
   }

   public void loadBlueprint(@Nonnull Blueprint blueprint){

      getMenu().blueprint = blueprint;

      initData();

      init();
   }

   protected void initBlueprint(){

      Blueprint.Structure structure = getMenu().blueprint.getStructure();

      structure.getBasePlacementData().getValidators().forEach(
           (type, validators) -> initType(MODEL_PROVIDERS.get(type), validators)
      );

      ITextComponent rarityName = structure.getRarity().getDisplayName();

      this.rarityName = rarityName.getString();

      rarityColor = rarityName.getStyle().getColor() == null ? 0xffffff : rarityName.getStyle().getColor().getValue();

      initEngine(structure);

      structureViewer.commitModels();
   }

   private void initEngine(Blueprint.Structure structure){

      for(Map.Entry<IEngineSerializer<?>, IEngineProvider> entry : ENGINES_PROVIDERS.entrySet()){

         if(entry.getKey() == structure.getEngine().getSerialize()){

            displayEngineDo = entry.getValue().getEngineTypeDo();

            entry.getValue().create();

            return;
         }
      }
   }

   @Override
   public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
      setFocused(null);
      return super.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
   }

   private void initType(
        @Nonnull IModelProvider provider,
        @Nonnull Set<IValidator> set
   ){

      List<IModel> models = new ArrayList<>();

      for(IValidator validator : set){

         validator.getEntities().forEach((blockPos, t) -> {

            IModel model = provider.createModel(t, blockPos);

            models.add(model);

            structureViewer.addModel(model);
         });
      }

      IInfoGroup group = provider.createGroup(models);

      if(group != null){

         group.build();

         infoPanel.addInfoGroup(group);
      }
   }

   public WindowInfoPanel getInfoPanel() {
      return infoPanel;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return (getFocused() != null && getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
              || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   @Nonnull
   @Override
   public Map<ResourceLocation, ISavable> getSavableObjects() {
      return savableObjects;
   }

   @Override
   protected void init() {

      activeViewer = this;

      super.init();

      windows.clear();
      savableObjects.clear();

      initWindow(structureViewer, leftPos + 113, topPos + 28, 192, 192);
      structureViewer.init(getBlueprint().getRenderProperty());

      initWindow(infoPanel, leftPos, topPos, width, height);
      infoPanel.init();

      structLevel = new SliderStructureViewer(structureViewer,
           leftPos + 315,
           topPos + 204,
           75, 20
      );

      addButton(structLevel);
      addSavableObject(structLevel);

      readSavableObjects(getMenu().itemBlueprint.getOrCreateTag());
   }

   private void initWindow(@Nonnull IWindow window, int x, int y, int width, int height){

      window.initGuiElement(x, y, height, width);

      children.add(window);
      windows.add(window);
   }

   public Blueprint getBlueprint(){
      return getMenu().blueprint;
   }

   @Override
   public void resize(@Nonnull Minecraft minecraft, int width, int height) {
      saveSavableObjects(getMenu().itemBlueprint.getOrCreateTag());
      super.resize(minecraft, width, height);
   }

   @Override
   public void tick() {

      structureViewer.tick();

      super.tick();
   }

   @Override
   public void removed() {

      CompoundNBT compoundNBT = saveSavableObjects(getMenu().itemBlueprint.getOrCreateTag());

      PacketHandler.INSTANCE.sendToServer(new PacketBlueprintToServer(
            getMenu().handIn,
            compoundNBT
      ));

      activeViewer = null;

      super.removed();
   }

   @Override
   protected void renderLabels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {

      rendFont(matrixStack, displayRarity, rarityName, rarityColor, 136f);

      rendFont(matrixStack, displayAuthor, getBlueprint().getStructure().getAuthor(), 0x9f9f9f, 153f);

      rendFont(matrixStack, displayEngineType, displayEngineDo, 0x9f9f9f, 170f);

      windows.forEach(iWindow -> iWindow.renderLabel(matrixStack, mouseX, mouseY));
   }

   private void rendFont(MatrixStack stack, String title, @Nonnull String name, int color, float y){

      int titleWidth = font.width(title);

      font.draw(stack, title, 315f, y,0x3b3b3b);

      if(font.width(name) + titleWidth > 100){

         stack.pushPose();
         stack.translate((317f + titleWidth) * 0.3f, (y + (font.lineHeight / 2f))  * 0.3f, 0);
         stack.scale(0.7f, 0.7f, 1);
         font.draw(stack, name, 317f + titleWidth, y, color);
         stack.popPose();
      }
      else {

         font.draw(stack, name, 317f + titleWidth, y, color);
      }
   }

   @Override
   public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {

      renderBackground(matrixStack);

      super.render(matrixStack, mouseX, mouseY, partialTick);

      infoPanel.render(matrixStack, mouseX, mouseY, partialTick);
      structureViewer.render(matrixStack, mouseX, mouseY, partialTick);
   }

   @Override
   protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {

      RenderSystem.blendColor(1.0f, 1.0f, 1.0f, 1.0f);

      minecraft.getTextureManager().bind(BACKGROUND);

      blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
   }
}