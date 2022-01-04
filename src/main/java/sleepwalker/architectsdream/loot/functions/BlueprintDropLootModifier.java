package sleepwalker.architectsdream.loot.functions;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.resources.BlueprintManager;
import sleepwalker.architectsdream.structure.Blueprint;
import sleepwalker.architectsdream.structure.EnumCondition;
import sleepwalker.architectsdream.utils.BlueprintUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class BlueprintDropLootModifier extends LootModifier {

    private final Blueprint blueprint;

    private final float chance;

    private final boolean clear;

    private static final String ID_BLUEPRINT = "id", CHANCE = "chance", CLEAR = "clear";

    protected BlueprintDropLootModifier(ILootCondition[] conditionsIn, Blueprint blueprint, float chance, boolean clear) {
        super(conditionsIn);

        this.blueprint = blueprint;
        this.chance = chance;
        this.clear = clear;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, @Nonnull LootContext context) {

        if(context.getRandom().nextFloat() < chance){

            if(clear){
                generatedLoot.clear();
            }

            generatedLoot.add(BlueprintUtils.setBlueprintToItem(new ItemStack(Items.Blueprint.get()), blueprint, EnumCondition.WHOLE));
        }

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<BlueprintDropLootModifier> {

        @Override
        public BlueprintDropLootModifier read(ResourceLocation location, @Nonnull JsonObject object, ILootCondition[] ailootcondition) {

            ResourceLocation id = new ResourceLocation(object.getAsJsonPrimitive(ID_BLUEPRINT).getAsString());

            Blueprint blueprint = BlueprintManager.getBlueprint(id);

            if(blueprint == null){
                throw new JsonSyntaxException("Unknown blueprint '" + id + "'");
            }

            return new BlueprintDropLootModifier(
                    ailootcondition,
                    blueprint,
                    object.getAsJsonPrimitive(CHANCE).getAsFloat(),
                    object.getAsJsonPrimitive(CLEAR).getAsBoolean()
            );
        }

        @Override
        public JsonObject write(@Nonnull BlueprintDropLootModifier instance) {

            JsonObject jsonObject = makeConditions(instance.conditions);

            jsonObject.addProperty(ID_BLUEPRINT, instance.blueprint.getID().toString());

            jsonObject.addProperty(CHANCE, instance.chance);

            jsonObject.addProperty(CLEAR, instance.clear);

            return jsonObject;
        }
    }
}
