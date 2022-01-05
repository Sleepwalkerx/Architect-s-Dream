package sleepwalker.architectsdream.serialize.engine;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import sleepwalker.architectsdream.exseption.NBTParseException;
import sleepwalker.architectsdream.network.shell.BlueprintShell;
import sleepwalker.architectsdream.serialize.converters.BlueprintPropertiesSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.engine.BaseStructureEngine;

import javax.annotation.Nonnull;

public interface IEngineSerializer<T extends BaseStructureEngine> {

    void serialize(T src, CompoundNBT nbtProperties);

    @Nonnull
    BaseStructureEngine deserialize(CompoundNBT nbtProperties) throws NBTParseException;

    default void serializeShell(@Nonnull Blueprint blueprint, @Nonnull PacketBuffer buffer){

        Blueprint.Structure structure = blueprint.getStructure();

        buffer.writeResourceLocation(blueprint.getID());
        buffer.writeEnum(structure.getRarity());
        buffer.writeInt(structure.getEngine().getIcon() == null ? -1 : Item.getId(structure.getEngine().getIcon().getItem()));

        buffer.writeNbt(BlueprintPropertiesSerializer.serialize(blueprint.getProperties()));
    }

    default BlueprintShell deserializeShell(@Nonnull PacketBuffer buffer){

        ResourceLocation id = buffer.readResourceLocation();

        Blueprint.Rarity rarity = buffer.readEnum(Blueprint.Rarity.class);

        int iconId = buffer.readInt();

        Blueprint.Properties properties = BlueprintPropertiesSerializer.deserialize(buffer.readAnySizeNbt());

        return new BlueprintShell(id, rarity, properties, iconId == -1 ? null : new ItemStack(Item.byId(iconId)));
    }

    @Nonnull
    ResourceLocation getRegistryName();
}