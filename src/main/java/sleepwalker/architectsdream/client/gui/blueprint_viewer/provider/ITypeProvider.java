package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoGroup;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.validators.IValidator;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public interface ITypeProvider<T extends IVerifiable, E extends IInfoGroup>  {

    @Nonnull
    Class<T> getTypeClass();

    @Nonnull
    IModel createModel(T entity, UBlockPos pos, IValidator validator, E group);

    E createTypeGroup();
}
