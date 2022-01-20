package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.group;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.elements.BlockInfoType;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block.ModelBlock;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class BlockInfoGroup extends SimpleInfoGroup<BlockInfoType> {

    public BlockInfoGroup(String name) {
        super(name);
    }

    public void addElement(ModelBlock modelBlock){

        Optional<BlockInfoType> element = elements.stream()
            .filter(a -> a.getItemStack().getItem() == modelBlock.getItemStack().getItem())
        .findFirst();

        if(element.isPresent()){

            element.get().addElement(modelBlock);
        }
        else {

            BlockInfoType infoElement = new BlockInfoType(modelBlock);

            elements.add(infoElement);
        }
    }

    @Override
    public void build() {
        elements.forEach(BlockInfoType::createTooltip);
    }
}
