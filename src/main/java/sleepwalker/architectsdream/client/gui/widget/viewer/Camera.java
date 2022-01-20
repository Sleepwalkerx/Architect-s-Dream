package sleepwalker.architectsdream.client.gui.widget.viewer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import sleepwalker.architectsdream.client.ISavable;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.structure.RenderProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Camera implements ISavable {

    private float fov = 50.0F;

    private final RenderProperty.Data data = RenderProperty.Data.create();

    private float rendFov, rendZoom, rendX, rendY, rendYaw, rendPitch;
    private float rendFovPrev, rendZoomPrev, rendXPrev, rendYPrev, rendYawPrev, rendPitchPrev;

    private float zoomFactor;

    public static final float
        SPEED_ROTATION = 0.8855f,
        SPEED_ZOOM = 0.00445f,
        SPEED_MOTION = 0.01551f
    ;

    public Camera(){
        rendFov = rendFovPrev = fov;
    }

    public void init(@Nonnull UVector3i size, @Nonnull RenderProperty property){

        zoomFactor = (float) Minecraft.getInstance().getWindow().getGuiScale() * (1440f / Minecraft.getInstance().getWindow().getHeight());

        data.setAll(property);

        if(property.getZoom() == RenderProperty.Data.DEFAULT_ZOOM){

            int max = Lists.newArrayList(size.getX(), size.getY(), size.getZ()).stream().mapToInt(value -> value).max().getAsInt();

            data.zoom = (1f / max) * 0.5f;
        }

        update();
    }

    private void update(){
        rendPitch = rendPitchPrev = data.pitch;
        rendX = rendXPrev = data.x;
        rendY = rendYPrev = data.y;
        rendYaw = rendYawPrev = data.yaw;
        rendZoom = rendZoomPrev = data.zoom;
    }

    public void tick() {

        this.rendFovPrev = this.rendFov;
        this.rendZoomPrev = this.rendZoom;
        this.rendXPrev = this.rendX;
        this.rendYPrev = this.rendY;
        this.rendYawPrev = this.rendYaw;
        this.rendPitchPrev = this.rendPitch;

        this.rendFov += (this.fov - this.rendFov);
        this.rendZoom += (data.zoom - this.rendZoom);
        this.rendX += (data.x - this.rendX);
        this.rendY += (data.y - this.rendY);
        this.rendYaw += (data.yaw - this.rendYaw);
        this.rendPitch += (data.pitch - this.rendPitch);
    }

    private void correct() {

        if (data.zoom < 0.01F) {
            data.zoom = 0.01F;
        } else if (data.zoom > 0.4F) {
            data.zoom = 0.4f;
        }

        if (this.fov < 1.0F) {
            this.fov = 1.0F;
        } else if (this.fov > 160.0F) {
            this.fov = 160.0F;
        }
    }

    public RenderProperty.Data getData() {
        return data;
    }

    public float getRendFovPrev() {
        return rendFovPrev;
    }

    public float getRendFov() {
        return rendFov;
    }

    public void setupCamera(@Nonnull MatrixStack matrixStack, float partialTick) {

        correct();

        matrixStack.scale(100f, 100f, 100f);
        matrixStack.translate(0.0F, 0.0F, -9.0F);

        float zoom = (rendZoomPrev + (rendZoom - rendZoomPrev) * partialTick) * zoomFactor;

        float posX = rendXPrev + (rendX - rendXPrev) * partialTick;
        float posY = rendYPrev + (rendY - rendYPrev) * partialTick;

        matrixStack.translate(-posX / 2, -posY / 2, 0.0F);

        matrixStack.scale(zoom, zoom, zoom);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.translate(posX, posY, 0.0F);

        matrixStack.mulPose(new Quaternion(new Vector3f(1f, 0, 0),  rendPitchPrev + (rendPitch - rendPitchPrev) * partialTick + 180.0F, true));
        matrixStack.mulPose(new Quaternion(new Vector3f(0f, 1f, 0), rendYawPrev + (rendYaw - rendYawPrev) * partialTick, true));
    }

    @Nonnull
    @Override
    public INBT saveData() {
        return data.saveData();
    }

    @Override
    public void readData(@Nullable INBT nbt) {

        data.readData(nbt);

        update();
    }
}
