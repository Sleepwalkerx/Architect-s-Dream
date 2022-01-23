package sleepwalker.architectsdream.client.gui.blueprint_creator.utils;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import sleepwalker.architectsdream.R;
import sleepwalker.architectsdream.client.gui.blueprint_creator.utils.FileStructureCreator.PointTemplateData;
import sleepwalker.architectsdream.config.Config;
import sleepwalker.architectsdream.init.Items;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class WorldRenderer {

    private final Minecraft mc;

    private static List<VisualCube> cachePoints;
    private static List<PointTemplateData> points;

    public WorldRenderer(Minecraft mc){
        this.mc = mc;
    }

    @Nonnull
    public static List<PointTemplateData> getPoints() {
        if(points == null){
            return Collections.emptyList();
        }
        else return points;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event){
        
        updateCache();
        if(cachePoints == null) return;

        if(Config.CLIENT.translucentMesh.get())
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        IRenderTypeBuffer.Impl buffer = mc.renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

        Vector3d view = mc.gameRenderer.getMainCamera().getPosition();
        for(VisualCube point : cachePoints){
            drawShape(
                event.getMatrixStack(), 
                builder, 
                point.voxel, 
                point.pos.getX() - view.x, point.pos.getY() - view.y, point.pos.getZ()- view.z, 
                point.r, point.g, point.b, 1.0f
            );
        }

        buffer.endBatch(RenderType.LINES);
    }

    private void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shape, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = matrixStackIn.last().pose();
        shape.forAllEdges((x, y, z, x1, y2, z2) -> {
           bufferIn.vertex(matrix4f, (float)(x + xIn), (float)(y + yIn), (float)(z + zIn)).color(red, green, blue, alpha).endVertex();
           bufferIn.vertex(matrix4f, (float)(x1 + xIn), (float)(y2 + yIn), (float)(z2 + zIn)).color(red, green, blue, alpha).endVertex();
        });
    }

    private void updateCache(){

        ItemStack btemplate;

        if(mc.player.getMainHandItem().getItem() == Items.BlueprintCreator.get()){
            btemplate = mc.player.getMainHandItem();
        }
        else if(mc.player.getOffhandItem().getItem() == Items.BlueprintCreator.get()){
            btemplate = mc.player.getOffhandItem();
        }
        else {
            points = null;
            cachePoints = null;
            return;
        }

        if(btemplate.getTag() == null){
            return;
        }

        List<PointTemplateData> newData = FileStructureCreator.getPointTemplateData(btemplate.getTag());

        if(!newData.equals(points)){
            cachePoints = buildVoxelShapes(newData);
            points = newData;
        }
    }

    @Nonnull
    private List<VisualCube> buildVoxelShapes(@Nonnull List<PointTemplateData> listData){

        List<VisualCube> list = Lists.newArrayList();

        for(PointTemplateData pointData : listData){

            list.add(new VisualCube(VoxelShapes.block(), pointData.getColorR(), pointData.getColorG(), pointData.getColorB(), pointData.getMax()));

            if(pointData.getMin() != BlockPos.ZERO){

                list.add(new VisualCube(VoxelShapes.block(), pointData.getColorR(), pointData.getColorG(), pointData.getColorB(), pointData.getMin()));

                list.add(new VisualCube(
                    VoxelShapes.create(new AxisAlignedBB(
                        0, 0, 0,
                        pointData.getMax().getX() - pointData.getMin().getX() + 1,
                        pointData.getMax().getY() - pointData.getMin().getY() + 1,
                        pointData.getMax().getZ() - pointData.getMin().getZ() + 1
                    )),

                    pointData.getColorR(),
                    pointData.getColorG(),
                    pointData.getColorB(),

                    pointData.getMin()
                ));
            }
        }

        return list;
    }

    @OnlyIn(Dist.CLIENT)
    private static class VisualCube {
        final VoxelShape voxel;
        final float r, g, b;
        final BlockPos pos;

        public VisualCube(VoxelShape voxel, float r, float g, float b, BlockPos pos){
            this.voxel = voxel;
            this.r = r;
            this.g = g;
            this.b = b;
            this.pos = pos;
        }
    }
}
