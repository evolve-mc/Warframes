package net.evo_mc.warframes.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.evo_mc.warframes.block.custom.WarframeControlSurfaceBlock;
import net.evo_mc.warframes.block.entity.WarframeControlSurfaceBlockEntity;
import net.evo_mc.warframes.block.entity.WarframeControlSurfaceModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import net.evo_mc.warframes.block.custom.WarframeHorizontalSlabPosition;

public class WarframeControlSurfaceRenderer extends GeoBlockRenderer<WarframeControlSurfaceBlockEntity> {
    public WarframeControlSurfaceRenderer(BlockEntityRendererProvider.Context context) { super(new WarframeControlSurfaceModel());
    }

    @Override
    public void preRender(PoseStack poseStack, WarframeControlSurfaceBlockEntity animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        BlockState state = animatable.getBlockState();

        WarframeHorizontalSlabPosition half = state.getValue(WarframeControlSurfaceBlock.HALF);
        double yOffset = switch (half) {
            case BOTTOM -> -4.0 / 16.0;
            case TOP -> 4.0 / 16.0;
            case CENTER -> 0.0;
        };
        poseStack.translate(0, yOffset, 0);

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);
    }
    @Override
    public int getViewDistance() {
        return 256;
    }

}