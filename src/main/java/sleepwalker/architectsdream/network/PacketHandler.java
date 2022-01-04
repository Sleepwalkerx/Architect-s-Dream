package sleepwalker.architectsdream.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sleepwalker.architectsdream.ArchitectsDream;

public final class PacketHandler {
    private PacketHandler(){}

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(ArchitectsDream.MODID, "main"), 
        () -> PROTOCOL_VERSION, 
        PROTOCOL_VERSION::equals, 
        PROTOCOL_VERSION::equals
    );

    public static void init(){

        int id = 0;

        INSTANCE.registerMessage(
            id++, PacketTempBlueprintToServer.class,
            PacketTempBlueprintToServer::writePacketData,
            PacketTempBlueprintToServer::readPacketData,
            PacketTempBlueprintToServer::processPacket
        );

        INSTANCE.registerMessage(
            id++, PacketBlueprintToServer.class,
            PacketBlueprintToServer::writePacketData,
            PacketBlueprintToServer::readPacketData,
            PacketBlueprintToServer::processPacket
        );

        INSTANCE.registerMessage(
            id++, PacketBlueprintToClient.class,
            PacketBlueprintToClient::writePacketData,
            PacketBlueprintToClient::readPacketData,
            PacketBlueprintToClient::processPacket
        );

        INSTANCE.registerMessage(
            id++, PacketBlueprintPresenceToServer.class,
            PacketBlueprintPresenceToServer::writePacketData,
            PacketBlueprintPresenceToServer::readPacketData,
            PacketBlueprintPresenceToServer::processPacket
        );

        INSTANCE.registerMessage(
            id++, PacketStorageBlueprintsToClient.class,
            PacketStorageBlueprintsToClient::writePacketData,
            PacketStorageBlueprintsToClient::readPacketData,
            PacketStorageBlueprintsToClient::processPacket
        );
    }
}
