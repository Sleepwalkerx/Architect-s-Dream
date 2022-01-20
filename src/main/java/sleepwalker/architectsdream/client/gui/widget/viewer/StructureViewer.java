package sleepwalker.architectsdream.client.gui.widget.viewer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;
import sleepwalker.architectsdream.client.gui.BaseGuiElement;
import sleepwalker.architectsdream.client.gui.IGuiElementEventListener;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.EmptyModel;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.structure.RenderProperty;
import sleepwalker.architectsdream.utils.RenderUtils;

import javax.annotation.Nonnull;

public class StructureViewer extends BaseGuiElement implements IGuiElementEventListener {

    private IModel[][][] models;

    private final Camera camera = new Camera();

    private final Minecraft mc;

    private UVector3i size = UVector3i.ZERO;

    public int
        xStartRend,
        yStartRend,
        zStartRend,
        xEndRend,
        yEndRend,
        zEndRend
    ;

    public StructureViewer() {

        mc = Minecraft.getInstance();
    }

    public void prepare(@Nonnull UVector3i size){

        models = new IModel[size.getX()][size.getY()][size.getZ()];

        xEndRend = size.getX();
        yEndRend = size.getY();
        zEndRend = size.getZ();

        this.size = size;
    }

    public IModel[][][] getModels() {
        return models;
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

    public void addModel(IModel model){
        models[model.getPos().getX()][model.getPos().getY()][model.getPos().getZ()] = model;
    }

    public void showModels(IModel... models){
        // TODO: future update
    }

    public void tick(){
        camera.tick();
    }

    public void init(RenderProperty property) {
        camera.init(size, property);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {

        double value = delta * Camera.SPEED_ZOOM;

        if(Screen.hasShiftDown()) {
            camera.getData().zoom += value * 0.1f;
        }
        else camera.getData().zoom += value;

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {

        if(!isMouseOver(mouseX, mouseY)){
            return false;
        }

        if(button == 0){
            camera.getData().yaw += deltaX * Camera.SPEED_ROTATION;
            camera.getData().pitch -= deltaY * Camera.SPEED_ROTATION;
        } else {
            camera.getData().x -= deltaX * Camera.SPEED_MOTION;
            camera.getData().y += deltaY * Camera.SPEED_MOTION;
        }

        return true;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float tick) {

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        RenderSystem.multMatrix(Matrix4f.perspective(
                (camera.getRendFovPrev() + (camera.getRendFov() - camera.getRendFovPrev()) * tick),
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

    public Camera getCamera() {
        return camera;
    }
}
