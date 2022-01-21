package sleepwalker.architectsdream.serialize.type;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sleepwalker.architectsdream.exception.NBTParseException;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PaletteTypeSerialBlock implements IPaletteTypeSerializer<ContainerTypeBlock> {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public CompoundNBT serialize(@Nonnull ContainerTypeBlock src) {

        CompoundNBT compoundNBT = new CompoundNBT();

        if(src.getBlockState() != null){

            compoundNBT.putString(
                R.BlockContainer.REGISTRATION_ID,
                src.getBlockState().getBlock().getRegistryName().toString()
            );

            if(!src.getBlockState().getValues().isEmpty()){

                compoundNBT.putBoolean(R.BlockContainer.HAS_PROPERTIES, src.isHasProperties());

                CompoundNBT properties = new CompoundNBT();

                src.getBlockState().getValues().forEach((property, compare) -> properties.putString(
                        property.getName(),
                        getValue(property, compare)
                ));

                compoundNBT.put(R.BlockContainer.PROPERTIES, properties);
            }
        }

        if(src.getTags() != null){

            ListNBT listNBT = new ListNBT();

            src.getTags().getTags().forEach((id, blockITag) -> {
                listNBT.add(StringNBT.valueOf(id.toString()));
            });

            compoundNBT.put(R.BlockContainer.TAGS, listNBT);
        }

        return compoundNBT;
    }

    @Nonnull
    @Override
    public IVerifiable deserialize(@Nonnull CompoundNBT entity) throws NBTParseException {

        NonNullList<ContainerTypeBlock.BlockComparator> comparators = NonNullList.create();

        ContainerTypeBlock.Properties propertiesContainer = new ContainerTypeBlock.Properties();

        // ============= TAGS ===============
        if(entity.contains(R.BlockContainer.TAGS, NBTTypes.LIST)) {

            ListNBT listNBT = entity.getList(R.BlockContainer.TAGS, NBTTypes.STRING);

            Map<ResourceLocation, ITag<Block>> map = new HashMap<>();

            for(int i = 0; i < listNBT.size(); i++){

                ResourceLocation location = new ResourceLocation(listNBT.getString(i));

                Optional<? extends ITag.INamedTag<Block>> blockITag = BlockTags.getWrappers().stream().filter(a -> a.getName().equals(location)).findFirst();

                if(!blockITag.isPresent()){
                    continue;
                }

                map.put(location, blockITag.get());
            }

            if(!map.isEmpty()){

                propertiesContainer.setTags(new ContainerTypeBlock.BlockTagCollection(map));

                comparators.add(ContainerTypeBlock.TAG);
            }
        }

        // ============== ID & PROPERTIES ================
        if(entity.contains(R.BlockContainer.REGISTRATION_ID, NBTTypes.STRING)){

            String id = entity.getString(R.BlockContainer.REGISTRATION_ID);

            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));

            if(block == null){
                throw new NBTParseException(String.format(R.Exception.UNKNOWN_NAME, "BlockRegistry", id));
            }

            BlockState blockState = block.defaultBlockState();

            boolean hasProperties = entity.contains(R.BlockContainer.HAS_PROPERTIES, NBTTypes.BYTE);

            if(hasProperties){

                hasProperties = entity.getBoolean(R.BlockContainer.HAS_PROPERTIES);

                StateContainer<Block, BlockState> stateContainer = block.getStateDefinition();

                CompoundNBT properties = entity.getCompound(R.BlockContainer.PROPERTIES);

                for(String key : properties.getAllKeys()){
                    Property<?> property = stateContainer.getProperty(key);
                    if(property != null){
                        blockState = setValue(blockState, property, properties.getString(key));
                    }
                    else {
                        logger.error(String.format(R.Exception.UNKNOWN_NAME, "BlockProperty", key));
                    }
                }
            }

            propertiesContainer.setBlockState(blockState, hasProperties);
            comparators.add(ContainerTypeBlock.BLOCK_STATE);
        }

        if(comparators.isEmpty()){
            throw new NBTParseException(String.format(R.Exception.CANNOT_EMPTY, "BlockComparators"));
        }
        else return new ContainerTypeBlock(comparators, propertiesContainer);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> String getValue(@Nonnull Property<T> property, Comparable<?> value) {
        return property.getName((T)value);
    }
    @Nonnull
    private <T extends Comparable<T>> BlockState setValue(BlockState blockState, @Nonnull Property<T> property, String value) throws NBTParseException {
        Optional<T> optional = property.getValue(value);

        if(optional.isPresent()){
            return blockState.setValue(property, optional.get());
        }
        else throw new NBTParseException(String.format(R.Exception.UNKNOWN_NAME, "BlockProperty", value));
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return R.BlockContainer.NAME;
    }
}
