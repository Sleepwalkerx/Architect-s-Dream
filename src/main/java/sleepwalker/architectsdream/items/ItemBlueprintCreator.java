package sleepwalker.architectsdream.items;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import sleepwalker.architectsdream.client.gui.blueprint_creator.ContainerBlueprintCreator;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.ValidatorMode;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.network.PacketBlueprintCreatorPointToServer;
import sleepwalker.architectsdream.network.PacketHandler;
import sleepwalker.architectsdream.utils.NBTTypes;

public class ItemBlueprintCreator extends Item {
    public ItemBlueprintCreator(Item.Properties properties){
        super(properties);
    }

    @Override
    public boolean canAttackBlock(@Nonnull BlockState blockState, @Nonnull World world, @Nonnull BlockPos blockPos,
                                  @Nonnull PlayerEntity playerEntity) {
        return false;
    }

    @SubscribeEvent
    public static void onLeftClickBlock(@Nonnull LeftClickBlock event){

        if(event.getWorld().isClientSide && event.getItemStack().getItem() == Items.BlueprintCreator.get()){

            PacketHandler.INSTANCE.sendToServer(new PacketBlueprintCreatorPointToServer(Screen.hasAltDown(), event.getPos(), event.getHand()));
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if(!worldIn.isClientSide){
            NetworkHooks.openGui(
                (ServerPlayerEntity) playerIn,
                new SimpleNamedContainerProvider(
                    (id, playerInventoryIn, player) -> new ContainerBlueprintCreator(id, playerInventoryIn, handIn),
                    stack.getDisplayName()
                ),
                buffer -> buffer.writeBoolean(handIn == Hand.MAIN_HAND)
            );
            return ActionResult.success(stack);
        }
        return ActionResult.pass(stack);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public ITextComponent getName(ItemStack stack) {

        if(stack.hasTag() && stack.getTag().contains(R.BlueprintCreator.NAME, NBTTypes.STRING))
            return new StringTextComponent(stack.getTag().getString(R.BlueprintCreator.NAME));
        else 
            return super.getName(stack);
    }
}