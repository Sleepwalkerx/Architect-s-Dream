package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.elements;

import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.widgets.BaseScrollItemTooltips;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockInfoElement extends BaseScrollItemTooltips {

    private final ContainerTypeBlock container;

    //private List<ModelBlock> models;

    public BlockInfoElement(ContainerTypeBlock container){
        super(new ItemStack(container.getBlock()));

        this.container = container;

        genTooltips();

        height = tooltips.size() * 7 + (tooltips.size() == 0 ? 24 : 26);
    }

    @Override
    public void genTooltips(){

        List<Text> tooltips = new ArrayList<>();

        if(container.getTags() != null){

            tooltips.add(Text.of(new StringTextComponent("Block Tag:").withStyle(TextFormatting.YELLOW), 0));

            container.getTags().getTags().forEach((name, tag) ->
                tooltips.add(Text.of(new StringTextComponent(name.toString()).withStyle(TextFormatting.WHITE), 5))
            );
        }

        if(container.isHasProperties()){

            tooltips.add(Text.of(new StringTextComponent("Block Properties:").withStyle(TextFormatting.YELLOW), 0));

            if(container.getBlockState() != null){

                container.getBlockState().getValues().forEach((property, comparable) ->
                    tooltips.add(Text.of(new StringTextComponent(
                            "  " + property.getName() + ": " + getValue(property, comparable)
                    ).withStyle(TextFormatting.WHITE), 5)
                ));
            }
        }

        if(!tooltips.isEmpty()){
            this.tooltips = tooltips;
        }

        calcHeight();
    }

    public boolean add(ContainerTypeBlock block){

        if(this.container.equals(block)){

            itemStack.grow(1);

            return true;
        }
        else return false;
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> String getValue(Property<T> property, Comparable<?> value) {
        return property.getName((T)value);
    }

    public ContainerTypeBlock getContainer() {
        return container;
    }

    @Override
    public boolean canShowInStructureViewer() {
        return true;
    }

    @Override
    public boolean canShowInInfoPanel() {
        return true;
    }
}
