package sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.group;

import com.google.common.collect.Lists;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.infopanel.elements.ValidatorInfoElement;
import sleepwalker.architectsdream.client.gui.blueprint_viewer.provider.IModel;
import sleepwalker.architectsdream.structure.validators.IValidator;

import java.util.Optional;

public class ValidatorInfoGroup extends SimpleInfoGroup<ValidatorInfoElement> {

    public ValidatorInfoGroup(String name) {
        super(name);
    }

    public void put(IValidator validator, IModel model){

        Optional<ValidatorInfoElement> optional = elements.stream()
            .filter(a -> a.getSerializer() == validator.getSerializer())
        .findFirst();

        if(optional.isPresent()){

            optional.get().addModel(model);
        }
        else {

            ValidatorInfoElement element = new ValidatorInfoElement(Lists.newArrayList(model), validator.getSerializer());

            elements.add(element);
        }
    }

    @Override
    public boolean canShowInStructureViewer() {
        return false;
    }

    @Override
    public boolean canShowInInfoPanel() {
        return false;
    }
}
