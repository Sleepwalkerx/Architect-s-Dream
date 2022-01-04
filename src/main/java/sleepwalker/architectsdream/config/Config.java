package sleepwalker.architectsdream.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_maker.ScreenBlueprintCreator.EnumFileFormat;

public final class Config {
    private Config() { throw new IllegalStateException("Utility class"); }

    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {

        Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT = client.getKey(); CLIENT_SPEC = client.getValue();
    }

    public static final class ClientConfig {
        public final ConfigValue<String> author, name, namespace;
        public final BooleanValue duplicateRegID, considerAir;
        public final EnumValue<EnumFileFormat> fileFormat;
        public final BooleanValue translucentMesh;

        public ClientConfig(ForgeConfigSpec.Builder builder){
            author = builder
                .comment("Default author")
                .define("author", "Anonymous", obj -> {
                    if(obj instanceof String){
                        return !((String)obj).isEmpty();
                    }
                    return false;
                })
            ;
            duplicateRegID = builder
                .comment("Duplicate the name of the drawing in the registration id")
                .define("duplicateRegID", true)
            ;
            name = builder
                .comment("Default name")
            .define("name", "dandelion");
            namespace = builder.define("namespace", ArchitectsDream.MODID);

            fileFormat = builder.defineEnum("fileFormat", EnumFileFormat.NBT);

            considerAir = builder.define("consider_air", false);

            translucentMesh = builder
                .comment("If positive, then when the object is selected, the mesh will show through the blocks.")
            .define("translucentMesh", false);
        }
    }
}
