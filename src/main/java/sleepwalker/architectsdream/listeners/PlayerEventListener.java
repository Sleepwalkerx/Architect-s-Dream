package sleepwalker.architectsdream.listeners;


import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import sleepwalker.architectsdream.ArchitectsDream;
import sleepwalker.architectsdream.client.resources.ShellManager;
import sleepwalker.architectsdream.network.PacketHandler;
import sleepwalker.architectsdream.network.PacketStorageBlueprintsToClient;
import sleepwalker.architectsdream.resources.BlueprintManager;

import java.util.Collections;

public class PlayerEventListener {

    private PlayerEventListener() {
    }

    @SubscribeEvent
    public static void onPlayerConnected(PlayerEvent.PlayerLoggedInEvent event) {

        if (BlueprintManager.getBlueprints().isEmpty()) return;

        PacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()),
                new PacketStorageBlueprintsToClient(Collections.emptyList())
        );
    }
}
