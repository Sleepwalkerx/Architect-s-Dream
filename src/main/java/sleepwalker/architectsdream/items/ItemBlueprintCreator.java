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
        tryChangeData(event.getPos(), event.getItemStack(), event.getWorld());
    }

    private static void tryChangeData(Vector3i pos, ItemStack itemStack, @Nonnull World worldIn){

        if(worldIn.isClientSide || itemStack.getItem() != Items.BlueprintCreator.get())
            return;

        ValidatorMode activeMode;

        CompoundNBT itemStackNBT = itemStack.getOrCreateTag();

        if(!itemStackNBT.contains(R.BlueprintCreator.VALIDATOR_MODE, NBTTypes.STRING)){
            itemStackNBT.putString(R.BlueprintCreator.VALIDATOR_MODE, ValidatorMode.CONST.getRegistryName().toString());
            activeMode = ValidatorMode.CONST;
        }
        else {

            activeMode = ValidatorMode.getValidator(new ResourceLocation(itemStackNBT.getString(R.BlueprintCreator.VALIDATOR_MODE)));
        }

        if(activeMode == null)
            return;

        String modeName = activeMode.getRegistryName().toString();

        CompoundNBT pointsData = itemStackNBT.getCompound(R.BlueprintCreator.POINTS_DATA);

        if(pointsData.contains(modeName, NBTTypes.INT_ARRAY)){
            int[] points = pointsData.getIntArray(modeName);
            if(points.length == 3){
                pointsData.putIntArray(
                    modeName,
                    new int[] { 
                        points[0],
                        points[1],
                        points[2],
                        pos.getX(), 
                        pos.getY(), 
                        pos.getZ() 
                    }
                );
            }
            else {

                boolean switchPoint = itemStackNBT.getBoolean(R.BlueprintCreator.SWITCH_POINT);

                if(switchPoint){
                    pointsData.putIntArray(
                        modeName,
                        new int[] { 
                            points[0],
                            points[1],
                            points[2],
                            pos.getX(), 
                            pos.getY(), 
                            pos.getZ() 
                        }
                    );
                }
                else {
                    pointsData.putIntArray(
                        modeName,
                        new int[] { 
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            points[3], 
                            points[4], 
                            points[5]
                        }
                    );
                }

                itemStackNBT.putBoolean(R.BlueprintCreator.SWITCH_POINT, !switchPoint);
            }
        }
        else {
            pointsData.putIntArray(
                modeName,
                new int[] { pos.getX(), pos.getY(), pos.getZ() }
            );
        }

        itemStackNBT.put(R.BlueprintCreator.POINTS_DATA, pointsData);
        itemStack.setTag(itemStackNBT);
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