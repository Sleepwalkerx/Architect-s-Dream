package sleepwalker.architectsdream.events;

import net.minecraftforge.eventbus.api.Event;
import sleepwalker.architectsdream.network.shell.BlueprintShell;

import javax.annotation.Nonnull;
import java.util.List;

public class BlueprintsLoadEvent extends Event {

    @Nonnull
    private final List<BlueprintShell> shells;

    public BlueprintsLoadEvent(@Nonnull List<BlueprintShell> shells){

        this.shells = shells;
    }

    @Nonnull
    public List<BlueprintShell> getShells() {
        return shells;
    }
}
