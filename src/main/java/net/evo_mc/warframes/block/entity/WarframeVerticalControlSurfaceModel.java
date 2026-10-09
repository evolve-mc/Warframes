package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.WarframeVerticalHalfControlSurfaceBlock;
import net.evo_mc.warframes.block.custom.WarframeVerticalSteppedControlSurfaceBlock;
import net.evo_mc.warframes.block.custom.WarframeVerticalControlSurfaceBlock;
import net.evo_mc.warframes.block.entity.renderer.WarframeGeoMimicTextureCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class WarframeVerticalControlSurfaceModel extends GeoModel<WarframeVerticalControlSurfaceBlockEntity> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Warframes.MOD_ID, "textures/block/airframe_block.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(Warframes.MOD_ID, "animations/airframe_vertical_control_surface.animation.json");

    public ResourceLocation getModelResource(WarframeVerticalControlSurfaceBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        Block block = state.getBlock();
        String modelName;
        if (block instanceof WarframeVerticalSteppedControlSurfaceBlock stepped) {
            modelName = stepped.getModelName(state);
        } else if (block instanceof WarframeVerticalHalfControlSurfaceBlock half) {
            modelName = half.getModelName(state);
        } else {
            modelName = ((WarframeVerticalControlSurfaceBlock) block).getModelName(state);
        }
        return new ResourceLocation(Warframes.MOD_ID, "geo/" + modelName + ".geo.json");
    }

    public ResourceLocation getTextureResource(WarframeVerticalControlSurfaceBlockEntity animatable) {
        BlockState mimicked = animatable.getMimickedBlock();
        if (mimicked == null) {
            return DEFAULT_TEXTURE;
        }
        return WarframeGeoMimicTextureCache.getOrCreate(this, getModelResource(animatable), mimicked);
    }

    public ResourceLocation getAnimationResource(WarframeVerticalControlSurfaceBlockEntity animatable) {
        return ANIMATIONS;
    }
}