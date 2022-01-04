package sleepwalker.architectsdream.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sleepwalker.architectsdream.init.Items;
import sleepwalker.architectsdream.client.resources.ShellManager;
import sleepwalker.architectsdream.utils.BlueprintUtils;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BlueprintOverlayGui extends AbstractGui {

    private final Minecraft mc = Minecraft.getInstance();

    private final ItemRenderer itemRenderer;

    public BlueprintOverlayGui(){
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    /*int xx = 300, yy;
    int factor = 10;

    @SubscribeEvent
    public void keyPressed(@Nonnull InputEvent.KeyInputEvent event){


        //g
        if(event.getKey() == 71){
            if(Screen.hasControlDown()){
                xx -= factor;
            }
            else xx += factor;

            System.out.println(xx);
        }

        //h
        if(event.getKey() == 72){
            if(Screen.hasControlDown()){
                yy--;
            }
            else yy++;
        }
    }*/

    private ContainerScreen<?> containerScreen;
    private List<Slot> containerSlots;

    private int leftPos, topPos;


    @SubscribeEvent
    public void guiOpenEvent(GuiOpenEvent event){

        if(event.getGui() == null){

            containerScreen = null;

            containerSlots = null;

            return;
        }

        if(event.getGui() instanceof ContainerScreen<?>){

            ContainerScreen<?> screen = (ContainerScreen<?>)event.getGui();

            containerSlots = screen.getMenu().slots;

            containerScreen = screen;
        }
    }

    @SubscribeEvent
    public void renderScreenEvent(GuiScreenEvent.DrawScreenEvent.Post event){

        if(containerScreen == null || !Screen.hasAltDown() || mc.player == null){
            return;
        }

        if(!mc.player.inventory.getCarried().isEmpty()){

            renderSlot(
                (int)(mc.mouseHandler.xpos() / mc.getWindow().getGuiScale()) - 8,
                (int)(mc.mouseHandler.ypos() / mc.getWindow().getGuiScale()) - 8,
                event.getRenderPartialTicks(), mc.player, mc.player.inventory.getCarried()
            );
        }

        containerSlots.forEach(slot -> {
            renderSlot(slot.x + containerScreen.getGuiLeft(), slot.y + containerScreen.getGuiTop(), event.getRenderPartialTicks(), mc.player, slot.getItem());
        });
    }

    @SubscribeEvent
    public void onPostRenderEvent(@Nonnull RenderGameOverlayEvent.Post event){
        
        if(event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || !Screen.hasAltDown() || mc.player == null) return;

        renderHotbar(event.getPartialTicks());
    }

    private void renderHotbar(float pPartialTicks){

        PlayerEntity playerentity = this.getCameraPlayer();

        if (playerentity != null) {

            ItemStack itemstack = playerentity.getOffhandItem();
            HandSide handside = playerentity.getMainArm().getOpposite();

            int i = mc.gui.screenWidth / 2;
            int j = this.getBlitOffset();

            this.setBlitOffset(j + 100);
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            for (int i1 = 0; i1 < 9; ++i1) {
                int j1 = i - 90 + i1 * 20 + 2;
                int k1 = mc.gui.screenHeight - 16 - 3;
                renderSlot(j1, k1, pPartialTicks, playerentity, playerentity.inventory.items.get(i1));
            }

            if (!itemstack.isEmpty()) {
                int i2 = mc.gui.screenHeight - 16 - 3;
                if (handside == HandSide.LEFT) {
                    this.renderSlot(i - 91 - 26, i2, pPartialTicks, playerentity, itemstack);
                } else {
                    renderSlot(i + 91 + 10, i2, pPartialTicks, playerentity, itemstack);
                }
            }

            RenderSystem.disableRescaleNormal();
            RenderSystem.disableBlend();
        }
    }

    private void renderSlot(int x, int y, float tick, PlayerEntity playerEntity, ItemStack itemStack) {

        if(itemStack.getItem() != Items.Blueprint.get()){
            return;
        }

        ResourceLocation location = BlueprintUtils.getBlueprintIdFromItem(itemStack);

        ItemStack stack = ShellManager.getClientStorage().get(location).getIcon();

        if(stack == null){
            return;
        }


        float f = (float)itemStack.getPopTime() - tick;
        if (f > 0.0F) {
            RenderSystem.pushMatrix();
            float f1 = 1.0F + f / 5.0F;
            RenderSystem.translatef((float)(x + 8), (float)(y + 12), 0.0F);
            RenderSystem.scalef(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            RenderSystem.translatef((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
        }

        renderGuiItem(stack, x, y, itemRenderer.getModel(stack, null, playerEntity));

        if (f > 0.0F) {
            RenderSystem.popMatrix();
        }
    }

    private PlayerEntity getCameraPlayer() {
        return !(this.mc.getCameraEntity() instanceof PlayerEntity) ? null : (PlayerEntity)this.mc.getCameraEntity();
    }

    protected void renderGuiItem(ItemStack pStack, int pX, int pY, @Nonnull IBakedModel pBakedmodel) {

        RenderSystem.pushMatrix();
        mc.getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
        mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)pX + 6.6f, (float)pY + 6f, 700f + itemRenderer.blitOffset);
        RenderSystem.translatef(5.0F, 5.0F, 5.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(10.0F, 10.0F, 10.0F);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !pBakedmodel.usesBlockLight();
        if (flag) {
            RenderHelper.setupForFlatItems();
        }

        itemRenderer.render(pStack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, pBakedmodel);
        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupFor3DItems();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }
}
