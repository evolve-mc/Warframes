package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.entity.renderer.WarframeGeoMimicTextureCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;
import net.evo_mc.warframes.block.ModBlocks;

public class WarframeChestModel extends GeoModel<WarframeChestBlockEntity> {

    private static final ResourceLocation MODEL = new ResourceLocation(Warframes.MOD_ID, "geo/airframe_chest.geo.json");
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(Warframes.MOD_ID, "textures/block/airframe_block.png");
    private static final ResourceLocation HEAVYFRAME_TEXTURE = new ResourceLocation(Warframes.MOD_ID, "textures/block/heavyframe_block.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(Warframes.MOD_ID, "animations/airframe_chest.animation.json");

    @Override
    public ResourceLocation getModelResource(WarframeChestBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(WarframeChestBlockEntity animatable) {
        BlockState mimicked = animatable.getMimickedBlock();
        if (mimicked == null) {
            return defaultTexture(animatable);
        }
        return WarframeGeoMimicTextureCache.getOrCreate(this, MODEL, mimicked);
    }
    public static ResourceLocation defaultTexture(WarframeChestBlockEntity animatable) {
        return animatable.getBlockState().is(ModBlocks.HEAVYFRAME_CHEST.get()) ? HEAVYFRAME_TEXTURE : DEFAULT_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(WarframeChestBlockEntity animatable) {
        return ANIMATIONS;
    }
}