package sleepwalker.architectsdream.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;
import sleepwalker.architectsdream.client.ISavableHandler;
import sleepwalker.architectsdream.init.Items;

import java.util.function.Supplier;

public final class PacketBlueprintToServer {

    private final Hand handIn;
    private final CompoundNBT compoundNBT;

    public PacketBlueprintToServer(Hand handIn, CompoundNBT compoundNBT){
        this.handIn = handIn;
        this.compoundNBT = compoundNBT;
    }


    public static void writePacketData(PacketBlueprintToServer blueprint, PacketBuffer buffer){
        buffer.writeEnum(blueprint.handIn);
        buffer.writeNbt(blueprint.compoundNBT);
    }

    public static PacketBlueprintToServer readPacketData(PacketBuffer buffer){
        return new PacketBlueprintToServer(
            buffer.readEnum(Hand.class),
            buffer.readNbt()
        );
    }

    public static void processPacket(PacketBlueprintToServer pBlueprint, Supplier<NetworkEvent.Context> supplier) {
        if(supplier.get().getDirection().getReceptionSide().isServer()){
            supplier.get().enqueueWork(() -> {

                ServerPlayerEntity player = supplier.get().getSender();

                if(player == null)
                    return;

                ItemStack blueprint = player.getItemInHand(pBlueprint.handIn);

                if(blueprint.getItem() == Items.Blueprint.get()){

                    CompoundNBT itemNbt = blueprint.getOrCreateTag();

                    itemNbt.put(ISavableHandler.COMPOUND_SAVABLE, pBlueprint.compoundNBT);

                    blueprint.setTag(itemNbt);
                }
            });
        }
        supplier.get().setPacketHandled(true);
    }
}
