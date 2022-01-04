package sleepwalker.architectsdream.serialize;

import com.google.common.collect.Sets;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.vector.Vector3i;
import sleepwalker.architectsdream.serialize.validator.IValidatorSerializer;
import sleepwalker.architectsdream.structure.container.IVerifiable;
import sleepwalker.architectsdream.structure.engine.BaseStructureEngine;
import sleepwalker.architectsdream.structure.Blueprint.Rarity;

import java.util.*;

public final class TemplateFileStructure {

    public String author = "Anonymous";
    public Rarity rarity = Rarity.SIMPLE;
    public Vector3i size = Vector3i.ZERO;

    public transient BlockPos maxPos, minPos;

    public BaseStructureEngine engine = BaseStructureEngine.DEFAULT_ENGINE;

    public Set<Entity> entities = Sets.newHashSet();

    public static class Entity {
        public ResourceLocation id;
        public List<IVerifiable> palette;
        public Map<IValidatorSerializer, Map<BlockPos, Integer>> validators;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entity entity = (Entity) o;
            return Objects.equals(id, entity.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
