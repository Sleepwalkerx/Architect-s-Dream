package sleepwalker.architectsdream.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;
import sleepwalker.architectsdream.events.BlueprintsLoadEvent;
import sleepwalker.architectsdream.network.shell.BlueprintShell;
import sleepwalker.architectsdream.client.resources.ShellManager;
import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.serialize.SerializerManager;
import sleepwalker.architectsdream.serialize.engine.IEngineSerializer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketStorageBlueprintsToClient {

    @Nonnull
    private final List<BlueprintShell> shells;

    public PacketStorageBlueprintsToClient(@Nonnull List<BlueprintShell> shells){
        this.shells = shells;
    }

    public static void writePacketData(PacketStorageBlueprintsToClient packet, @Nonnull PacketBuffer buffer){

        buffer.writeInt(BlueprintManager.getBlueprints().size());

        BlueprintManager.getBlueprints().forEach(blueprint -> {

            IEngineSerializer<?> serializer = blueprint.getStructure().getEngine().getSerialize();

            buffer.writeResourceLocation(serializer.getRegistryName());

            serializer.serializeShell(blueprint, buffer);
        });
    }

    public static PacketStorageBlueprintsToClient readPacketData(@Nonnull PacketBuffer buffer){

        int size = buffer.readInt();

        List<BlueprintShell> structureShells = new ArrayList<>(size);

        for(int i = 0; i < size; i++){

            ResourceLocation serializerID = buffer.readResourceLocation();

            IEngineSerializer<?> serializer = SerializerManager.ENGINES.get(serializerID);

            if(serializer == null){
                break;
            }

            structureShells.add(serializer.deserializeShell(buffer));
        }

        return new PacketStorageBlueprintsToClient(structureShells);
    }

    // CLIENT ONLY
    public static void processPacket(PacketStorageBlueprintsToClient packet, @Nonnull Supplier<NetworkEvent.Context> supplier) {

        if(supplier.get().getDirection().getReceptionSide().isClient()){

            supplier.get().enqueueWork(() -> {

                ShellManager.loadClientStorage(packet.shells);

                MinecraftForge.EVENT_BUS.post(new BlueprintsLoadEvent(packet.shells));
            });
        }

        supplier.get().setPacketHandled(true);
    }
}
