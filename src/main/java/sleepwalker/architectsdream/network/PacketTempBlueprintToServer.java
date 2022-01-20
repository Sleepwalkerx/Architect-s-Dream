package sleepwalker.architectsdream.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.utils.NBTTypes;

import javax.annotation.Nonnull;

public final class PacketTempBlueprintToServer {

    private final CompoundNBT compoundNBT;
    private final Hand handIn;

    public PacketTempBlueprintToServer(CompoundNBT compoundNBT, Hand handIn){
        this.compoundNBT = compoundNBT;
        this.handIn = handIn;
    }

    public static void writePacketData(@Nonnull PacketTempBlueprintToServer pTemplate, @Nonnull PacketBuffer buffer){
        buffer.writeNbt(pTemplate.compoundNBT);
        buffer.writeBoolean(pTemplate.handIn == Hand.MAIN_HAND);
    }

    @Nonnull
    public static PacketTempBlueprintToServer readPacketData(@Nonnull PacketBuffer buffer){
        return new PacketTempBlueprintToServer(
            buffer.readNbt(),
            buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND
        );
    }

    public static void processPacket(PacketTempBlueprintToServer pTemplate, @Nonnull Supplier<NetworkEvent.Context> supplier) {

        if(supplier.get().getDirection().getReceptionSide().isServer()){
            supplier.get().enqueueWork(() -> {

                ServerPlayerEntity player = supplier.get().getSender();

                if(player != null){

                    ItemStack blueprint = player.getItemInHand(pTemplate.handIn);

                    if(blueprint.getItem() == Items.BlueprintCreator.get()){

                        CompoundNBT compoundNBT = blueprint.getOrCreateTag();

                        compoundNBT.merge(pTemplate.compoundNBT);

                        //compoundNBT.putString(R.BlueprintCreator.CURRENT_SCREEN, pTemplate.compoundNBT.getString(R.BlueprintCreator.CURRENT_SCREEN));
                        //compoundNBT.put(R.BlueprintCreator.SCREENS_DATA, pTemplate.compoundNBT.getList(R.BlueprintCreator.SCREENS_DATA, NBTTypes.OBJECT));

                        blueprint.setTag(compoundNBT);
                    }
                }
            });
        }
        supplier.get().setPacketHandled(true);
    }
}
