package sleepwalker.architectsdream.resources;

import com.google.common.collect.Maps;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.exseption.InitException;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public final class BlueprintManager extends ReloadListener<Map<ResourceLocation, CompoundNBT>> {

    private static final Map<ResourceLocation, Blueprint> STORAGE = Maps.newHashMap();

    private static final Logger LOGGER = LogManager.getLogger();

    public BlueprintManager(){}

    @Override
    @Nonnull
    protected Map<ResourceLocation, CompoundNBT> prepare(@Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn){

        STORAGE.clear();

        Map<ResourceLocation, CompoundNBT> map = Maps.newHashMap();

        deserializeFiles(resourceManagerIn, ".json", (location, resource) -> {
            try (
                InputStream inputStream = resource.getInputStream();
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            ){

                if(map.putIfAbsent(location, JsonToNBT.parseTag(IOUtils.toString(reader))) != null){
                    LOGGER.error(String.format(R.Exception.DUPLICATE, location));
                }
            } 
            catch (Exception  e) {
                LOGGER.error("Couldn't parse data JSON file {}", location, e);
            }
        });

        deserializeFiles(resourceManagerIn, ".nbt", (location, resource) -> {

            try(DataInputStream inputStream = new DataInputStream(resource.getInputStream())){

                if(map.put(location, CompressedStreamTools.read(inputStream)) != null){
                    LOGGER.error(String.format(R.Exception.DUPLICATE, location));
                }
            }
            catch(Exception e){
                LOGGER.error("Couldn't parse data NBT file {}", location, e);
            }
        });

        return map;
    }

    @Override
    protected void apply(
        Map<ResourceLocation, CompoundNBT> agony,
        @Nonnull IResourceManager resourceManagerIn,
        @Nonnull IProfiler profilerIn
    ){
        LOGGER.info("=======================================================");
    	LOGGER.info("============|=====| Load Structures |=====|============");

        int[] result = new int[2];

        agony.forEach((location, fileIn) -> {
            try {
                Blueprint blueprint = SerializerManager.deserialize(fileIn, location);

                STORAGE.put(location, blueprint);

                result[0]++;
                LOGGER.info("[✔] Structure \"{}\" Loaded", location);
            }
            catch(Exception e){

                result[1]++;
                LOGGER.error("[✖] Can't initialization Structure \"{}\"", location, e);
            }
        });

        LOGGER.info("[✔] {} Loaded  [✖] {} Errors", result[0], result[1]);
        LOGGER.info("================| End Load Structures |================");
        LOGGER.info("=======================================================");
    }

    private void deserializeFiles(IResourceManager resourceManagerIn, String extension, BiConsumer<ResourceLocation, IResource> func){
        for(ResourceLocation resourceLocationIn : resourceManagerIn.listResources(R.FOLDER_DATA_NAME, file -> file.endsWith(extension))){
            String s = resourceLocationIn.getPath();
            ResourceLocation structureLocation = new ResourceLocation(
                resourceLocationIn.getNamespace(), 
                s.substring(R.FOLDER_DATA_NAME.length() + 1, s.length() - extension.length())
            );

            if(STORAGE.containsKey(structureLocation)){
                ArchitectsDream.LOGGER.error("[✖] Structure with the {} name already exists", structureLocation);
                continue;
            }

            try(IResource iresource = resourceManagerIn.getResource(resourceLocationIn)){
                func.accept(structureLocation, iresource);
            }
            catch(InitException e){
                LOGGER.error("[✖] Can't to prepare file for initialization '{}'", structureLocation, e);
            }
            catch(IOException e){
                LOGGER.error("[✖] Can't take {} file in Manager", resourceLocationIn, e);
            }
        }
    }

    public static boolean hasBlueprint(@Nonnull ResourceLocation location){
        return STORAGE.containsKey(location);
    }

    @Nullable
    public static Blueprint getBlueprint(@Nonnull ResourceLocation locationIn){
        return STORAGE.get(locationIn);
    }

    @Nonnull
    public static Collection<Blueprint> getBlueprints(){
        return STORAGE.values();
    }

    public static void addBlueprint(Blueprint blueprint){
        STORAGE.put(blueprint.getID(), blueprint);
    }
}
