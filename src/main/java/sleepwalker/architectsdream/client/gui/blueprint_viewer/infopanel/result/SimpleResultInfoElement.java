package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.result;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.widget.BaseScrollItemElement;
import sleepwalker.architectsdream.client.gui.widget.BaseScrollItemTooltips;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class SimpleResultInfoElement extends BaseScrollItemElement {

    public SimpleResultInfoElement(ItemStack stack) {
        super(stack);
    }

    @Override
    public boolean canShowInStructureViewer() {
        return false;
    }

    @Override
    public boolean canShowInInfoPanel() {
        return true;
    }
}
