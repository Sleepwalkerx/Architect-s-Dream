package sleepwalker.architectsdream.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ContainerBlueprintViewer;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.Blueprint.Rarity;
import sleepwalker.architectsdream.structure.Blueprint.Result;
import sleepwalker.architectsdream.utils.BlueprintUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlueprint extends Item {

    public ItemBlueprint(Item.Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @Nonnull
    public ITextComponent getName(@Nonnull ItemStack stack) {

        ResourceLocation id = BlueprintUtils.getBlueprintIdFromItem(stack);

        if(id != null){

            return convertName(id, stack);
        }
        else return new TranslationTextComponent(BlueprintUtils.getBlueprintCondition(stack).translationKey);
    }

    @Nonnull
    private TranslationTextComponent convertName(@Nonnull ResourceLocation id, @Nonnull ItemStack stack){

        String name = I18n.get(String.format("blueprint.%s.%s.name", id.getNamespace(), id.getPath()));

        int start = name.indexOf("${");
        int end;

        while (start != -1){

             end = name.indexOf("}");

             if(end == -1 || end - 2 == start){
                 break;
             }

            String otherName = name.substring(start + 2, end);

            name = name.substring(0, start) +  I18n.get(otherName) + name.substring(end + 1);

            start = name.indexOf("${");
        }

        return new TranslationTextComponent(
            BlueprintUtils.getBlueprintCondition(stack).translationKey,
            name
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(BlueprintUtils.getBlueprintRarity(stack).getDisplayName());
    }

    // TODO: net.minecraftforge.event.ForgeEventFactory.onItemTooltip

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(@Nonnull World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {

        ItemStack stack = playerIn.getItemInHand(handIn);

        if(!worldIn.isClientSide){

            Blueprint blueprint = BlueprintUtils.getStructureFromItem(stack);

            if(blueprint == null){
                stack.setCount(0);
            }
            else {
                NetworkHooks.openGui(
                    (ServerPlayerEntity) playerIn,
                    new SimpleNamedContainerProvider(
                        (id, playerInventoryIn, player) -> new ContainerBlueprintViewer(id, playerInventoryIn, handIn, blueprint),
                        stack.getDisplayName()
                    ),
                    buffer -> buffer.writeBoolean(handIn == Hand.MAIN_HAND)
                );
            }
        }

        return ActionResult.success(stack);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        return super.onItemUseFirst(stack, context);
    }

    @Override
    @Nonnull
    public ActionResultType useOn(@Nonnull ItemUseContext itemContext) {

        if (!itemContext.getLevel().isClientSide && itemContext.getPlayer() != null) {

            Blueprint blueprint = BlueprintUtils.getStructureFromItem(itemContext.getItemInHand());

            if(blueprint == null){

                itemContext.getItemInHand().setCount(0);

                return ActionResultType.SUCCESS;
            }

            Result resultMatches = blueprint.matches(itemContext);

            itemContext.getPlayer().displayClientMessage(resultMatches.textMessage, false);
        }

        return ActionResultType.SUCCESS;
    }

    /*@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        playerIn.openGui(ArchitectsDream.MODID, handIn.ordinal() + 1, worldIn, 5, 5, 5);
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }*/

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return BlueprintUtils.getBlueprintRarity(stack) == Rarity.LEGENDARY;
    }
}
