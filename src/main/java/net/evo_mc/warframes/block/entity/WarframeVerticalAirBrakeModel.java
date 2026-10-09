package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.entity.renderer.WarframeGeoMimicTextureCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class WarframeVerticalAirBrakeModel extends GeoModel<WarframeVerticalAirBrakeBlockEntity> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Warframes.MOD_ID, "textures/block/airframe_block.png");
    private static final ResourceLocation MODEL = new ResourceLocation(Warframes.MOD_ID, "geo/airframe_vertical_air_brake.geo.json");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(Warframes.MOD_ID, "animations/airframe_vertical_airbrake.animation.json");

    public ResourceLocation getModelResource(WarframeVerticalAirBrakeBlockEntity animatable) {
        return MODEL;
    }

    public ResourceLocation getTextureResource(WarframeVerticalAirBrakeBlockEntity animatable) {
        BlockState mimicked = animatable.getMimickedBlock();
        if (mimicked == null) {
            return DEFAULT_TEXTURE;
        }
        return WarframeGeoMimicTextureCache.getOrCreate(this, getModelResource(animatable), mimicked);
    }

    public ResourceLocation getAnimationResource(WarframeVerticalAirBrakeBlockEntity animatable) {
        return ANIMATIONS;
    }
}