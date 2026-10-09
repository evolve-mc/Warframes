package net.evo_mc.warframes.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.evo_mc.warframes.block.entity.WarframeChestBlockEntity;
import net.evo_mc.warframes.block.entity.WarframeChestModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;

public class WarframeChestRenderer extends GeoBlockRenderer<WarframeChestBlockEntity> {
    private static final String LATCH_BONE = "latch";

    private final RandomSource random = RandomSource.create();
    private boolean latchPass = false;

    public WarframeChestRenderer(BlockEntityRendererProvider.Context context) {
        super(new WarframeChestModel());
        addRenderLayer(new GeoRenderLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, WarframeChestBlockEntity animatable, BakedGeoModel bakedModel, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
                if (animatable.getMimickedBlock() == null) {
                    return;
                }
                RenderType latchType = RenderType.entityCutoutNoCull(WarframeChestModel.defaultTexture(animatable));
                latchPass = true;
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, latchType,
                        bufferSource.getBuffer(latchType), partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
                latchPass = false;
            }
        });
    }

    @Override
    public RenderType getRenderType(WarframeChestBlockEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        if (animatable.getMimickedBlock() != null) {
            return RenderType.entityTranslucent(texture);
        }
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public Color getRenderColor(WarframeChestBlockEntity animatable, float partialTick, int packedLight) {
        BlockState mimicked = animatable.getMimickedBlock();
        if (mimicked != null && animatable.getLevel() != null) {
            BakedQuad topQuad = findTopQuad(mimicked);
            if (topQuad != null && topQuad.isTinted()) {
                int color = Minecraft.getInstance().getBlockColors().getColor(mimicked, animatable.getLevel(), animatable.getBlockPos(), topQuad.getTintIndex());
                if (color != -1) {
                    return Color.ofOpaque(color);
                }
            }
        }
        return super.getRenderColor(animatable, partialTick, packedLight);
    }

    @Override
    public void preRender(PoseStack poseStack, WarframeChestBlockEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource,
                          VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay,
                          float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        boolean mimicked = animatable.getMimickedBlock() != null;
        for (GeoBone bone : model.topLevelBones()) {
            applyVisibility(bone, mimicked, false);
        }
    }

    private void applyVisibility(GeoBone bone, boolean mimicked, boolean insideLatch) {
        boolean isLatch = insideLatch || bone.getName().equals(LATCH_BONE);
        if (!mimicked) {
            bone.setHidden(false);
        } else if (latchPass) {
            bone.setHidden(!isLatch);
        } else {
            bone.setHidden(isLatch);
        }
        bone.setChildrenHidden(false);
        for (GeoBone child : bone.getChildBones()) {
            applyVisibility(child, mimicked, isLatch);
        }
    }

    private BakedQuad findTopQuad(BlockState state) {
        random.setSeed(42L);
        List<BakedQuad> quads = Minecraft.getInstance().getBlockRenderer().getBlockModel(state).getQuads(state, Direction.UP, random);
        return quads.isEmpty() ? null : quads.get(0);
    }
}