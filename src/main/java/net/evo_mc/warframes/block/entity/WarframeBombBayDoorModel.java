package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.WarframeBombBayDoorBlock;
import net.evo_mc.warframes.block.custom.WarframeBombBayDoorVariant;
import net.evo_mc.warframes.block.entity.renderer.WarframeGeoMimicTextureCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class WarframeBombBayDoorModel extends GeoModel<WarframeBombBayDoorBlockEntity> {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Warframes.MOD_ID, "textures/block/airframe_bomb_bay.png");

    private static String modelName(WarframeBombBayDoorBlockEntity animatable) {
        WarframeBombBayDoorVariant variant = animatable.getBlockState().getValue(WarframeBombBayDoorBlock.VARIANT);
        return variant.getModelName();
    }

    @Override
    public ResourceLocation getModelResource(WarframeBombBayDoorBlockEntity animatable) {
        return new ResourceLocation(Warframes.MOD_ID, "geo/" + modelName(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WarframeBombBayDoorBlockEntity animatable) {
        BlockState mimicked = animatable.getMimickedBlock();
        if (mimicked == null) {
            return DEFAULT_TEXTURE;
        }
        return WarframeGeoMimicTextureCache.getOrCreate(this, getModelResource(animatable), mimicked);
    }

    @Override
    public ResourceLocation getAnimationResource(WarframeBombBayDoorBlockEntity animatable) {
        return new ResourceLocation(Warframes.MOD_ID, "animations/" + modelName(animatable) + ".animation.json");
    }
}