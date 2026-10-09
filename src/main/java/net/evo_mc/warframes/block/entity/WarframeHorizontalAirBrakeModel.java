package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.WarframeHorizontalAirBrakeBlock;
import net.evo_mc.warframes.block.entity.renderer.WarframeGeoMimicTextureCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class WarframeHorizontalAirBrakeModel extends GeoModel<WarframeHorizontalAirBrakeBlockEntity> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Warframes.MOD_ID, "textures/block/airframe_block.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(Warframes.MOD_ID, "animations/airframe_horizontal_airbrake.animation.json");

    public ResourceLocation getModelResource(WarframeHorizontalAirBrakeBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        String modelName = ((WarframeHorizontalAirBrakeBlock) state.getBlock()).getModelName(state);
        return new ResourceLocation(Warframes.MOD_ID, "geo/" + modelName + ".geo.json");
    }

    public ResourceLocation getTextureResource(WarframeHorizontalAirBrakeBlockEntity animatable) {
        BlockState mimicked = animatable.getMimickedBlock();
        if (mimicked == null) {
            return DEFAULT_TEXTURE;
        }
        return WarframeGeoMimicTextureCache.getOrCreate(this, getModelResource(animatable), mimicked);
    }

    public ResourceLocation getAnimationResource(WarframeHorizontalAirBrakeBlockEntity animatable) {
        return ANIMATIONS;
    }
}