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
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.BaseScrollItemTooltips;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class SimpleResultInfoElement extends BaseScrollItemTooltips {

    public SimpleResultInfoElement(ItemStack stack) {
        super(stack);

        genTooltips();
    }


    @Override
    public boolean canShowInStructureViewer() {
        return false;
    }

    @Override
    public boolean canShowInInfoPanel() {
        return true;
    }

    @Override
    protected void genTooltips() {

        tooltips = getTooltipLines(Minecraft.getInstance().player, ITooltipFlag.TooltipFlags.NORMAL)
                .stream()
                .map(component -> new Text(component, 0)).collect(Collectors.toList());

        tooltips.remove(0);

        calcHeight();
    }

    public List<ITextComponent> getTooltipLines(@Nullable PlayerEntity pPlayer, ITooltipFlag pIsAdvanced) {

        List<ITextComponent> list = Lists.newArrayList();

        IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("")).append(itemStack.getHoverName()).withStyle(itemStack.getRarity().color);

        if (itemStack.hasCustomHoverName()) {
            iformattabletextcomponent.withStyle(TextFormatting.ITALIC);
        }

        list.add(iformattabletextcomponent);
        if (!pIsAdvanced.isAdvanced() && !itemStack.hasCustomHoverName() && itemStack.getItem() == Items.FILLED_MAP) {
            list.add((new StringTextComponent("#" + FilledMapItem.getMapId(itemStack))).withStyle(TextFormatting.GRAY));
        }

        int i = itemStack.getHideFlags();
        if (ItemStack.shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.ADDITIONAL)) {
            itemStack.getItem().appendHoverText(itemStack, pPlayer == null ? null : pPlayer.level, list, pIsAdvanced);
        }

        if (itemStack.hasTag()) {
            if (ItemStack.shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
                ItemStack.appendEnchantmentNames(list, itemStack.getEnchantmentTags());
            }

            if (itemStack.getTag().contains("display", 10)) {
                CompoundNBT compoundnbt = itemStack.getTag().getCompound("display");
                if (ItemStack.shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.DYE) && compoundnbt.contains("color", 99)) {
                    if (pIsAdvanced.isAdvanced()) {
                        list.add((new TranslationTextComponent("item.color", String.format("#%06X", compoundnbt.getInt("color")))).withStyle(TextFormatting.GRAY));
                    } else {
                        list.add((new TranslationTextComponent("item.dyed")).withStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
                    }
                }

                if (compoundnbt.getTagType("Lore") == 9) {
                    ListNBT listnbt = compoundnbt.getList("Lore", 8);

                    for(int j = 0; j < listnbt.size(); ++j) {
                        String s = listnbt.getString(j);

                        try {
                            IFormattableTextComponent iformattabletextcomponent1 = ITextComponent.Serializer.fromJson(s);
                            if (iformattabletextcomponent1 != null) {
                                list.add(TextComponentUtils.mergeStyles(iformattabletextcomponent1, ItemStack.LORE_STYLE));
                            }
                        } catch (JsonParseException jsonparseexception) {
                            compoundnbt.remove("Lore");
                        }
                    }
                }
            }
        }

        if (ItemStack.shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.MODIFIERS)) {
            for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
                Multimap<Attribute, AttributeModifier> multimap = itemStack.getAttributeModifiers(equipmentslottype);
                if (!multimap.isEmpty()) {
                    list.add(StringTextComponent.EMPTY);
                    list.add((new TranslationTextComponent("item.modifiers." + equipmentslottype.getName())).withStyle(TextFormatting.GRAY));

                    for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        double d0 = attributemodifier.getAmount();
                        boolean flag = false;
                        if (pPlayer != null) {
                            if (attributemodifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                                d0 = d0 + pPlayer.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                                d0 = d0 + (double) EnchantmentHelper.getDamageBonus(itemStack, CreatureAttribute.UNDEFINED);
                                flag = true;
                            } else if (attributemodifier.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                                d0 += pPlayer.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                                flag = true;
                            }
                        }

                        double d1;
                        if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                                d1 = d0 * 10.0D;
                            } else {
                                d1 = d0;
                            }
                        } else {
                            d1 = d0 * 100.0D;
                        }

                        if (flag) {
                            list.add(
                                    (new StringTextComponent(" "))
                                            .append(new TranslationTextComponent("attribute.modifier.equals." + attributemodifier.getOperation().toValue(),
                                                    ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
                                                    new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.DARK_GREEN));
                        } else if (d0 > 0.0D) {
                            list.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.BLUE));
                        } else if (d0 < 0.0D) {
                            d1 = d1 * -1.0D;
                            list.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.RED));
                        }
                    }
                }
            }
        }

        if (itemStack.hasTag()) {
            if (ItemStack.shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.UNBREAKABLE) && itemStack.getTag().getBoolean("Unbreakable")) {
                list.add((new TranslationTextComponent("item.unbreakable")).withStyle(TextFormatting.BLUE));
            }

            if (ItemStack.shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.CAN_DESTROY) && itemStack.getTag().contains("CanDestroy", 9)) {
                ListNBT listnbt1 = itemStack.getTag().getList("CanDestroy", 8);
                if (!listnbt1.isEmpty()) {
                    list.add(StringTextComponent.EMPTY);
                    list.add((new TranslationTextComponent("item.canBreak")).withStyle(TextFormatting.GRAY));

                    for(int k = 0; k < listnbt1.size(); ++k) {
                        list.addAll(ItemStack.expandBlockState(listnbt1.getString(k)));
                    }
                }
            }

            if (ItemStack.shouldShowInTooltip(i, ItemStack.TooltipDisplayFlags.CAN_PLACE) && itemStack.getTag().contains("CanPlaceOn", 9)) {
                ListNBT listnbt2 = itemStack.getTag().getList("CanPlaceOn", 8);
                if (!listnbt2.isEmpty()) {
                    list.add(StringTextComponent.EMPTY);
                    list.add((new TranslationTextComponent("item.canPlace")).withStyle(TextFormatting.GRAY));

                    for(int l = 0; l < listnbt2.size(); ++l) {
                        list.addAll(ItemStack.expandBlockState(listnbt2.getString(l)));
                    }
                }
            }
        }

        if (pIsAdvanced.isAdvanced()) {
            if (itemStack.isDamaged()) {
                list.add(new TranslationTextComponent("item.durability", itemStack.getMaxDamage() - itemStack.getDamageValue(), itemStack.getMaxDamage()));
            }

            list.add((new StringTextComponent(Registry.ITEM.getKey(itemStack.getItem()).toString())).withStyle(TextFormatting.DARK_GRAY));
            if (itemStack.hasTag()) {
                list.add((new TranslationTextComponent("item.nbt_tags", itemStack.getTag().getAllKeys().size())).withStyle(TextFormatting.DARK_GRAY));
            }
        }

        //net.minecraftforge.event.ForgeEventFactory.onItemTooltip(itemStack, pPlayer, list, pIsAdvanced);
        return list;
    }
}
