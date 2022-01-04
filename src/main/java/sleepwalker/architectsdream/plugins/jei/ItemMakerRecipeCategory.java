package sleepwalker.architectsdream.plugins.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.R;

import javax.annotation.Nonnull;
import java.util.Collections;

public class ItemMakerRecipeCategory implements IRecipeCategory<ItemMakerRecipe> {

    private static final int recipeWidth = 160;
    private static final int recipeHeight = 125;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotBackground;
    private final ITextComponent localizedName;

    public static final ResourceLocation UUID = new ResourceLocation(ArchitectsDream.MODID, R.Jei.ITEM_MAKER_NAME);

    public ItemMakerRecipeCategory(IGuiHelper guiHelper) {

        this.background = guiHelper.createBlankDrawable(recipeWidth, recipeHeight);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Items.Blueprint.get()));
        this.slotBackground = guiHelper.getSlotDrawable();
        this.localizedName = new TranslationTextComponent(String.format("gui.jei.category.%s.%s", ArchitectsDream.MODID, R.Jei.ITEM_MAKER_NAME));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return UUID;
    }

    @Nonnull
    @Override
    public Class<? extends ItemMakerRecipe> getRecipeClass() {
        return ItemMakerRecipe.class;
    }

    @Nonnull
    @Override
    @Deprecated
    public String getTitle() {
        return getTitleAsTextComponent().getString();
    }

    @Nonnull
    @Override
    public ITextComponent getTitleAsTextComponent() {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setIngredients(@Nonnull ItemMakerRecipe recipe, @Nonnull IIngredients ingredients) {

        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(Collections.singletonList(recipe.getBlueprintItem())));

        ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getItems()));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull ItemMakerRecipe recipe, @Nonnull IIngredients ingredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 74, 9);
        guiItemStacks.setBackground(0, slotBackground);
        guiItemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        int slotIndex = 1;

        for (int i = 0; i < ingredients.getOutputs(VanillaTypes.ITEM).get(0).size(); i++) {

            final int slotX = 2 + i % 9 * 18;
            final int slotY = 36 + i / 9 * 18;

            ItemStack output = ingredients.getOutputs(VanillaTypes.ITEM).get(0).get(i);

            guiItemStacks.init(slotIndex + i, false, slotX, slotY);
            guiItemStacks.set(slotIndex + i, output);
        }
    }
}
