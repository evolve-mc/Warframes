package net.evo_mc.warframes.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.evo_mc.warframes.block.custom.WarframeVerticalHalfControlSurfaceBlock;
import net.evo_mc.warframes.block.custom.WarframeVerticalSteppedControlSurfaceBlock;
import net.evo_mc.warframes.block.custom.WarframeVerticalSlabPosition;
import net.evo_mc.warframes.block.custom.WarframeVerticalControlSurfaceBlock;
import net.evo_mc.warframes.block.entity.WarframeVerticalControlSurfaceBlockEntity;
import net.evo_mc.warframes.block.entity.WarframeVerticalControlSurfaceModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WarframeVerticalControlSurfaceRenderer extends GeoBlockRenderer<WarframeVerticalControlSurfaceBlockEntity> {
    public WarframeVerticalControlSurfaceRenderer(BlockEntityRendererProvider.Context context) {
        super(new WarframeVerticalControlSurfaceModel());
    }

    @Override
    public void preRender(PoseStack poseStack, WarframeVerticalControlSurfaceBlockEntity animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        BlockState state = animatable.getBlockState();
        Block block = state.getBlock();

        Direction facing;
        WarframeVerticalSlabPosition aerofoil;
        if (block instanceof WarframeVerticalSteppedControlSurfaceBlock) {
            facing = state.getValue(WarframeVerticalSteppedControlSurfaceBlock.FACING);
            aerofoil = state.getValue(WarframeVerticalSteppedControlSurfaceBlock.AEROFOIL);
        } else if (block instanceof WarframeVerticalHalfControlSurfaceBlock) {
            facing = state.getValue(WarframeVerticalHalfControlSurfaceBlock.FACING);
            aerofoil = state.getValue(WarframeVerticalHalfControlSurfaceBlock.AEROFOIL);
        } else {
            facing = state.getValue(WarframeVerticalControlSurfaceBlock.FACING);
            aerofoil = state.getValue(WarframeVerticalControlSurfaceBlock.AEROFOIL);
        }

        double offset;
        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            offset = 0.0;
        } else {
            Direction lowSide = facing.getAxis() == Direction.Axis.Z ? Direction.WEST : Direction.NORTH;
            Direction right = facing.getClockWise();
            Direction resolvedSide = aerofoil == WarframeVerticalSlabPosition.RIGHT ? right : right.getOpposite();
            offset = resolvedSide == lowSide ? -4.0 / 16.0 : 4.0 / 16.0;
        }

        if (facing.getAxis() == Direction.Axis.Z) {
            poseStack.translate(offset, 0, 0);
        } else {
            poseStack.translate(0, 0, offset);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, red, green, blue, alpha);
    }
    @Override
    public int getViewDistance() {
        return 256;
    }
}