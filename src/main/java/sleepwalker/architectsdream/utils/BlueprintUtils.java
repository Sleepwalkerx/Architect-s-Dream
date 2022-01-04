package sleepwalker.architectsdream.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.resources.ShellManager;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.network.shell.BlueprintShell;
import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.Blueprint.Rarity;
import sleepwalker.architectsdream.structure.EnumCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static sleepwalker.architectsdream.R.Blueprint.BLUEPRINT_NAME;
import static sleepwalker.architectsdream.R.Blueprint.CONDITION;

public final class BlueprintUtils {

    private BlueprintUtils(){ throw new IllegalStateException("Utility class"); }

    @Nonnull
    public static ItemStack setBlueprintToItem(@Nonnull ItemStack itemStack, @Nonnull ResourceLocation id, @Nonnull EnumCondition condition){

        CompoundNBT tagCompound = itemStack.getOrCreateTag();

        tagCompound.putString(BLUEPRINT_NAME, id.toString());
        tagCompound.putString(CONDITION, condition.toString());

        itemStack.setTag(tagCompound);

        return itemStack;
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public static ItemStack genBlueprintFromShell(@Nonnull ResourceLocation id, EnumCondition condition){
        return setBlueprintToItem(new ItemStack(Items.Blueprint.get()), id, condition);
    }

    @Nonnull
    public static ItemStack setBlueprintToItem(@Nonnull ItemStack stack, @Nonnull Blueprint blueprint, @Nonnull EnumCondition condition){
        return setBlueprintToItem(stack, blueprint.getID(), condition);
    }

    public static @Nullable
    Blueprint getStructureFromItem(@Nonnull ItemStack itemStack){
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

    public static EnumCondition getBlueprintCondition(@Nonnull ItemStack itemStack){

        if(itemStack.hasTag()){

            try {
                String name = itemStack.getOrCreateTag().getString(CONDITION);

                return EnumCondition.valueOf(name);
            }
            catch (IllegalArgumentException ignored) { }
        }

        return EnumCondition.WHOLE;
    }

    @OnlyIn(Dist.CLIENT)
    public static Rarity getBlueprintRarity(@Nonnull ItemStack itemStack){

        if(itemStack.hasTag()){

            ResourceLocation location = new ResourceLocation(itemStack.getOrCreateTag().getString(BLUEPRINT_NAME));

            BlueprintShell shell = ShellManager.getClientStorage().get(location);

            if(shell == null){
                return Rarity.NONE;
            }
            else return shell.getRarity();
        }
        return Rarity.SIMPLE;
    }
}
