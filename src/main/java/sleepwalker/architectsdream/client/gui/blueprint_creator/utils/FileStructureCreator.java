package sleepwalker.architectsdream.client.gui.blueprint_creator.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;

import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_creator.ScreenBlueprintCreator.EnumFileFormat;
import sleepwalker.architectsdream.client.gui.blueprint_creator.screens.MainSettingScreen;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.TemplateFileStructure;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.utils.NBTTypes;
import sleepwalker.architectsdream.utils.NBTUtils;

@OnlyIn(Dist.CLIENT)
public class FileStructureCreator {
    private FileStructureCreator(){}
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();

    private static File directory = Paths.get(FMLPaths.GAMEDIR.relative().resolve("blueprints").toUri()).toFile();

    @OnlyIn(Dist.CLIENT)
    public static void createBlueprintFile(TemplateFileStructure template, MainSettingScreen screen){

        if(!directory.exists() && !directory.mkdirs()){
            LOGGER.error("Unable to create directory");
        }

        CompoundNBT file = SerializerManager.serialize(template);

        if(screen.getFileFormatOption() == EnumFileFormat.JSON){
            try(FileWriter writer = new FileWriter(
                new File(directory.toPath().resolve(screen.getRegistryID() + ".json").toUri())
            )){
                GSON.toJson(NBTUtils.parseToJson(file), writer);
                sendMessage(true, screen.getRegistryID() + ".json");
                addTranslation(screen.getRegistryID(), screen.getName());
            }
            catch(Exception e){
                LOGGER.error(e);
                sendMessage(false, screen.getRegistryID() + ".json");
            }
        }
        else {
            try{
                CompressedStreamTools.write(
                    file,
                    new File(directory.toPath().resolve(screen.getRegistryID() + ".nbt").toUri())
                );
                sendMessage(true, screen.getRegistryID() + ".nbt");
                addTranslation(screen.getRegistryID(), screen.getName());
            }
            catch(Exception e){
                LOGGER.error(e);
                sendMessage(false, screen.getRegistryID() + ".nbt");
            }
        }
    }

    private static void addTranslation(String key, String value) {

        key = String.format("blueprint.%s.%s.name", Config.CLIENT.namespace.get(), key);
        File file = new File(directory.toPath().resolve("translation.json").toUri());

        JsonObject jsonObject = null;
        if(file.exists()){
            try(FileReader reader = new FileReader(file)){
                jsonObject = JSONUtils.fromJson(GSON, reader, JsonObject.class);
            }
            catch(IOException e){
                LOGGER.error(e);
                return;
            }
        }

        if(jsonObject == null) jsonObject = new JsonObject();

        jsonObject.addProperty(key, value);

        try(FileWriter writer = new FileWriter(file)){
            GSON.toJson(jsonObject, writer);
        }
        catch(IOException e){
            LOGGER.error(e);
        }
    }

    public static void sendMessage(boolean is, String fileName){

        if(is){
            Minecraft.getInstance().player.displayClientMessage(new TranslationTextComponent(
                ArchitectsDream.MODID + ".structure.development.success_build", fileName)
                .setStyle(Style.EMPTY.withColor(TextFormatting.GREEN)), false
            );
        }
        else{
            Minecraft.getInstance().player.displayClientMessage(new TranslationTextComponent(
                ArchitectsDream.MODID + ".structure.development.error_build", fileName)
                .setStyle(Style.EMPTY.withColor(TextFormatting.RED)), false
            );
        }
    }

    public static List<PointTemplateData> getPointTemplateData(CompoundNBT tag){
        if(tag != null && tag.contains(R.BlueprintCreator.POINTS_DATA, NBTTypes.OBJECT)){
            List<PointTemplateData> data = Lists.newArrayList();
            tag = tag.getCompound(R.BlueprintCreator.POINTS_DATA);
            
            for(ValidatorMode mode : ValidatorMode.getValidators()){
                if(tag.contains(mode.getRegistryName().toString(), NBTTypes.INT_ARRAY)){
                    int[] array = tag.getIntArray(mode.getRegistryName().toString());

                    if(array.length < 3) continue;

                    data.add(array.length != 6 ?
                        new PointTemplateData(mode, new BlockPos(array[0], array[1], array[2]))
                        : new PointTemplateData(mode,
                            new BlockPos(array[0], array[1], array[2]),
                            new BlockPos(array[3], array[4], array[5])
                        )
                    );
                }
            }

            return data;
        }
        else return Collections.emptyList();
    }

    public static IValidatorSerializer getSerialize(BlockPos blockPos){
        return getMode(WorldRenderer.getPoints(), blockPos).getSerializer();
    }

    public static ValidatorMode getMode(List<PointTemplateData> dataList, BlockPos blockPos){
        return dataList
            .stream()
            .filter(pointData -> isInArea(pointData.max, pointData.min, blockPos))
            .min(Comparator.comparingInt(a -> a.area))
            .orElseGet(() -> dataList.get(0))
        .getMode();
    }

    private static boolean isInArea(BlockPos max, BlockPos min, BlockPos point){
        return
            min.getX() >= point.getX() && point.getX() <= max.getX() &&
            min.getY() >= point.getY() && point.getY() <= max.getY() &&
            min.getZ() >= point.getZ() && point.getZ() <= max.getZ()
        ;
    }

    public static class WorldEnumerator implements Iterable<BlockPos> {
        private int x0, y0, z0;
        private final int x1, y1, z1;

        private final int deltaX, deltaY;

        public WorldEnumerator(BlockPos value0, BlockPos value1){
            deltaX = Math.abs(value0.getX() - value1.getX());
            deltaY = Math.abs(value0.getY() - value1.getY());

            x0 = Math.min(value0.getX(), value1.getX());
            y0 = Math.min(value0.getY(), value1.getY());
            z0 = Math.min(value0.getZ(), value1.getZ());

            x1 = Math.max(value0.getX(), value1.getX());
            y1 = Math.max(value0.getY(), value1.getY());
            z1 = Math.max(value0.getZ(), value1.getZ());
        }

        public static WorldEnumerator of(BlockPos value0, BlockPos value1){
            return new WorldEnumerator(value0, value1);
        }

        @Override
        public Iterator<BlockPos> iterator() {
            return new Iterator<BlockPos>(){
                @Override
                public boolean hasNext() {
                    return z0 <= z1;
                }

                @Override
                public BlockPos next() {
                    if(!hasNext()){
                        throw new NoSuchElementException();
                    }
                    else {
                        BlockPos blockPos = new BlockPos(x0, y0, z0);

                        if(x0 != x1){
                            x0++;
                        }
                        else if(y0 != y1){
                            x0 -= deltaX;
                            y0++;
                        }
                        else {
                            y0 -= deltaY;
                            x0 -= deltaX;
                            z0++;
                        }

                        return blockPos;
                    }
                }
            };
        }
    }

    public static class PointTemplateData {
        private final ValidatorMode mode;
        private final BlockPos max, min;

        private final int area;

        public PointTemplateData(ValidatorMode mode, BlockPos a, BlockPos b){
            this.mode = mode;

            this.max = new BlockPos(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
            );
            this.min = new BlockPos(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ())
            );

            this.area = mathArea(max, min);
        }

        public PointTemplateData(ValidatorMode mode, BlockPos a){
            this.mode = mode;

            this.max = new BlockPos(a);
            this.min = BlockPos.ZERO;

            this.area = mathArea(max, min);
        }

        public float getColorR(){
            return mode.getR();
        }

        public float getColorG(){
            return mode.getG();
        }

        public float getColorB(){
            return mode.getB();
        }

        public BlockPos getMax() {
            return max;
        }

        public BlockPos getMin() {
            return min;
        }

        public ValidatorMode getMode() {
            return mode;
        }

        public int getArea() {
            return area;
        }

        private int mathArea(BlockPos a, BlockPos b){
            return Math.abs(a.getX() - b.getX()) * Math.abs(a.getY() - b.getY()) * Math.abs(a.getZ() - b.getZ());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PointTemplateData that = (PointTemplateData) o;
            return area == that.area && mode == that.mode && Objects.equals(max, that.max) && Objects.equals(min, that.min);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mode, max, min, area);
        }
    }
}
