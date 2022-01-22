package sleepwalker.architectsdream.plugins.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.Focus;
import mezz.jei.plugins.jei.info.IngredientInfoRecipe;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.resources.ShellManager;
import sleepwalker.architectsdream.events.BlueprintsLoadEvent;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.network.shell.BlueprintShell;
import sleepwalker.architectsdream.network.shell.ItemMakerShell;
import sleepwalker.architectsdream.utils.BlueprintUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class JeiSleepPlugin implements IModPlugin {

    private final ResourceLocation UUID = new ResourceLocation(ArchitectsDream.MODID,"bebra");
    public static IJeiRuntime runtime;

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return UUID;
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {

        runtime = jeiRuntime;

        if(ShellManager.isLoad){
            loadBlueprints(ShellManager.getClientStorage().values());
        }
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {

        registration.addRecipeCategories(new ItemMakerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerItemSubtypes(@Nonnull ISubtypeRegistration registration) {

        registration.registerSubtypeInterpreter(Items.Blueprint.get(), BlueprintSubtypeInterpreter.INSTANCE);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onBlueprintsLoadEvent(@Nonnull BlueprintsLoadEvent event) {

        if(runtime != null){
            loadBlueprints(event.getShells());
        }
    }

    public static void loadBlueprints(@Nonnull Collection<BlueprintShell> shells){

        ITextComponent[] iTextComponents = new ITextComponent[3];

        iTextComponents[0] = new TranslationTextComponent("gui.jei.blueprint_info_1").withStyle(TextFormatting.YELLOW);
        iTextComponents[1] = new TranslationTextComponent("gui.jei.blueprint_info_2");
        iTextComponents[2] = new TranslationTextComponent("gui.jei.blueprint_info_3");

        List<ItemStack> items = new ArrayList<>(shells.size());

        shells.forEach(structureShell -> {

            ItemStack stack = BlueprintUtils.setBlueprintToItem(structureShell.getId());

            boolean flag = runtime.getRecipeManager().getRecipeCategories(new Focus<>(IFocus.Mode.OUTPUT, stack), true).isEmpty();

            if(flag){

                List<IngredientInfoRecipe<ItemStack>> infoRecipes = IngredientInfoRecipe.create(
                        Collections.singletonList(stack),
                        VanillaTypes.ITEM,
                        iTextComponents
                );

                runtime.getRecipeManager().addRecipe(infoRecipes.get(0), VanillaRecipeCategoryUid.INFORMATION);
            }

            items.add(stack);

            if(!(structureShell instanceof ItemMakerShell)){
                return;
            }

            ItemMakerShell itemMaker = (ItemMakerShell)structureShell;

            if(itemMaker.getItemStacks().size() != 0){

                runtime.getRecipeManager().addRecipe(new ItemMakerRecipe(itemMaker.getItemStacks(), stack), ItemMakerRecipeCategory.UUID);
            }
        });

        if(items.size() != 0){

            runtime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM, items);
        }

        System.out.println();
    }

    /*@Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {

        registration.getCraftingCategory().addCategoryExtension(RecipeCraftingShapedOverride.class, RecipeCraftingExtension::new);
    }*/

    public static void blit9Part(
        MatrixStack matrixStack,
        float u0, float v0,
        int uWidth, int vHeight,
        int width, int height,
        int x, int y,
        int texWidth, int texHeight,
        int borderSize
    ){
        GL11.glEnable(GL11.GL_BLEND);

        int dBorder = borderSize * 2;

        AbstractGui.blit(
            matrixStack,
            x, y,
            borderSize, borderSize,
            u0, v0,
            borderSize, borderSize,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x + borderSize, y,
            width, borderSize,
            u0 + borderSize, v0,
            uWidth - dBorder, borderSize,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x + borderSize + width, y,
            borderSize, borderSize,
            u0 + uWidth - borderSize, v0,
            borderSize, borderSize,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x + borderSize + width, y + borderSize,
            borderSize, height,
            u0 + uWidth - borderSize, v0 + borderSize,
            borderSize, vHeight - dBorder,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x + borderSize + width, y + borderSize + height,
            borderSize, borderSize,
            u0 + uWidth - borderSize, v0 + vHeight - borderSize,
            borderSize, borderSize,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x + borderSize, y + borderSize + height,
            width, borderSize,
            u0 + borderSize, v0 + vHeight - borderSize,
            uWidth - dBorder, borderSize,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x, y + borderSize + height,
            borderSize, borderSize,
            u0, v0 + vHeight - borderSize,
            borderSize, borderSize,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x, y + borderSize,
            borderSize, height,
            u0, v0 + borderSize,
            borderSize, vHeight - dBorder,
            texWidth, texHeight
        );

        AbstractGui.blit(
            matrixStack,
            x + borderSize, y + borderSize,
            width, height,
            u0 + borderSize, v0 + borderSize,
            uWidth - dBorder, vHeight - dBorder,
            texWidth, texHeight
        );

        GL11.glDisable(GL11.GL_BLEND);
    }
}
