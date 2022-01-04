package sleepwalker.architectsdream.plugins.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.ItemStack;
import sleepwalker.architectsdream.R;

import javax.annotation.Nonnull;

public class BlueprintSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {

    public static final BlueprintSubtypeInterpreter INSTANCE = new BlueprintSubtypeInterpreter();

    @Override
    public String apply(@Nonnull ItemStack ingredient, UidContext context) {
        return ingredient.getOrCreateTag().getString(R.Blueprint.BLUEPRINT_NAME);
    }
}
