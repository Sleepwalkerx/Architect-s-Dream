package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.engine;

import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.result.SimpleResultInfoElement;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.structure.engine.StructureEngineItemMaker;

public class ItemMakerProvider implements IEngineProvider {

    public static final ItemMakerProvider PROVIDER = new ItemMakerProvider();

    @Override
    public void create() {

        ScreenBlueprintViewer viewer = ScreenBlueprintViewer.activeViewer();

        StructureEngineItemMaker itemMaker = (StructureEngineItemMaker) viewer.getBlueprint().getStructure().getEngine();

        itemMaker.getOutputItems().forEach(stack -> {
            viewer.getInfoPanel().addResultElement(new SimpleResultInfoElement(stack));
        });
    }

    @Override
    public IEngineSerializer<?> getSerializer() {
        return SerializerManager.ENGINE_ITEM_MAKER;
    }
}
