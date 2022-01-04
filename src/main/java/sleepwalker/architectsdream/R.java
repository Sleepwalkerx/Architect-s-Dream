package sleepwalker.architectsdream;

import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.ArchitectsDream;

public final class R {
    private R(){ }
    public static final String
        FOLDER_DATA_NAME = "blueprints",
        // Number Operation
        NUMBEROPERATION_VALUE     = "value",
        NUMBEROPERATION_TYPE      = "type",

        // Exseption
        EXSEPTION_FUNCNAME_NOTEXIST = "The function with the name \"%s\" does not exist",
        EXSEPTION_ITEM_NOTEXIST = "The item with the name \"%s\" does not exist",
        EXSEPTION_EMPTY_OBJECT = "The \"%s\" field cannot be empty or null",
        EXSEPTION_INVALID_VEC3I = "Vec3i should be \"IntArray\" from 3 elements",
        EXSEPTION_INVALID_NUMBEROPERATION = "NumericOperation should be \"Primitive\" or \"Object\"",

        EXSEPTION_BLOCKPOS_FORMAT = "",

        NAME = "name"
    ;

    public static class BlockContainer {
        public static final String

            REGISTRATION_ID = "id",
            PROPERTIES = "properties",
            HAS_PROPERTIES = "has_properties",
            TAGS = "tags"
        ;

        public static final ResourceLocation NAME = ArchitectsDream.namespace("block_type");
    }

    public static class Validator {
        public static final String
            PALETTE_STATE = "state",
            POSITION = "pos"
        ;

        public static final ResourceLocation
            CONST = ArchitectsDream.namespace("const"),
            ANY_ONE = ArchitectsDream.namespace("any_one"),
            PERMUTATION_CONST = ArchitectsDream.namespace("perm_const")
        ;
    }

    public static class ScreenName {
        public static String SCREEN_BLUEPRINT_VIEWER = "blueprint_viewer";
    }

    public static class Exception {
        public static final String
            UNKNOWN_NAME = "Unknown %s name \"%s\"",
            UNSUPPORTED_TYPE = "The field \"%s\" is of an unsupported type",
            OBJECT_MISSING = "Required object \"%s\" is missing",
            CANNOT_EMPTY = "The \"%s\" object cannot be empty",
            DUPLICATE = "Object \"%s\" cannot have duplicates.",

            PALETTE_INDEX_OUT = "Palette index \"%s\" out of bounds."
        ;
    }

    public static class Warn {
        public static final String
            UNUSED_OBJECT = "Find unused object \"%s\""
        ;
    }

    public static class NBTNames {
        public static final String
            ENGINE_NAME = "name",
            ENGINE_PROPERTIES = "properties",

            CHALLENGE_OBJECT = "challenges",
            CHALLENGE_NAME = "name",

            STRUCTURE_VERSION = "version",
            STRUCTURE_AUTHOR = "author",
            STRUCTURE_SIZE = "size",
            STRUCTURE_RARITY = "rarity",
            STRUCTURE_ENGINE = "engine",
            STRUCTURE_PALETTE = "palette",
            STRUCTURE_VALIDATORS = "validators",
            STRUCTURE_CHALLENGES = "challenges"
        ;
    }

    public static class ChallengeConstructionExperience {
        public static final String
            /** INT */
            EXPERIENCE = "exp",
            /** INT */
            HAVE_EXPERIENCE = "have_exp"
        ;

        public static final ResourceLocation NAME = ArchitectsDream.namespace("construction_exp");
    }

    public static class BaseEngine {
        private BaseEngine(){}

        public static final String
            /** STRING ARRAY */
            ITEMS_LOCKED = "locked_items",
            ENGINE_TYPE_DO = "engine_type_do"
        ;
    }

    public static class EngineItemMaker {
        private EngineItemMaker(){}
        public static final String
            /** OBJECTS ARRAY */
            ITEMS_OUTPUT = "output_items"
        ;

        public static final ResourceLocation NAME = ArchitectsDream.namespace("engine_item_maker");
    }

    public static class BlueprintTemplate {
        public static final String
            REGISTRATION_ID = "temp_reg_id",
            NAME = "temp_name",
            AUTHOR = "temp_author",
            VALIDATOR_MODE = "validator_mode",
            RARITY = "temp_rarity",
            FILE_FORMAT = "temp_file_format",

            POINTS_DATA = "temp_points",

            SCREENS_DATA = "temp_screens_data",
            SCREEN_NAME = "name"
        ;
    }

    public static class Jei {
        public static final String
            ITEM_MAKER_NAME = "item_maker"
        ;
    }

    public static class RenderProperty {
        private RenderProperty(){}
        public static final String
            NAME = "rend_prop",
            YAW = "yaw",
            PITCH = "pitch"
        ;
    }

    public static class Blueprint {
        private Blueprint(){}
        public static final String

            // CLIENT ONLY
            RARITY = "rarity",

            // COMMON
            CONDITION = "condition",
            BLUEPRINT_NAME = "id"
        ;
    }

    public static class ModID {
        public static final String
            EXTENDED_CRAFTING = "extendedcrafting"
        ;
    }
}