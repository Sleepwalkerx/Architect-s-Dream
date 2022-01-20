package sleepwalker.architectsdream.structure;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.IDisplayName;

import java.util.Locale;

public enum DataType implements IDisplayName {

    PALETTE_TYPE,
    ENGINE,
    GENERAL_DATA,
    RENDER_PROPERTY;

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(I18n.get(String.format("%s.blueprint.data_type.%s.name", ArchitectsDream.MODID, toString().toLowerCase(Locale.ROOT))));
    }
}
