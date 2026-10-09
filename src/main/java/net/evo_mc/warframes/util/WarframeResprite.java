package net.evo_mc.warframes.util;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.model.IQuadTransformer;

public final class WarframeResprite {

    private WarframeResprite() {
    }

    public static BakedQuad resprite(BakedQuad quad, TextureAtlasSprite newSprite, int tintIndex) {
        TextureAtlasSprite oldSprite = quad.getSprite();
        int[] original = quad.getVertices();
        int[] vertices = java.util.Arrays.copyOf(original, original.length);

        for (int i = 0; i < 4; i++) {
            int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.UV0;

            float u = Float.intBitsToFloat(vertices[offset]);
            float v = Float.intBitsToFloat(vertices[offset + 1]);

            float uFraction = (u - oldSprite.getU0()) / (oldSprite.getU1() - oldSprite.getU0());
            float vFraction = (v - oldSprite.getV0()) / (oldSprite.getV1() - oldSprite.getV0());

            float newU = newSprite.getU0() + uFraction * (newSprite.getU1() - newSprite.getU0());
            float newV = newSprite.getV0() + vFraction * (newSprite.getV1() - newSprite.getV0());

            vertices[offset] = Float.floatToRawIntBits(newU);
            vertices[offset + 1] = Float.floatToRawIntBits(newV);
        }

        return new BakedQuad(vertices, tintIndex, quad.getDirection(), newSprite, quad.isShade(), quad.hasAmbientOcclusion());
    }
}