package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public abstract class BaseModel<T extends IVerifiable> implements IModel {

    protected final UBlockPos pos;
    protected final T entity;
    protected boolean visible = true;

    protected BaseModel(UBlockPos pos, T entity) {
        this.pos = pos;
        this.entity = entity;
    }

    @Nonnull
    @Override
    public UBlockPos getPos() {
        return pos;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean value) {
        visible = value;
    }
}
