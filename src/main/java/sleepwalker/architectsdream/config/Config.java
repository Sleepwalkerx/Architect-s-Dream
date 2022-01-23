package sleepwalker.architectsdream.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator.EnumFileFormat;
import sleepwalker.architectsdream.structure.RenderProperty;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        public final BooleanValue lockAllTrans;
        public final BooleanValue lockPitch;
        public final ConfigValue<Float> x, y, pitch, yaw, zoom;

        public final ConfigValue<List<String>> startComponentsKit;

        public ClientConfig(@Nonnull ForgeConfigSpec.Builder builder){
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

            lockPitch = builder.define("LockPitchRotation", true);

            lockAllTrans = builder.define("LockAllMovement", true);

            x = builder.define("x", RenderProperty.DEFAULT.getX());
            y = builder.define("y", RenderProperty.DEFAULT.getY());
            pitch = builder.define("pitch", RenderProperty.DEFAULT.getPitch());
            yaw = builder.define("yaw", RenderProperty.DEFAULT.getYaw());
            zoom = builder.define("zoom", RenderProperty.DEFAULT.getZoom());

            startComponentsKit = builder
                    .comment("The starter kit of components for BlueprintCreator. When you create a new drawing, they will be automatically added. Format: 'mod_id:component_id'")
                    .comment("You can use: [architectsdream:block_type], [architectsdream:engine_item_maker]")
            .define("startComponentsKit", new ArrayList<>());


        }
    }
}
