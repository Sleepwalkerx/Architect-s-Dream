package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.group;

import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.IInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.elements.BlockInfoType;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.block.ModelBlock;

import java.util.Optional;

public class BlockInfoGroup extends SimpleInfoGroup<BlockInfoType> {

    public BlockInfoGroup(String name) {
        super(name);
    }

    public IInfoElement addElement(ModelBlock modelBlock){

        Optional<BlockInfoType> element = elements.stream()
            .filter(a -> a.getItemStack().getItem() == modelBlock.getItemStack().getItem())
        .findFirst();

        if(element.isPresent()){

            element.get().addElement(modelBlock);

            return element.get();
        }
        else {

            BlockInfoType infoElement = new BlockInfoType(modelBlock);

            elements.add(infoElement);

            return infoElement;
        }
    }

    @Override
    public void init() {
        elements.forEach(BlockInfoType::createTooltip);
    }
}
