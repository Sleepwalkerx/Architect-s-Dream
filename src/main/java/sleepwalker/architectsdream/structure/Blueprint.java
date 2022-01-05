package sleepwalker.architectsdream.structure;

import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.gui.IDisplayName;
import sleepwalker.architectsdream.math.UVector3i;
import sleepwalker.architectsdream.structure.engine.BaseStructureEngine;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;

public class Blueprint {

    @Nonnull
    private final ResourceLocation id;

    @Nonnull
    protected final Structure structure;

    @Nonnull
    protected final RenderProperty renderProperty;

    @Nonnull
    private final CompoundNBT sourceForClient;

    @Nonnull
    private final Properties properties;

    public Blueprint(
        @Nonnull ResourceLocation id,
        @Nonnull Structure structure,
        @Nonnull CompoundNBT sourceForClient,
        @Nonnull RenderProperty renderProperty,
        @Nonnull Properties properties
    ){
        this.structure = structure;
        this.id = id;
        this.sourceForClient = sourceForClient;
        this.renderProperty = renderProperty;
        this.properties = properties;
    }

    @Nonnull
    public CompoundNBT getSourceForClient(){ return sourceForClient; }

    @Nonnull
    public Structure getStructure(){
        return structure;
    }

    @Nonnull
    public RenderProperty getRenderProperty() {
        return renderProperty;
    }

    @Nonnull
    public Properties getProperties() { return properties; }

    @Nonnull
    public ResourceLocation getID(){
        return id;
    }

    @Nonnull
    public Result matches(ItemUseContext itemContext){

        if(itemContext.getItemInHand().getCount() == 0){
            return Result.EMPTY_STRUCTURE;
        }

        for(PlacementData placementData : structure.placementData){

            BlockPos shiftPos = placementData.matches(structure, itemContext);

            if(shiftPos != null){
                if(structure.engine.formed(shiftPos, this, placementData, itemContext)){

                    properties.onStructureFormed(itemContext, this);

                    return Result.CORRECTLY_FORMED;
                }
                else 
                    return Result.ERROR_ENGINE_FORMED;
            }
        }

        return Result.INCORRECTLY_FORMED;
    }

    public enum Rarity implements IDisplayName {

        SIMPLE(Style.EMPTY),
        RARE(Style.EMPTY.withColor(TextFormatting.YELLOW)),
        LEGENDARY(Style.EMPTY.withColor(TextFormatting.AQUA)),
        WORLD_WONDER(Style.EMPTY.withColor(TextFormatting.GREEN)),
        NONE(Style.EMPTY);

        private final Style style;

        Rarity(Style style){
            this.style = style;
        }

        @Nonnull
        @OnlyIn(Dist.CLIENT)
        public ITextComponent getDisplayName() { 
            return new TranslationTextComponent(
                String.format("%s.blueprint.rarity.%s", ArchitectsDream.MODID, toString().toLowerCase())
            ).setStyle(style); 
        }
    }

    public static final class Result {

        private static final Result
            CORRECTLY_FORMED = of("correctly_build", TextFormatting.GREEN),
            INCORRECTLY_FORMED = of("incorrectly_build", TextFormatting.RED),
            ERROR_ENGINE_FORMED = of("error_engine_formed", TextFormatting.RED),
            EMPTY_STRUCTURE = of("empty_structure", TextFormatting.YELLOW)
        ;

        public final ITextComponent textMessage;

        private Result(ITextComponent msg){
            this.textMessage = msg;
        }

        private static Result of(String key, TextFormatting formatting){
            return new Result(
                new TranslationTextComponent(trans(key)).withStyle(Style.EMPTY.withColor(formatting))
            );
        }

        private static String trans(String key){
            return String.format("%s.structure.analysis.%s", ArchitectsDream.MODID, key);
        }
    }

    public static class Properties {

        public static final int INFINITY = -1;

        public static final Properties DEFAULT = new Properties(-1, EnumCondition.WHOLE){

            @Override
            void onStructureFormed(ItemUseContext context, Blueprint blueprint) { }
        };

        private final int numberOfUses;

        @Nonnull
        private final EnumCondition condition;

        public Properties(int numberOfUses, @Nonnull EnumCondition condition){

            this.numberOfUses = numberOfUses;
            this.condition = condition;
        }

        public int getNumberOfUses() {
            return numberOfUses;
        }

        @Nonnull
        public EnumCondition getCondition() {
            return condition;
        }

        void onStructureFormed(final ItemUseContext context, final Blueprint blueprint){

            if(numberOfUses != INFINITY && !context.getPlayer().isCreative()){

                CompoundNBT itemCompound = context.getItemInHand().getOrCreateTag();

                CompoundNBT properties = context.getItemInHand().getOrCreateTag().getCompound(R.Properties.NAME);

                int number = properties.getInt(R.Properties.NUMBER_OF_USE);

                if(number == 0){
                    number = numberOfUses;
                }

                number--;

                if(number <= 0){

                    context.getItemInHand().setCount(0);

                    return;
                }
                else {

                    properties.putInt(R.Properties.NUMBER_OF_USE, number);
                }

                itemCompound.put(R.Properties.NAME, properties);

                context.getItemInHand().setTag(itemCompound);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Properties that = (Properties) o;
            return numberOfUses == that.numberOfUses && condition == that.condition;
        }

        @Override
        public int hashCode() {
            return Objects.hash(numberOfUses, condition);
        }
    }

    public static class Structure {

        protected final NonNullList<PlacementData> placementData;

        private final BaseStructureEngine engine;

        private final Rarity rarity;
        private final UVector3i size;

        private final String author;

        public Structure(
            NonNullList<PlacementData> placementData,
            BaseStructureEngine engine,
            Rarity rarity,
            String author,
            UVector3i size
        ){
            this.placementData = placementData;
            this.engine = engine;
            this.rarity = rarity;
            this.size = size;
            this.author = author;
        }

        public UVector3i getSize(){ return this.size; }
        public String getAuthor(){ return author; }
        public Rarity getRarity(){ return rarity; }
        public PlacementData getBasePlacementData() { return placementData.get(0); }
        public BaseStructureEngine getEngine(){ return engine; }

        private static final Structure EMPTY = new Structure(
            NonNullList.withSize(1, new PlacementData(Collections.emptyMap())),
            BaseStructureEngine.DEFAULT_ENGINE,
            Rarity.NONE,
            "",
            UVector3i.ZERO
        );
    }

    public static final Blueprint DEFAULT = new Blueprint(
        new ResourceLocation(ArchitectsDream.MODID, "null-empty"),
        Structure.EMPTY,
        new CompoundNBT(),
        RenderProperty.DEFAULT,
        Blueprint.Properties.DEFAULT
    ){

        @Nonnull
        @Override
        public Result matches(ItemUseContext itemContext) {
            return Result.EMPTY_STRUCTURE;
        }
    };
}
