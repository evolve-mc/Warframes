package net.evo_mc.warframes.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.evo_mc.warframes.block.custom.WarframeVerticalSlabPosition;
import net.evo_mc.warframes.block.custom.WarframeVerticalAirBrakeBlock;
import net.evo_mc.warframes.block.entity.WarframeVerticalAirBrakeBlockEntity;
import net.evo_mc.warframes.block.entity.WarframeVerticalAirBrakeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WarframeVerticalAirBrakeRenderer extends GeoBlockRenderer<WarframeVerticalAirBrakeBlockEntity> {
    public WarframeVerticalAirBrakeRenderer(BlockEntityRendererProvider.Context context) {
        super(new WarframeVerticalAirBrakeModel());
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void preRender(PoseStack poseStack, WarframeVerticalAirBrakeBlockEntity animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        BlockState state = animatable.getBlockState();
        Direction facing = state.getValue(WarframeVerticalAirBrakeBlock.FACING);
        WarframeVerticalSlabPosition aerofoil = state.getValue(WarframeVerticalAirBrakeBlock.AEROFOIL);

        double offset;
        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            offset = 0.0;
        } else {
            Direction lowSide = facing.getAxis() == Direction.Axis.Z ? Direction.WEST : Direction.NORTH;
            Direction right = facing.getClockWise();
            Direction resolvedSide = aerofoil == WarframeVerticalSlabPosition.RIGHT ? right : right.getOpposite();
            offset = resolvedSide == lowSide ? -5.0 / 16.0 : 5.0 / 16.0;
        }

        if (facing.getAxis() == Direction.Axis.Z) {
            poseStack.translate(offset, 0, 0);
        } else {
            poseStack.translate(0, 0, offset);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);
    }
}