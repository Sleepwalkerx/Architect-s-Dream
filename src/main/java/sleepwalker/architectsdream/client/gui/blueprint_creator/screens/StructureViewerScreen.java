package sleepwalker.architectsdream.client.gui.blueprint_creator.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.custom_screen.BaseCustomScreen;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModelProvider;
import sleepwalker.architectsdream.client.gui.widget.viewer.Camera;
import sleepwalker.architectsdream.client.gui.widget.viewer.StructureViewer;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.structure.DataType;
import sleepwalker.architectsdream.structure.RenderProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StructureViewerScreen extends BaseCustomScreen {

    private final StructureViewer viewer;

    private final String CAMERA = "camera";

    private boolean structureLoad;

    private LockIconButton lockYawRotation, lockAllTrans;

    private boolean cachePitchLock, cacheAllLock;

    private static final ResourceLocation STRUCTURE_VIEWER_BG = new ResourceLocation(ArchitectsDream.MODID,"textures/gui/screen_viewer_mini.png");

    public StructureViewerScreen(ScreenBlueprintCreator parent, ResourceLocation registrationId) {
        super(parent, registrationId, DataType.RENDER_PROPERTY);

        viewer = new StructureViewer();
    }

    @Override
    public void preSwitchScreen() {
        structureLoad = false;
    }

    public void init(){

        viewer.initGuiElement(parent.getGuiLeft() + (parent.getXSize() / 2) - 87, parent.getGuiTop() + 4, 175, 175);

        // 26
        Button leftSwitch = new Button(
                parent.getGuiLeft() + 4,
                parent.getGuiTop() + 5,
                49, 20,
                new StringTextComponent("<----"),
                this::onSwitchRotationLeft
        );
        addButton(leftSwitch);

        Button rightSwitch = new Button(
                parent.getGuiLeft() + 53,
                parent.getGuiTop() + 5,
                50, 20,
                new StringTextComponent("---->"),
                this::onSwitchRotationRight
        );
        addButton(rightSwitch);

        lockYawRotation = new LockIconButton(
                parent.getGuiLeft() + 4, parent.getGuiTop() + 26, this::onPressedPitchLockButton
        );
        lockYawRotation.setLocked(cachePitchLock);
        addButton(lockYawRotation);

        lockAllTrans = new LockIconButton(
                parent.getGuiLeft() + 4, parent.getGuiTop() + 47, this::onPressedAllLockButton
        );
        lockAllTrans.setLocked(cacheAllLock);
        addButton(lockAllTrans);

        Button resetButton = new Button(
                parent.getGuiLeft() + 4,
                parent.getGuiTop() + 159,
                99, 20,
                new StringTextComponent(i18n("reset.name")),
                this::onPressedResetButton
        );
        addButton(resetButton);

        if(!structureLoad) {

            structureLoad = true;

            initStructure();
        }
    }

    private void onPressedPitchLockButton(Button button){
        lockYawRotation.setLocked(!lockYawRotation.isLocked());
        cachePitchLock = lockYawRotation.isLocked();
    }

    private void onPressedResetButton(Button button){

        viewer.getCamera().getData().setAll(genDefaultValue());
    }

    @Nonnull
    private RenderProperty genDefaultValue(){
        return new RenderProperty(
                Config.CLIENT.pitch.get(),
                Config.CLIENT.yaw.get(),
                Config.CLIENT.zoom.get(),
                Config.CLIENT.x.get(),
                Config.CLIENT.y.get()
        );
    }

    private void onPressedAllLockButton(Button button){
        lockAllTrans.setLocked(!lockAllTrans.isLocked());
        cacheAllLock = lockAllTrans.isLocked();
    }

    private void onSwitchRotationRight(Button button){
        onSwitchRotation(90);
    }

    private void onSwitchRotationLeft(Button button){
        onSwitchRotation(-90);
    }

    private void onSwitchRotation(int value){
        viewer.getCamera().getData().yaw += value;
    }


    private final FontRenderer font = Minecraft.getInstance().font;
    @Override
    public void renderLabels(MatrixStack pMatrixStack, int pX, int pY) {

        font.draw(pMatrixStack, i18n("title_pitch_lock"), 26, 32, 0x727272);
        font.draw(pMatrixStack, i18n("title_all_lock"), 26, 53, 0x727272);

        font.draw(pMatrixStack, "pitch: " + (viewer.getCamera().getData().pitch % 360), 282, 7, 0x727272);
        font.draw(pMatrixStack, "yaw: " + (viewer.getCamera().getData().yaw % 360), 282, 17, 0x727272);
        font.draw(pMatrixStack, "x: " + viewer.getCamera().getData().x, 282, 27, 0x727272);
        font.draw(pMatrixStack, "y: " + viewer.getCamera().getData().y, 282, 37, 0x727272);
    }

    public void initStructure(){

        try {

            TemplateFileStructure structure = new TemplateFileStructure();

            parent.getMainSettingScreen().serializeTemplateStructure(structure);

            parent.getCacheScreens().forEach(screen -> {

                if(screen.getType() == DataType.PALETTE_TYPE){

                    screen.serializeTemplateStructure(structure);
                }
            });

            if(!structure.entities.isEmpty()){

                viewer.prepare(structure.size);

                for(TemplateFileStructure.Entity entity : structure.entities){

                    IModelProvider provider = ScreenBlueprintViewer.MODEL_PROVIDERS.get(entity.id);

                    if(provider == null){
                        continue;
                    }

                    entity.validators.forEach((serializer, indexes) -> {

                        indexes.forEach((pos, index) -> {

                            IModel model = provider.createModel(entity.palette.get(index), pos);

                            viewer.addModel(model);
                        });
                    });
                }

                viewer.commitModels();

                viewer.init(genDefaultValue());

                viewer.getCamera().readData(cameraNBT);
            }
        }
        catch (Exception e){

            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        viewer.tick();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return viewer.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {

        if(!viewer.isMouseOver(pMouseX, pMouseY)){
            return false;
        }

        if(pButton == 0){

            if(!lockYawRotation.isLocked()){
                viewer.getCamera().getData().yaw += pDragX * Camera.SPEED_ROTATION;
            }

            if(!lockAllTrans.isLocked()){
                viewer.getCamera().getData().pitch -= pDragY * Camera.SPEED_ROTATION;
            }
        } else {
            if(!lockAllTrans.isLocked()) {
                viewer.getCamera().getData().x -= pDragX * Camera.SPEED_MOTION;
                viewer.getCamera().getData().y += pDragY * Camera.SPEED_MOTION;
            }
        }

        return true;
    }

    @Override
    public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {

        Minecraft.getInstance().getTextureManager().bind(STRUCTURE_VIEWER_BG);

        AbstractGui.blit(pMatrixStack, viewer.getPosX(), viewer.getPosY(), 175, 175, 0, 0, 175, 175, 175, 175);

        pMatrixStack.pushPose();

        pMatrixStack.translate(0, 19 * Minecraft.getInstance().getWindow().getGuiScale(), 0);

        viewer.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

        pMatrixStack.popPose();
    }

    @Override
    public void serializeTemplateStructure(@Nonnull TemplateFileStructure fileStructure) {

        fileStructure.rend_prop = viewer.getCamera().getData();
    }

    private final String LOCK_YAW = "lock_yaw", LOCK_ALL = "lock_all";
    @Override
    public void saveData(@Nonnull CompoundNBT fileIn) {

        fileIn.putBoolean(LOCK_YAW, cachePitchLock);
        fileIn.putBoolean(LOCK_ALL, cacheAllLock);

        fileIn.put(CAMERA, viewer.getCamera().saveData());
    }

    private CompoundNBT cameraNBT;
    @Override
    public void readData(@Nullable CompoundNBT fileIn) {

        if(fileIn == null){

            cacheAllLock = Config.CLIENT.lockAllTrans.get();

            cachePitchLock = Config.CLIENT.lockPitch.get();

            cameraNBT = null;
        }
        else {

            cacheAllLock = fileIn.getBoolean(LOCK_ALL);

            cachePitchLock = fileIn.getBoolean(LOCK_YAW);

            cameraNBT = fileIn.getCompound(CAMERA);
        }
    }
}
