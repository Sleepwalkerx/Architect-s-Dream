package sleepwalker.architectsdream.serialize.engine;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import sleepwalker.architectsdream.network.shell.BlueprintShell;
import sleepwalker.architectsdream.network.shell.ItemMakerShell;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.exseption.NBTParseException;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.engine.StructureEngineItemMaker;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EngineSerialItemMaker implements IEngineSerializer<StructureEngineItemMaker> {

    @Override
    public void serialize(StructureEngineItemMaker src, CompoundNBT nbtProperties) {

        CompoundNBT itemsOutput = new CompoundNBT();

        src.getOutputItems().forEach(itemStack ->
            itemsOutput.putInt(itemStack.getItem().getRegistryName().toString(), itemStack.getCount())
        );

        nbtProperties.put(R.EngineItemMaker.ITEMS_OUTPUT, itemsOutput);
    }

    @Override
    @Nonnull
    public ResourceLocation getRegistryName() {
        return R.EngineItemMaker.NAME;
    }

    @Override
    public void serializeShell(@Nonnull Blueprint blueprint, @Nonnull PacketBuffer buffer) {

        IEngineSerializer.super.serializeShell(blueprint, buffer);

        StructureEngineItemMaker engine = (StructureEngineItemMaker)blueprint.getStructure().getEngine();

        buffer.writeInt(engine.getOutputItems().size());

        engine.getOutputItems().forEach(itemStack -> {
            buffer.writeItemStack(itemStack, true);
        });
    }

    @Override
    public BlueprintShell deserializeShell(@Nonnull PacketBuffer buffer) {

        ResourceLocation id = buffer.readResourceLocation();

        Blueprint.Rarity rarity = buffer.readEnum(Blueprint.Rarity.class);

        int iconId = buffer.readInt();

        int size = buffer.readInt();

        List<ItemStack> itemStacks = new ArrayList<>(size);

        for(int i = 0; i < size; i++){
            itemStacks.add(buffer.readItem());
        }

        return new ItemMakerShell(id, rarity, iconId == -1 ? null : new ItemStack(Item.byId(iconId)), itemStacks);
    }

    @Override
    @Nonnull
    public StructureEngineItemMaker deserialize(@Nonnull CompoundNBT nbtProperties) throws NBTParseException {

        CompoundNBT nbtItemsOutput = nbtProperties.getCompound(R.EngineItemMaker.ITEMS_OUTPUT);

        if(nbtItemsOutput.size() == 0)
            throw new NBTParseException(String.format(R.EXSEPTION_EMPTY_OBJECT, R.EngineItemMaker.ITEMS_OUTPUT));

        NonNullList<ItemStack> itemsOutput = NonNullList.create();

        for(String key : nbtItemsOutput.getAllKeys()){

            ResourceLocation id = new ResourceLocation(key);

            Item item = ForgeRegistries.ITEMS.getValue(id);

            if(item == null)
                throw new NBTParseException(String.format(R.EXSEPTION_ITEM_NOTEXIST, id));

            itemsOutput.add(new ItemStack(item, nbtItemsOutput.getInt(key)));
        }

        return new StructureEngineItemMaker(itemsOutput);
    }
}