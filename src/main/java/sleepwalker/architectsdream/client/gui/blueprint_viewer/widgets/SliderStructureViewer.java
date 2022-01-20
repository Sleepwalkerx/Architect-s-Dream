package sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets;

import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import sleepwalker.architectsdream.client.ISavable;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.window.WindowStructureViewer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SliderStructureViewer extends AbstractSlider implements ISavable {

    private final double section;
    private int level;
    private int maxLevel;

    private ChangeMode changeMode = ChangeMode.Y_END;

    private final WindowStructureViewer viewer;

    private static final String MODE = "mode", VALUE = "value";

    public SliderStructureViewer(WindowStructureViewer viewer, int x, int y, int width, int height){
        this(viewer, x, y, width, height, ChangeMode.Y_END.maxValue(viewer));
    }

    protected SliderStructureViewer(WindowStructureViewer viewer, int x, int y, int width, int height, int maxLevel) {
        super(x, y, width, height, new StringTextComponent(String.valueOf(maxLevel)), 1.0D);

        section = 1.0D / maxLevel;
        this.level = maxLevel;
        this.maxLevel = maxLevel;
        this.viewer = viewer;
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height){
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        else return false;
    }

    public void setChangeMode(@Nonnull ChangeMode changeMode) {

        this.changeMode = changeMode;

        maxLevel = changeMode.maxValue(viewer);
    }

    @Override
    protected void updateMessage() {
        setMessage(new StringTextComponent(String.valueOf(level)));
    }

    @Override
    protected void applyValue() {
        level = MathHelper.clamp((int)(value / section) + 1, 0, maxLevel);
        changeMode.change(viewer, level);
    }

    @Nonnull
    @Override
    public INBT saveData() {

        CompoundNBT compoundNBT = new CompoundNBT();

        compoundNBT.putInt(MODE, changeMode.ordinal());
        compoundNBT.putDouble(VALUE, value);

        return compoundNBT;
    }

    @Override
    public void readData(@Nullable INBT nbt) {

        if(nbt == null){
            return;
        }

        CompoundNBT compoundNBT = (CompoundNBT)nbt;

        changeMode = ChangeMode.values()[compoundNBT.getInt(MODE)];

        value = compoundNBT.getDouble(VALUE);

        this.applyValue();
        updateMessage();
    }

    public enum ChangeMode {

        X_START {
            @Override
            public void change(WindowStructureViewer viewer, int value) {
                viewer.xStartRend = value;
            }

            @Override
            public int maxValue(WindowStructureViewer viewer) {
                return viewer.getSize().getX();
            }
        },
        X_END {
            @Override
            public void change(WindowStructureViewer viewer, int value) {
                viewer.xEndRend = value;
            }

            @Override
            public int maxValue(WindowStructureViewer viewer) {
                return viewer.getSize().getX();
            }
        },
        Y_START {
            @Override
            public void change(WindowStructureViewer viewer, int value) {
                viewer.yStartRend = value;
            }

            @Override
            public int maxValue(WindowStructureViewer viewer) {
                return viewer.getSize().getY();
            }
        },
        Y_END {
            @Override
            public void change(WindowStructureViewer viewer, int value) {
                viewer.yEndRend = value;
            }

            @Override
            public int maxValue(WindowStructureViewer viewer) {
                return viewer.getSize().getY();
            }
        },
        Z_START {
            @Override
            public void change(WindowStructureViewer viewer, int value) {
                viewer.zStartRend = value;
            }

            @Override
            public int maxValue(WindowStructureViewer viewer) {
                return viewer.getSize().getZ();
            }
        },
        Z_END {
            @Override
            public void change(WindowStructureViewer viewer, int value) {
                viewer.zEndRend = value;
            }

            @Override
            public int maxValue(WindowStructureViewer viewer) {
                return viewer.getSize().getZ();
            }
        };

        public abstract void change(WindowStructureViewer viewer, int value);

        public abstract int maxValue(WindowStructureViewer viewer);
    }
}
