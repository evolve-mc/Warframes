package net.evo_mc.warframes.block.entity.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;

public class WarframeGeoMimicUtil {

    public static NativeImage buildMimicTexture(GeoModel<?> geoModel, ResourceLocation modelLocation, BlockState mimicked) {
        BakedGeoModel baked = geoModel.getBakedModel(modelLocation);
        int texW = (int) baked.properties().textureWidth();
        int texH = (int) baked.properties().textureHeight();

        NativeImage canvas = new NativeImage(texW, texH, true);

        BakedModel mimickedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimicked);
        List<BakedQuad> topQuads = mimickedModel.getQuads(mimicked, Direction.UP, RandomSource.create());
        if (topQuads.isEmpty()) {
            return canvas;
        }
        var sprite = topQuads.get(0).getSprite();
        var spriteContents = sprite.contents();
        int srcW = spriteContents.width();
        int srcH = spriteContents.height();

        for (int y = 0; y < texH; y++) {
            for (int x = 0; x < texW; x++) {
                int sx = (x * srcW) / texW;
                int sy = (y * srcH) / texH;
                canvas.setPixelRGBA(x, y, spriteContents.getOriginalImage().getPixelRGBA(sx, sy));
            }
        }

        return canvas;
    }

}