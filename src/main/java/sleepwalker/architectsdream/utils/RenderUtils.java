package sleepwalker.architectsdream.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    private RenderUtils(){}

    public static void startGlScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        double scaleW = (double)mc.getWindow().getScreenWidth() / (double)mc.getWindow().getGuiScaledWidth();
        double scaleH = (double)mc.getWindow().getScreenHeight() / (double)mc.getWindow().getGuiScaledHeight();
        if (width > 0 && height > 0) {
            if (x < 0) {
              x = 0;
            }
  
            if (y < 0) {
              y = 0;
            }
  
            GL11.glEnable(3089);
            GL11.glScissor(
                (int)Math.floor((double)x * scaleW), 
                (int)Math.floor((double)mc.getWindow().getScreenHeight() - (double)(y + height) * scaleH), 
                (int)Math.floor((double)(x + width) * scaleW) - (int)Math.floor((double)x * scaleW), 
                (int)Math.floor((double)mc.getWindow().getScreenHeight() - (double)y * scaleH) - (int)Math.floor((double)mc.getWindow().getScreenHeight() - (double)(y + height) * scaleH)
            );
        }
    }
}
