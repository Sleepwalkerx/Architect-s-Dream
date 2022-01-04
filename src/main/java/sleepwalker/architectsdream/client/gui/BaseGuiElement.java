package sleepwalker.architectsdream.client.gui;

public abstract class BaseGuiElement implements IGuiElement {

    protected int height;
    protected int width;
    protected int posX;
    protected int posY;
    protected int posEndX, posEndY;

    public void initGuiElement(int posX, int posY, int height, int width){

        this.posX = posX;
        this.posY = posY;
        this.height = height;
        this.width = width;

        posEndX = posX + width;
        posEndY = posY + height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

    @Override
    public int getPosEndX() {
        return posEndX;
    }

    @Override
    public int getPosEndY() {
        return posEndY;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
        posEndY = posY + height;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
        posEndX = posX + width;
    }

    @Override
    public void setPosX(int posX) {
        this.posX = posX;
        posEndX = posX + width;
    }

    @Override
    public void setPosY(int posY) {
        this.posY = posY;
        posEndY = posY + height;
    }

    @Override
    public void setPosEndX(int posEndX) {
        this.posEndX = posEndX;
        width = posEndX - posX;
    }

    @Override
    public void setPosEndY(int posEndY) {
        this.posEndY = posEndY;
        height = posEndY - posY;
    }
}
