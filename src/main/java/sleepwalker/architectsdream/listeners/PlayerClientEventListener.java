package sleepwalker.architectsdream.listeners;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleepwalker.architectsdream.client.resources.ShellManager;

@OnlyIn(Dist.CLIENT)
public class PlayerClientEventListener {

    @SubscribeEvent
    public static void onPlayerOut(ClientPlayerNetworkEvent.LoggedOutEvent event){
        ShellManager.getClientStorage().clear();
    }
}
