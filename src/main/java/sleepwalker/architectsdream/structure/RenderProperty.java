package sleepwalker.architectsdream.structure;

public class RenderProperty {

    public static final RenderProperty DEFAULT = new RenderProperty(-13.34155f, -0.34989f);

    protected final float pitch, yaw;

    public RenderProperty(float pitch, float yaw){
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }
}
