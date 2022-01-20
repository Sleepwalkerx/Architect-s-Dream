package sleepwalker.architectsdream.structure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class RenderProperty {

    public static final RenderProperty DEFAULT = Data.create().build();

    private final float pitch, yaw, zoom, x, y;

    public RenderProperty(float pitch, float yaw, float zoom, float x, float y) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.zoom = zoom;
        this.x = x;
        this.y = y;
    }

    public float getZoom() {
        return zoom;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderProperty that = (RenderProperty) o;
        return Float.compare(that.pitch, pitch) == 0 && Float.compare(that.yaw, yaw) == 0 && Float.compare(that.zoom, zoom) == 0 && Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pitch, yaw, zoom, x, y);
    }

    public static class Data {

        public static float
            DEFAULT_ZOOM = 0.0831F
        ;

        public float
            pitch = -28f,
            yaw = -39f,
            zoom = DEFAULT_ZOOM,
            x = 0.0f,
            y = 0.0f
        ;

        @Nonnull
        public static Data create(){
            return new Data();
        }

        @Nonnull
        public RenderProperty build(){
            return new RenderProperty(pitch, yaw, zoom, x, y);
        }

        public void setAll(@Nonnull RenderProperty property){
            pitch = property.pitch;
            yaw = property.yaw;
            zoom = property.zoom;
            x = property.x;
            y = property.y;
        }

        public void readData(@Nullable INBT nbt) {

            if(nbt != null){

                CompoundNBT compoundNBT = (CompoundNBT) nbt;

                if(compoundNBT.contains(R.RenderProperty.ZOOM, NBTTypes.FLOAT)){
                    zoom = compoundNBT.getFloat(R.RenderProperty.ZOOM);
                }

                if(compoundNBT.contains(R.RenderProperty.PITCH, NBTTypes.FLOAT)){
                    pitch = compoundNBT.getFloat(R.RenderProperty.PITCH);
                }

                if(compoundNBT.contains(R.RenderProperty.YAW, NBTTypes.FLOAT)){
                    yaw = compoundNBT.getFloat(R.RenderProperty.YAW);
                }

                if(compoundNBT.contains(R.RenderProperty.X, NBTTypes.FLOAT)){
                    x = compoundNBT.getFloat(R.RenderProperty.X);
                }

                if(compoundNBT.contains(R.RenderProperty.Y, NBTTypes.FLOAT)){
                    y = compoundNBT.getFloat(R.RenderProperty.Y);
                }
            }
        }

        @Nonnull
        public INBT saveData() {

            CompoundNBT compoundNBT = new CompoundNBT();

            compoundNBT.putFloat(R.RenderProperty.ZOOM, zoom);
            compoundNBT.putFloat(R.RenderProperty.PITCH, pitch);
            compoundNBT.putFloat(R.RenderProperty.YAW, yaw);
            compoundNBT.putFloat(R.RenderProperty.X, x);
            compoundNBT.putFloat(R.RenderProperty.Y, y);

            return compoundNBT;
        }
    }
}
