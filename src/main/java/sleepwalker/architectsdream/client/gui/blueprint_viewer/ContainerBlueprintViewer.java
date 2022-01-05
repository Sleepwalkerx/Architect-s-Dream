package sleepwalker.architectsdream.client.gui.blueprint_viewer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sleepwalker.architectsdream.init.Containers;
import sleepwalker.architectsdream.network.PacketBlueprintPresenceToServer;
import sleepwalker.architectsdream.network.PacketHandler;
import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.utils.BlueprintUtils;

import javax.annotation.Nonnull;

public class ContainerBlueprintViewer extends Container {

    @Nonnull
    final Hand handIn;

    @Nonnull
    final ItemStack itemBlueprint;

    @Nonnull
    Blueprint blueprint;

    @Nonnull
    public static ContainerBlueprintViewer fromNetwork(
            int id,
            PlayerInventory playerInventoryIn,
            @Nonnull PacketBuffer buffer
    ){
        return new ContainerBlueprintViewer(
            id, 
            playerInventoryIn, 
            buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND
        );
    }

    @OnlyIn(Dist.CLIENT)
    public ContainerBlueprintViewer(int id, @Nonnull PlayerInventory playerInventoryIn, @Nonnull Hand handIn) {
        super(Containers.TypeBlueprint.get(), id);

        this.handIn = handIn;
        this.itemBlueprint = playerInventoryIn.player.getItemInHand(handIn);

        ResourceLocation idBlueprint = BlueprintUtils.getBlueprintIdFromItem(this.itemBlueprint);
        Blueprint blueprint = BlueprintManager.getBlueprint(idBlueprint);

        this.blueprint = Blueprint.DEFAULT;

        if(blueprint == null){

            PacketHandler.INSTANCE.sendToServer(new PacketBlueprintPresenceToServer(idBlueprint));
        }
        else {
            this.blueprint = blueprint;
        }
    }

    public ContainerBlueprintViewer(int id, @Nonnull PlayerInventory playerInventoryIn, @Nonnull Hand handIn, @Nonnull Blueprint blueprint) {
        super(Containers.TypeBlueprint.get(), id);

        this.handIn = handIn;
        itemBlueprint = playerInventoryIn.player.getItemInHand(handIn);
        this.blueprint = blueprint;
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        ItemStack main = playerIn.getMainHandItem();
        ItemStack off = playerIn.getOffhandItem();
        return !main.isEmpty() && main == itemBlueprint || !off.isEmpty() && off == itemBlueprint;
    }
}
