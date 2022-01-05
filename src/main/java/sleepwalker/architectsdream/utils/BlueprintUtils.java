package sleepwalker.architectsdream.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.resources.ShellManager;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.network.shell.BlueprintShell;
import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.serialize.converters.BlueprintPropertiesSerializer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.Blueprint.Rarity;
import sleepwalker.architectsdream.structure.EnumCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static sleepwalker.architectsdream.R.Blueprint.BLUEPRINT_NAME;

public final class BlueprintUtils {

    private BlueprintUtils(){ throw new IllegalStateException("Utility class"); }

    @Nonnull
    public static ItemStack setBlueprintToItem(@Nonnull ResourceLocation id){

        ItemStack itemStack = new ItemStack(Items.Blueprint.get());

        CompoundNBT tagCompound = new CompoundNBT();

        tagCompound.putString(BLUEPRINT_NAME, id.toString());

        itemStack.setTag(tagCompound);

        return itemStack;
    }

    /*@Nonnull
    public static ItemStack setBlueprintToItem(@Nonnull ItemStack stack, @Nonnull Blueprint blueprint){
        return setBlueprintToItem(stack, blueprint.getID());
    }*/

    @Nullable
    public static Blueprint getStructureFromItem(@Nonnull ItemStack itemStack){

        ResourceLocation id = getBlueprintIdFromItem(itemStack);

        if(id != null){
            return BlueprintManager.getBlueprint(id);
        }
        else return null;
    }

    public static @Nullable ResourceLocation getBlueprintIdFromItem(@Nonnull ItemStack itemStack){

        if(itemStack.getOrCreateTag().contains(BLUEPRINT_NAME, NBTTypes.STRING)){

            return new ResourceLocation(itemStack.getTag().getString(BLUEPRINT_NAME));
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static Blueprint.Properties getItemStackClientProperties(@Nonnull ItemStack stack){

        if(stack.getOrCreateTag().contains(R.Properties.NAME, NBTTypes.OBJECT)){

            return BlueprintPropertiesSerializer.deserialize(stack.getTag().getCompound(R.Properties.NAME));
        }
        else {

            ResourceLocation id = new ResourceLocation(stack.getTag().getString(BLUEPRINT_NAME));

            BlueprintShell shell = ShellManager.getClientStorage().get(id);

            if(shell == null){
                return Blueprint.Properties.DEFAULT;
            }
            else return shell.getDefaultProperties();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static Rarity getBlueprintRarity(@Nonnull ItemStack itemStack){

        if(itemStack.hasTag()){

            ResourceLocation location = new ResourceLocation(itemStack.getOrCreateTag().getString(BLUEPRINT_NAME));

            BlueprintShell shell = ShellManager.getClientStorage().get(location);

            if(shell != null) {
                return shell.getRarity();
            }
        }

        return Rarity.SIMPLE;
    }
}
