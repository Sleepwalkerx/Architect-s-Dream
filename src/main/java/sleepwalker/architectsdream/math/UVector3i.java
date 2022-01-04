package sleepwalker.architectsdream.math;

import net.minecraft.util.math.vector.Vector3i;

public class UVector3i extends Vector3i {

    public static final UVector3i ZERO = new UVector3i(0, 0, 0);

    public UVector3i(int x, int y, int z) {
        super(x, y, z);

        if(x < 0 || y < 0 || z < 0){
            throw new IllegalArgumentException("Vector3i can't have negative numbers");
        }
    }

    public UVector3i(double x, double y, double z) {
        super(x, y, z);

        if(x < 0 || y < 0 || z < 0){
            throw new IllegalArgumentException("Vector3i can't have negative numbers");
        }
    }
}
