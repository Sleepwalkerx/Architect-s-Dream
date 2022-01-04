package sleepwalker.architectsdream.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.ScreenBlueprintViewer;
import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.structure.Blueprint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class PacketBlueprintToClient {

    @Nonnull
    private final Blueprint blueprint;

    public PacketBlueprintToClient(Blueprint blueprint){
        this.blueprint = blueprint;
    }

    public static void writePacketData(PacketBlueprintToClient packet, PacketBuffer buffer){
        buffer.writeNbt(packet.blueprint.getSource());
        buffer.writeResourceLocation(packet.blueprint.getID());
    }

    @Nullable
    public static PacketBlueprintToClient readPacketData(PacketBuffer buffer){

        try {
            return new PacketBlueprintToClient(SerializerManager.deserialize(
                buffer.readNbt(),
                buffer.readResourceLocation()
            ));
        }
        catch (Exception e){
            ArchitectsDream.LOGGER.error("The structure file cannot be read from the network", e);
            return null;
        }
    }

    public static void processPacket(PacketBlueprintToClient packet, Supplier<NetworkEvent.Context> supplier) {
        if(supplier.get().getDirection().getReceptionSide().isClient()){
            supplier.get().enqueueWork(() -> {
                if(packet == null) return;

                if(Minecraft.getInstance().screen instanceof ScreenBlueprintViewer){
                    ((ScreenBlueprintViewer)Minecraft.getInstance().screen).loadBlueprint(packet.blueprint);
                    BlueprintManager.addBlueprint(packet.blueprint);
                }
            });
        }
        supplier.get().setPacketHandled(true);
    }
}
