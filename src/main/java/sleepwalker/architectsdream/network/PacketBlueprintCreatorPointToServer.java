package sleepwalker.architectsdream.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.ValidatorMode;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PacketBlueprintCreatorPointToServer {

    private final boolean near;
    private final BlockPos pos;
    private final Hand handIn;

    public PacketBlueprintCreatorPointToServer(boolean near, BlockPos pos, Hand handIn){
        this.near = near;
        this.pos = pos;
        this.handIn = handIn;
    }

    public static void writePacketData(@Nonnull PacketBlueprintCreatorPointToServer pTemplate, @Nonnull PacketBuffer buffer){
        buffer.writeBoolean(pTemplate.near);
        buffer.writeBlockPos(pTemplate.pos);
        buffer.writeBoolean(pTemplate.handIn == Hand.MAIN_HAND);
    }

    @Nonnull
    public static PacketBlueprintCreatorPointToServer readPacketData(@Nonnull PacketBuffer buffer){
        return new PacketBlueprintCreatorPointToServer(
                buffer.readBoolean(),
                buffer.readBlockPos(),
                buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND
        );
    }

    public static void processPacket(PacketBlueprintCreatorPointToServer pTemplate, @Nonnull Supplier<NetworkEvent.Context> supplier) {

        if(supplier.get().getDirection().getReceptionSide().isServer()){
            supplier.get().enqueueWork(() -> {

                ServerPlayerEntity player = supplier.get().getSender();

                if(player != null){

                    ItemStack blueprint = player.getItemInHand(pTemplate.handIn);

                    if(blueprint.getItem() == Items.BlueprintCreator.get()){

                        CompoundNBT itemStackNBT = blueprint.getOrCreateTag();

                        ValidatorMode activeMode;

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
                                        pTemplate.pos.getX(),
                                        pTemplate.pos.getY(),
                                        pTemplate.pos.getZ()
                                    }
                                );
                            }
                            else {

                                if(pTemplate.near){
                                    pointsData.putIntArray(
                                        modeName,
                                        new int[] {
                                            points[0],
                                            points[1],
                                            points[2],
                                            pTemplate.pos.getX(),
                                            pTemplate.pos.getY(),
                                            pTemplate.pos.getZ()
                                        }
                                    );
                                }
                                else {
                                    pointsData.putIntArray(
                                        modeName,
                                        new int[] {
                                            pTemplate.pos.getX(),
                                            pTemplate.pos.getY(),
                                            pTemplate.pos.getZ(),
                                            points[3],
                                            points[4],
                                            points[5]
                                        }
                                    );
                                }
                            }
                        }
                        else {
                            pointsData.putIntArray(
                                    modeName,
                                    new int[] { pTemplate.pos.getX(), pTemplate.pos.getY(), pTemplate.pos.getZ() }
                            );
                        }

                        itemStackNBT.put(R.BlueprintCreator.POINTS_DATA, pointsData);

                        blueprint.setTag(itemStackNBT);
                    }
                }
            });
        }
        supplier.get().setPacketHandled(true);
    }
}
