package sleepwalker.architectsdream.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PacketBlueprintPresenceToServer {

    private final ResourceLocation idStructure;

    public PacketBlueprintPresenceToServer(ResourceLocation id){
        this.idStructure = id;
    }

    public static void writePacketData(@Nonnull PacketBlueprintPresenceToServer packet, @Nonnull PacketBuffer buffer){
        buffer.writeResourceLocation(packet.idStructure);
    }

    @Nonnull
    public static PacketBlueprintPresenceToServer readPacketData(@Nonnull PacketBuffer buffer){
        return new PacketBlueprintPresenceToServer(buffer.readResourceLocation());
    }

    public static void processPacket(PacketBlueprintPresenceToServer packet, @Nonnull Supplier<NetworkEvent.Context> supplier) {
        if(supplier.get().getDirection().getReceptionSide().isServer()){
            supplier.get().enqueueWork(() -> {

                Blueprint blueprint = BlueprintManager.getBlueprint(packet.idStructure);

                if(blueprint != null){
                    PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> supplier.get().getSender()),
                        new PacketBlueprintToClient(blueprint)
                    );
                }
            });
        }
        supplier.get().setPacketHandled(true);
    }
}
