package sleepwalker.architectsdream.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import sleepwalker.architectsdream.init.Items;

public final class PacketTempBlueprintToServer {

    private final CompoundNBT compoundNBT;
    private final Hand handIn;

    public PacketTempBlueprintToServer(CompoundNBT compoundNBT, Hand handIn){
        this.compoundNBT = compoundNBT;
        this.handIn = handIn;
    }

    public static void writePacketData(PacketTempBlueprintToServer pTemplate, PacketBuffer buffer){
        buffer.writeNbt(pTemplate.compoundNBT);
        buffer.writeBoolean(pTemplate.handIn == Hand.MAIN_HAND);
    }

    public static PacketTempBlueprintToServer readPacketData(PacketBuffer buffer){
        return new PacketTempBlueprintToServer(
            buffer.readNbt(),
            buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND
        );
    }

    public static void processPacket(PacketTempBlueprintToServer pTemplate, Supplier<NetworkEvent.Context> supplier) {

        if(supplier.get().getDirection().getReceptionSide().isServer()){
            supplier.get().enqueueWork(() -> {
                ServerPlayerEntity player = supplier.get().getSender();

                ItemStack blueprint = player.getItemInHand(pTemplate.handIn);

                if(blueprint.getItem() == Items.BlueprintCreator.get()){
                    CompoundNBT compoundNBT =  blueprint.getOrCreateTag();

                    for(String key : pTemplate.compoundNBT.getAllKeys()){
                        compoundNBT.put(key, pTemplate.compoundNBT.get(key));
                    }

                    compoundNBT.merge(pTemplate.compoundNBT);

                    blueprint.setTag(compoundNBT);
                }
            });
        }
        supplier.get().setPacketHandled(true);
    }
}
