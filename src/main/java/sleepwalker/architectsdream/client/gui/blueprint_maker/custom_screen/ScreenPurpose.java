package sleepwalker.architectsdream.client.gui.blueprint_maker.custom_screen;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.IDisplayName;
import sleepwalker.architectsdream.client.gui.blueprint_maker.ScreenBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_maker.screens.MainSettingScreen;

public enum ScreenPurpose implements IDisplayName {
    ENGINE(new TranslationTextComponent(i18n("engine"))),
    PALETTE_TYPE(new TranslationTextComponent(i18n("palette_type")));

    final ITextComponent name;

    ScreenPurpose(ITextComponent name) {
        this.name = name;
    }

    @Override
    public ITextComponent getDisplayName() {
        return name;
    }

    private static String i18n(String name){
        return String.format("screen.%s.%s.%s.tab.%s", ArchitectsDream.MODID, ScreenBlueprintCreator.NAME, MainSettingScreen.ID, name);
    }
}
