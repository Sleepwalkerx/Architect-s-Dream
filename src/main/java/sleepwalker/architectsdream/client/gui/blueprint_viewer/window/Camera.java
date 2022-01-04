package sleepwalker.architectsdream.client.gui.blueprint_viewer.window;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.ISavable;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class Camera implements IGuiEventListener, ISavable {

    float fov = 50.0F;
    float zoom = 0.0831F;
    float x = 0.0F;
    float y = 0.0F;
    float yaw = -0.34989f;
    float pitch = -13.34155f;

    float rendFov;
    float rendZoom;
    float rendX;
    float rendY;
    float rendYaw;
    float rendPitch;
    float rendFovPrev;
    float rendZoomPrev;
    float rendXPrev;
    float rendYPrev;
    float rendYawPrev;
    float rendPitchPrev;

    private float zoomFactor;

    final float
        speedRotation = 0.8855f,
        speedZoom = 0.00445f,
        speedMotion = 0.01551f
    ;

    private static final String
        ZOOM = "zoom",
        PITCH = "pitch",
        YAW = "yaw",
        X = "x",
        Y = "y"
    ;

    Camera() {

        this.rendFov = this.fov;
        this.rendZoom = this.zoom;
        this.rendX = this.x;
        this.rendY = this.y;
        this.rendYaw = this.yaw;
        this.rendPitch = this.pitch;
        this.rendFovPrev = this.rendFov;
        this.rendZoomPrev = this.rendZoom;
        this.rendXPrev = this.rendX;
        this.rendYPrev = this.rendY;
        this.rendYawPrev = this.rendYaw;
        this.rendPitchPrev = this.rendPitch;
    }

    void init(){

        zoomFactor = (float)Minecraft.getInstance().getWindow().getGuiScale() * (1440f / Minecraft.getInstance().getWindow().getHeight());

        ScreenBlueprintViewer.activeViewer().addSavableObject(this);
    }

    void tick() {

        this.rendFovPrev = this.rendFov;
        this.rendZoomPrev = this.rendZoom;
        this.rendXPrev = this.rendX;
        this.rendYPrev = this.rendY;
        this.rendYawPrev = this.rendYaw;
        this.rendPitchPrev = this.rendPitch;

        this.rendFov += (this.fov - this.rendFov);
        this.rendZoom += (this.zoom - this.rendZoom);
        this.rendX += (this.x - this.rendX);
        this.rendY += (this.y - this.rendY);
        this.rendYaw += (this.yaw - this.rendYaw);
        this.rendPitch += (this.pitch - this.rendPitch);
    }

    void correct() {
        if (this.zoom < 0.01F) {
            this.zoom = 0.01F;
        } else if (this.zoom > 0.4F) {
            this.zoom = 0.4f;
        }

        if (this.fov < 1.0F) {
            this.fov = 1.0F;
        } else if (this.fov > 160.0F) {
            this.fov = 160.0F;
        }
    }

    void setupCamera(@Nonnull MatrixStack matrixStack, float partialTick) {

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

        matrixStack.mulPose(new Quaternion(new Vector3f(1f, 0, 0), -15.0F + rendPitchPrev + (rendPitch - rendPitchPrev) * partialTick + 180.0F, true));
        matrixStack.mulPose(new Quaternion(new Vector3f(0f, 1f, 0), -38.0F + rendYawPrev + (rendYaw - rendYawPrev) * partialTick, true));
    }

    @Nonnull
    @Override
    public INBT saveData() {

        CompoundNBT compoundNBT = new CompoundNBT();

        compoundNBT.putFloat(ZOOM, zoom);
        compoundNBT.putFloat(PITCH, pitch);
        compoundNBT.putFloat(YAW, yaw);
        compoundNBT.putFloat(X, x);
        compoundNBT.putFloat(Y, y);

        return compoundNBT;
    }

    @Override
    public void readData(@Nullable INBT nbt) {

        if(nbt == null){

            Blueprint blueprint = ScreenBlueprintViewer.activeViewer().getBlueprint();

            UVector3i size = blueprint.getStructure().getSize();

            int max = Lists.newArrayList(size.getX(), size.getY(), size.getZ()).stream().mapToInt(value -> value).max().getAsInt();

            rendZoomPrev = rendZoom = zoom = (1f / max) * 0.5f;

            rendPitchPrev = rendPitch = pitch = blueprint.getRenderProperty().getPitch();

            rendYawPrev = rendYaw = yaw = blueprint.getRenderProperty().getYaw();
        }
        else {

            CompoundNBT compoundNBT = (CompoundNBT) nbt;

            rendZoomPrev = rendZoom = zoom = compoundNBT.getFloat(ZOOM);

            rendPitchPrev = rendPitch = pitch = compoundNBT.getFloat(PITCH);

            rendYawPrev = rendYaw = yaw = compoundNBT.getFloat(YAW);

            rendXPrev = rendX = x = compoundNBT.getFloat(X);

            rendYPrev = rendY = y = compoundNBT.getFloat(Y);
        }
    }
}
