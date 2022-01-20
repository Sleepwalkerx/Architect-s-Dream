package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoGroup;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.structure.container.IVerifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface IModelProvider {

    ResourceLocation getTypeName();

    @Nonnull
    IModel createModel(IVerifiable entity, UBlockPos pos);

    @Nullable
    IInfoGroup createGroup(List<IModel> models);
}
