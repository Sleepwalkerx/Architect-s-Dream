package sleepwalker.architectsdream.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class IntegerFieldWidget {

    private TextFieldWidget textField;
    private Button leftButton, rightButton;
    private int number;
    private final ITextComponent name;

    private final Function<Integer, Boolean> function;

    public IntegerFieldWidget(ITextComponent name, Function<Integer, Boolean> function, int number) {

        this.name = name;
        this.function = function;
        this.number = number;
    }

    public IntegerFieldWidget(ITextComponent name, Function<Integer, Boolean> function) {
        this(name, function, 0);
    }

    public IntegerFieldWidget(ITextComponent name) {
        this(name, integer -> true);
    }

    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partial){
        textField.render(matrixStack, mouseX, mouseY, partial);
        leftButton.render(matrixStack, mouseX, mouseY, partial);
        rightButton.render(matrixStack, mouseX, mouseY, partial);
    }

    public boolean canConsumeInput(){ return textField.canConsumeInput(); }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return textField.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    public boolean charTyped(char word, int code) {
        return textField.charTyped(word, code);
    }

    public boolean mouseScrolled(double x, double y, double scroll){
        return textField.mouseScrolled(x, y, scroll);
    }

    public boolean isOnTextField(double x){
        return x >= textField.x && x <= textField.getWidth() + textField.x;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button){
        return
            textField.mouseClicked(mouseX, mouseY, button) ||
            leftButton.mouseClicked(mouseX, mouseY, button) ||
            rightButton.mouseClicked(mouseX, mouseY, button)
        ;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        return
            textField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) ||
            leftButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) ||
            rightButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        ;
    }

    public TextFieldWidget getTextField(){ return textField; }

    public IntegerFieldWidget init(FontRenderer font, int left, int top, int width){
        return init(font, left, top, width, 18);
    }

    public IntegerFieldWidget init(FontRenderer font, int left, int top, int width, int height){

        textField = new TextFieldWidget(font, left += 10, top, width, height, name){
            @Override
            public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
                addToNumber((int)scroll);
                return true;
            }
        };
        leftButton = new Button(left - 11, top - 1, 10, 20, new StringTextComponent("<"), b -> addToNumber(-1));
        rightButton = new Button(left + width + 1, top - 1, 10, 20, new StringTextComponent(">"), b -> addToNumber(1));

        textField.setValue(String.valueOf(number));
        textField.setFilter(str -> {
            if(str.isEmpty() || (str.charAt(0) == '-' && str.length() == 1)) return true;

            try {
                int number = Integer.parseInt(str);
                if(function.apply(number)){
                    this.number = number;
                    return true;
                }
            }
            catch (NumberFormatException ignored){ }
            return false;
        });

        return this;
    }

    public IntegerFieldWidget init(FontRenderer font, int left, int top){
        return init(font, left, top, 34);
    }

    public Widget[] getButtons(){
        return new Widget[]{ textField, leftButton, rightButton };
    }

    public void addToNumber(int value){
        textField.setValue(String.valueOf(number + value));
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void saveData(@Nonnull CompoundNBT compoundNBT){
        compoundNBT.putInt(name.getString(), number);
    }

    public void readData(@Nonnull CompoundNBT compoundNBT){
        number = compoundNBT.getInt(name.getString());
    }
}
