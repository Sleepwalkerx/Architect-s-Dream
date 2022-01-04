package sleepwalker.architectsdream.math;

import net.minecraft.dispenser.IPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class UBlockPos extends BlockPos {

    public static final UBlockPos ZERO = new UBlockPos(0, 0, 0);

    public UBlockPos(int x, int y, int z) {
        super(x, y, z);

        if(x < 0 || y < 0 || z < 0){
            throw new IllegalArgumentException("UBlockPos can't have negative numbers");
        }
    }

    public UBlockPos(double x, double y, double z) {
        super(x, y, z);

        if(x < 0 || y < 0 || z < 0){
            throw new IllegalArgumentException("UBlockPos can't have negative numbers");
        }
    }

    public UBlockPos(Vector3d pos) {
        super(pos);

        if(pos.x < 0 || pos.y < 0 || pos.z < 0){
            throw new IllegalArgumentException("UBlockPos can't have negative numbers");
        }
    }

    public UBlockPos(IPosition pos) {
        super(pos);

        if(pos.x() < 0 || pos.y() < 0 || pos.z() < 0){
            throw new IllegalArgumentException("UBlockPos can't have negative numbers");
        }
    }

    public UBlockPos(Vector3i pos) {
        super(pos);

        if(pos.getX() < 0 || pos.getY() < 0 || pos.getZ() < 0){
            throw new IllegalArgumentException("UBlockPos can't have negative numbers");
        }
    }
}
