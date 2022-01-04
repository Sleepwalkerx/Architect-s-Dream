package sleepwalker.architectsdream.client.gui.blueprint_viewer.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.BaseGuiElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.IWindow;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.EmptyModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.utils.RenderUtils;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class WindowStructureViewer extends BaseGuiElement implements IWindow {

    private IModel[][][] models;

    private final Camera camera = new Camera();

    private final Minecraft mc;

    private UVector3i size;

    private final String displayName;

    private final ScreenBlueprintViewer viewer;

    private final FontRenderer font = Minecraft.getInstance().font;

    public int
        xStartRend,
        yStartRend,
        zStartRend,
        xEndRend,
        yEndRend,
        zEndRend
    ;

    public WindowStructureViewer() {

        mc = Minecraft.getInstance();

        this.viewer = ScreenBlueprintViewer.activeViewer();

        this.displayName = I18n.get(String.format("screen.%s.%s.window_structure_viewer.name", ArchitectsDream.MODID, R.ScreenName.SCREEN_BLUEPRINT_VIEWER));
    }

    public void prepare(UVector3i size){

        models = new IModel[size.getX()][size.getY()][size.getZ()];

        xEndRend = size.getX();
        yEndRend = size.getY();
        zEndRend = size.getZ();

        this.size = size;
    }

    public UVector3i getSize() {
        return size;
    }

    public void commitModels(){

        for(int x = xStartRend; x < xEndRend; x++){
            for(int y = yStartRend; y < yEndRend; y++){
                for(int z = zStartRend; z < zEndRend; z++) {

                    if (models[x][y][z] == null) {
                        models[x][y][z] = EmptyModel.EMPTY;
                    }
                }
            }
        }
    }

    public void addModel(IModel model, BlockPos pos){
        models[pos.getX()][pos.getY()][pos.getZ()] = model;
    }

    public void showModels(IModel... models){

    }

    @Override
    public void setHighlight(boolean value) {

    }

    public void tick(){
        camera.tick();
    }

    @Override
    public void init() {
        camera.init();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {

        double value = delta * camera.speedZoom;

        if(Screen.hasShiftDown()) {
            camera.zoom += value * 0.1f;
        }
        else camera.zoom += value;

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

        if(!isMouseOver(mouseX, mouseY)){
            return false;
        }

        if(button == 0){
            camera.yaw += deltaX * camera.speedRotation;
            camera.pitch -= deltaY * camera.speedRotation;
        } else {
            camera.x -= deltaX * camera.speedMotion;
            camera.y += deltaY * camera.speedMotion;
        }

        return true;
    }

    @Override
    public void renderLabel(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        drawStringCenter(matrixStack, posX - viewer.getGuiLeft() + (width / 2f), posY - viewer.getGuiTop() - font.lineHeight - 5, displayName);
    }

    private void drawStringCenter(@Nonnull MatrixStack matrixStack, float x, float y, String text){
        font.draw(matrixStack, text, x - font.width(text) / 2f, y, 0x666666);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        RenderSystem.multMatrix(Matrix4f.perspective(
                (camera.rendFovPrev + (camera.rendFov - camera.rendFovPrev) * tick),
                (float)mc.getWindow().getWidth() / (float)mc.getWindow().getHeight(),
                4.0F,
                10000F
        ));
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();


        matrixStack.pushPose();

        renderModels(matrixStack, mouseX, mouseY, tick);

        matrixStack.popPose();

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        RenderSystem.ortho(
                0.0D,
                mc.getWindow().getWidth() / mc.getWindow().getGuiScale(),
                mc.getWindow().getHeight() / mc.getWindow().getGuiScale(),
                0.0D,
                -5000.0D,
                5000.0D
        );
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    private void renderModels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick){

        camera.setupCamera(matrixStack, tick);

        RenderHelper.setupForFlatItems();

        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();


        GL11.glEnable(32826);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        RenderUtils.startGlScissor(posX, posY, width + 1, height + 1);

        matrixStack.translate(-size.getX() / 2f, -size.getY() / 2f, -size.getZ() / 2f);

        for(int x = xStartRend; x < xEndRend; x++){
            for(int y = yStartRend; y < yEndRend; y++){
                for(int z = zStartRend; z < zEndRend; z++){

                    IModel model = models[x][y][z];

                    if(!model.isVisible()){
                        continue;
                    }

                    model.render(matrixStack, mouseX, mouseY, tick);
                }
            }
        }

        matrixStack.translate(size.getX() / 2f, size.getY() / 2f, size.getZ() / 2f);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(32826);
    }
}
