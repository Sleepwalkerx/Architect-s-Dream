package sleepwalker.architectsdream.serialize;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.exseption.InitException;
import sleepwalker.architectsdream.exseption.NBTParseException;
import sleepwalker.architectsdream.math.UBlockPos;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.serialize.converters.EngineNBT;
import sleepwalker.architectsdream.serialize.converters.EnumNBT;
import sleepwalker.architectsdream.serialize.converters.BlueprintPropertiesSerializer;
import sleepwalker.architectsdream.serialize.converters.UVector3iNBT;
import sleepwalker.architectsdream.serialize.engine.EngineSerialItemMaker;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;
import sleepwalker.architectsdream.serialize.type.IPaletteTypeSerializer;
import sleepwalker.architectsdream.serialize.type.PaletteTypeSerialBlock;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.serialize.validator.ValidatorSerialConst;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.PlacementData;
import sleepwalker.architectsdream.structure.RenderProperty;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.engine.BaseStructureEngine;
import sleepwalker.architectsdream.structure.validators.IValidator;
import sleepwalker.architectsdream.utils.NBTTypes;
import sleepwalker.architectsdream.utils.ValidatorUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static sleepwalker.architectsdream.R.NBTNames.*;

public class SerializerManager {

    public static final Map<ResourceLocation, IEngineSerializer<?>> ENGINES = Maps.newHashMap();

    public static final EngineSerialItemMaker ENGINE_ITEM_MAKER = new EngineSerialItemMaker();

    public static final IEngineSerializer<BaseStructureEngine> ENGINE_DEFAULT = new IEngineSerializer<BaseStructureEngine>() {

        @Override
        public void serialize(BaseStructureEngine src, CompoundNBT nbtProperties) { }

        @Nonnull
        @Override
        public BaseStructureEngine deserialize(CompoundNBT nbtProperties) throws NBTParseException {
            return BaseStructureEngine.DEFAULT_ENGINE;
        }

        @Nonnull
        @Override
        public ResourceLocation getRegistryName() {
            return ArchitectsDream.namespace("default");
        }
    };

    public static final Map<ResourceLocation, IValidatorSerializer> VALIDATORS = Maps.newHashMap();
    public static final ValidatorSerialConst VALIDATOR_CONST = new ValidatorSerialConst();

    public static final Map<ResourceLocation, IPaletteTypeSerializer<?>> TYPES = Maps.newHashMap();
    public static final PaletteTypeSerialBlock TYPE_BLOCK = new PaletteTypeSerialBlock();

    static {
        ENGINES.put(ENGINE_DEFAULT.getRegistryName(), ENGINE_DEFAULT);
    }

    private static final Logger logger = LogManager.getLogger();

    @Nonnull
    public static Blueprint deserialize(@Nonnull CompoundNBT main, ResourceLocation id) throws InitException {
        String author = main.getString(STRUCTURE_AUTHOR);

        if(author.isEmpty()){
            author = "No-name From The Internet";
        }

        if(!main.contains(STRUCTURE_VERSION, NBTTypes.STRING)){
            throw new InitException(String.format(R.Exception.OBJECT_MISSING, STRUCTURE_VERSION));
        }

        if((new DefaultArtifactVersion(main.getString(STRUCTURE_VERSION))).compareTo(ArchitectsDream.STRUCTURE_VERSION) != 0){
            throw new InitException("The version of the structure must match the current version of the mod");
        }

        UVector3i size = UVector3iNBT.deserialize(main, STRUCTURE_SIZE);
        if(!isAboveZero(size)){
            throw new InitException("Structure size must be strictly > 0");
        }

        Blueprint.Rarity rarity = EnumNBT.deserialize(
            main,
            STRUCTURE_RARITY,
            Blueprint.Rarity.SIMPLE,
            Blueprint.Rarity.class
        );

        main.remove(STRUCTURE_RARITY);

        Map<String, Pair<List<IVerifiable>, ResourceLocation>> palette = deserializePalette(main.getCompound(STRUCTURE_PALETTE));

        if(palette.isEmpty()){
            throw new InitException(String.format(R.Exception.CANNOT_EMPTY, "Palette"));
        }

        RenderProperty renderProperty = RenderProperty.DEFAULT;

        if(Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER){

            if(main.contains(R.RenderProperty.NAME.getPath(), NBTTypes.OBJECT)){

                RenderProperty.Data data = RenderProperty.Data.create();

                data.readData(main.getCompound(R.RenderProperty.NAME.getPath()));

                renderProperty = data.build();
            }
        }

        Blueprint.Properties properties;

        if(main.contains(R.Properties.NAME, NBTTypes.OBJECT)){

            properties = BlueprintPropertiesSerializer.deserialize(main.getCompound(R.Properties.NAME));

            main.remove(R.Properties.NAME);
        }
        else {
            properties = Blueprint.Properties.DEFAULT;
        }

        return new Blueprint(
            id,
            new Blueprint.Structure(
                ValidatorUtils.calculateStructureData(
                    deserializeValidators(
                        main.getCompound(STRUCTURE_VALIDATORS),
                        palette
                    ),
                    size
                ),
                EngineNBT.deserialize(main.getCompound(STRUCTURE_ENGINE)),
                rarity,
                author,
                size
            ),
            main,
            renderProperty,
            properties
        );
    }

    @Nonnull
    public static CompoundNBT serialize(@Nonnull TemplateFileStructure template){

        CompoundNBT structureFile = new CompoundNBT();

        structureFile.putString(STRUCTURE_AUTHOR, template.author);
        structureFile.putString(STRUCTURE_VERSION, ArchitectsDream.STRUCTURE_VERSION.toString());
        structureFile.put(STRUCTURE_RARITY, EnumNBT.serialize(template.rarity));
        structureFile.put(STRUCTURE_SIZE, UVector3iNBT.serialize(template.size));

        structureFile.put(STRUCTURE_ENGINE, EngineNBT.serialize(template.engine));

        if(!template.rend_prop.build().equals(RenderProperty.DEFAULT)){

            CompoundNBT compoundNBT = (CompoundNBT) template.rend_prop.saveData();

            compoundNBT.remove(R.RenderProperty.ZOOM);

            structureFile.put(R.RenderProperty.NAME.getPath(), compoundNBT);
        }

        if(template.properties != Blueprint.Properties.DEFAULT){
            structureFile.put(R.Properties.NAME, BlueprintPropertiesSerializer.serialize(template.properties));
        }

        if(!template.entities.isEmpty()){
            CompoundNBT paletteNBT = new CompoundNBT();
            CompoundNBT validatorsNBT = new CompoundNBT();

            template.entities.forEach(entity -> {
                paletteNBT.put(convert(entity.id), serializeListEntities(TYPES.get(entity.id), entity.palette));
                validatorsNBT.put(convert(entity.id), serializeTypeValidators(entity.validators));
            });

            structureFile.put(STRUCTURE_PALETTE, paletteNBT);
            structureFile.put(STRUCTURE_VALIDATORS, validatorsNBT);
        }

        return structureFile;
    }

    @Nonnull
    private static CompoundNBT serializeTypeValidators(@Nonnull Map<IValidatorSerializer, Map<UBlockPos, Integer>> validators){

        CompoundNBT validatorsNBT = new CompoundNBT();

        validators.forEach((serializer, map) -> {

            INBT nbt = serializer.serialize(map);

            if(nbt != null)
                validatorsNBT.put(convert(serializer.getRegistryName()), nbt);
        });

        return validatorsNBT;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static <T extends IVerifiable> ListNBT serializeListEntities(IPaletteTypeSerializer<T> serializer, @Nonnull List<IVerifiable> entities){
        ListNBT entitiesNBT = new ListNBT();

        entities.forEach(entity -> entitiesNBT.add(serializer.serialize((T) entity)));

        return entitiesNBT;
    }

    @Nonnull
    private static PlacementData deserializeValidators(CompoundNBT validatorsNBT, Map<String, Pair<List<IVerifiable>, ResourceLocation>> palette){

        Map<ResourceLocation, Set<IValidator>> validatorsMap = Maps.newHashMap();

        for(Map.Entry<String, Pair<List<IVerifiable>, ResourceLocation>> entry : palette.entrySet()){
            if(validatorsNBT.contains(entry.getKey(), NBTTypes.OBJECT)){
                CompoundNBT validatorsEntity = validatorsNBT.getCompound(entry.getKey());

                Set<IValidator> validators = Sets.newHashSet();

                for(String key : validatorsEntity.getAllKeys()){
                    IValidatorSerializer serializer = VALIDATORS.get(ArchitectsDream.location(key));

                    if(serializer == null){
                        logger.error(String.format(R.Exception.UNKNOWN_NAME, "Validator", key));
                        continue;
                    }

                    if(!validators.add(serializer.deserialize(Objects.requireNonNull(validatorsEntity.get(key)), entry.getValue().getKey()))){
                        logger.error(String.format(R.Exception.DUPLICATE, key));
                    }
                }

                validatorsMap.put(entry.getValue().getValue(), validators);
            }
            else {
                logger.warn(String.format(R.Warn.UNUSED_OBJECT, entry.getKey()));
            }
        }

        if(validatorsMap.isEmpty()){
            throw new InitException(String.format(R.Exception.CANNOT_EMPTY, "Validators"));
        }
        else return new PlacementData(validatorsMap);
    }

    @Nonnull
    private static Map<String, Pair<List<IVerifiable>, ResourceLocation>> deserializePalette(CompoundNBT paletteNBT){

        Map<String, Pair<List<IVerifiable>, ResourceLocation>> palette = Maps.newHashMap();

        for(String key : paletteNBT.getAllKeys()){

            IPaletteTypeSerializer<?> serializer = TYPES.get(ArchitectsDream.location(key));

            if(serializer == null){
                logger.error(String.format(R.Exception.UNKNOWN_NAME, "PaletteType", key));
                continue;
            }

            if(paletteNBT.contains(key, NBTTypes.LIST)){
                List<IVerifiable> objects = Lists.newArrayList();
                ListNBT listNBT = paletteNBT.getList(key, NBTTypes.OBJECT);

                for(int i = 0; i < listNBT.size(); i++){
                    objects.add(serializer.deserialize(listNBT.getCompound(i)));
                }

                if(palette.put(key, Pair.of(objects, serializer.getRegistryName())) != null){
                    logger.error(String.format(R.Exception.DUPLICATE, key));
                }
            }
            else logger.error(String.format(R.Exception.UNSUPPORTED_TYPE, key));
        }

        return palette;
    }

    private static boolean isAboveZero(@Nonnull Vector3i vector3i){
        return vector3i.getX() > 0 && vector3i.getY() > 0 && vector3i.getZ() > 0;
    }

    private static String convert(@Nonnull ResourceLocation location){
        return location.getNamespace().equals(ArchitectsDream.MODID) ? location.getPath() : location.toString();
    }
}