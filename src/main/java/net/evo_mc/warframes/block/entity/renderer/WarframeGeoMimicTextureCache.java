package net.evo_mc.warframes.block.entity.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

import java.util.HashMap;
import java.util.Map;

public class WarframeGeoMimicTextureCache {

    private record Key(ResourceLocation model, BlockState state) {
    }

    private static final Map<Key, ResourceLocation> CACHE = new HashMap<>();

    public static ResourceLocation getOrCreate(GeoModel<?> geoModel, ResourceLocation modelLocation, BlockState mimicked) {
        return CACHE.computeIfAbsent(new Key(modelLocation, mimicked), key -> {
            NativeImage image = WarframeGeoMimicUtil.buildMimicTexture(geoModel, modelLocation, mimicked);
            String modelName = modelLocation.getPath().replace("geo/", "").replace(".geo.json", "");
            ResourceLocation rl = new ResourceLocation("warframes",
                    "dynamic/" + modelName + "_" + Block.getId(mimicked));
            Minecraft.getInstance().getTextureManager().register(rl, new DynamicTexture(image));
            return rl;
        });
    }
}