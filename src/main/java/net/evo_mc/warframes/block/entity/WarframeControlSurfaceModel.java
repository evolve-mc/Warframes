package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.WarframeControlSurfaceBlock;
import net.evo_mc.warframes.block.custom.WarframeHalfControlSurfaceBlock;
import net.evo_mc.warframes.block.custom.WarframeSteppedControlSurfaceBlock;
import net.evo_mc.warframes.block.entity.renderer.WarframeGeoMimicTextureCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class WarframeControlSurfaceModel extends GeoModel<WarframeControlSurfaceBlockEntity> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Warframes.MOD_ID, "textures/block/airframe_block.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(Warframes.MOD_ID, "animations/airframe_control_surface.animation.json");

    public ResourceLocation getModelResource(WarframeControlSurfaceBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        Block block = state.getBlock();
        String modelName;
        if (block instanceof WarframeSteppedControlSurfaceBlock stepped) {
            modelName = stepped.getModelName(state);
        } else if (block instanceof WarframeHalfControlSurfaceBlock half) {
            modelName = half.getModelName(state);
        } else {
            modelName = ((WarframeControlSurfaceBlock) block).getModelName(state);
        }
        return new ResourceLocation(Warframes.MOD_ID, "geo/" + modelName + ".geo.json");
    }

    public ResourceLocation getTextureResource(WarframeControlSurfaceBlockEntity animatable) {
        BlockState mimicked = animatable.getMimickedBlock();
        if (mimicked == null) {
            return DEFAULT_TEXTURE;
        }
        return WarframeGeoMimicTextureCache.getOrCreate(this, getModelResource(animatable), mimicked);
    }

    public ResourceLocation getAnimationResource(WarframeControlSurfaceBlockEntity animatable) {
        return ANIMATIONS;
    }
}