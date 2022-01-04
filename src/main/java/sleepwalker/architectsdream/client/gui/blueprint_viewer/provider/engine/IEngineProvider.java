package sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.engine;

import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;

public interface IEngineProvider {

    IEngineProvider DEFAULT = new IEngineProvider() {

        @Override
        public void create() { }

        @Override
        public IEngineSerializer<?> getSerializer() {
            return SerializerManager.ENGINE_DEFAULT;
        }
    };

    void create();

    default String getEngineTypeDo(){
        return ScreenBlueprintViewer.screenI18n(R.BaseEngine.ENGINE_TYPE_DO, getSerializer().getRegistryName().getPath(), R.NAME);
    }

    IEngineSerializer<?> getSerializer();
}
