package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.elements;

import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import sleepwalker.architectsdream.client.gui.widget.BaseScrollItemTooltips;
import sleepwalker.architectsdream.structure.container.ContainerTypeBlock;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlockInfoElement extends BaseScrollItemTooltips {

    private final ContainerTypeBlock container;

    public BlockInfoElement(@Nonnull ContainerTypeBlock container){
        super(new ItemStack(container.getBlock()));

        this.container = container;

        genTooltips();

        height = tooltips.size() * 7 + (tooltips.size() == 0 ? 24 : 26);
    }

    @Override
    public void genTooltips(){

        List<SimpleTooltipText> tooltips = new ArrayList<>();

        if(container.getTags() != null){

            tooltips.add(new SimpleTooltipText("Block Tag:", TextFormatting.YELLOW));

            container.getTags().getTags().forEach((name, tag) ->
                tooltips.add(new SimpleTooltipText(name.toString(), 5, TextFormatting.WHITE))
            );
        }

        if(container.isHasProperties()){

            tooltips.add(new SimpleTooltipText("Block Properties:", TextFormatting.YELLOW));

            if(container.getBlockState() != null){

                container.getBlockState().getValues().forEach((property, comparable) ->
                    tooltips.add(new SimpleTooltipText("  " + property.getName() + ": " + getValue(property, comparable), 5))
                );
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

    @Nonnull
    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> String getValue(@Nonnull Property<T> property, Comparable<?> value) {
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
