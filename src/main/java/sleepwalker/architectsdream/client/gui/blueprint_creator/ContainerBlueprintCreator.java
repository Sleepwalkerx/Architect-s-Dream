package sleepwalker.architectsdream.client.gui.blueprint_creator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import sleepwalker.architectsdream.init.Containers;

public final class ContainerBlueprintCreator extends Container {
    private final ItemStack blueprint;
    private final Hand handIn;

    public static ContainerBlueprintCreator fromNetwork(
        int id, 
        PlayerInventory playerInventoryIn, 
        PacketBuffer buffer
    ){
        return new ContainerBlueprintCreator(
            id, 
            playerInventoryIn, 
            buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND
        );
    }

    public ContainerBlueprintCreator(int id, PlayerInventory playerInventoryIn, Hand handIn) {
        super(Containers.TypeBlueprintMaker.get(), id);

        this.handIn = handIn;
        this.blueprint = playerInventoryIn.player.getItemInHand(handIn);

        for(int y = 0; y < 3; ++y) {
            for(int x = 0; x < 9; ++x) {
               addSlot(new Slot(playerInventoryIn, x + y * 9 + 9, 111 + x * 18, 194 + y * 18));
            }
        }
   
        for(int x = 0; x < 9; ++x) {
            addSlot(new Slot(playerInventoryIn, x, 111 + x * 18, 252));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        ItemStack main = playerIn.getMainHandItem();
        ItemStack off = playerIn.getOffhandItem();
        return !main.isEmpty() && main == blueprint || !off.isEmpty() && off == blueprint;
    }

    public ItemStack getBlueprint() {
        return blueprint;
    }

    public Hand getHandIn() {
        return handIn;
    }
}
