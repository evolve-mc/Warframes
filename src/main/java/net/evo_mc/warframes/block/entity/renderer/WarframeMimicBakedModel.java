package net.evo_mc.warframes.block.entity.renderer;

import net.evo_mc.warframes.block.custom.WarframeMimicable;
import net.evo_mc.warframes.block.entity.WarframeMimicProperties;
import net.evo_mc.warframes.util.WarframeResprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import java.util.Set;

public class WarframeMimicBakedModel extends BakedModelWrapper<BakedModel> {

    public WarframeMimicBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Nonnull
    @Override
    public ModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData modelData) {
        ModelData data = super.getModelData(level, pos, state, modelData);
        if (data.get(WarframeMimicProperties.MIMICKED_BLOCK) == null) {
            return data;
        }
        return WarframeMimicCulling.withNeighbors(level, pos, data);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction,
                                    RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {

        BlockState mimicked = extraData.get(WarframeMimicProperties.MIMICKED_BLOCK);
        if (mimicked == null) {
            return super.getQuads(state, direction, rand, extraData, renderType);
        }

        List<BakedQuad> originalQuads = super.getQuads(state, direction, rand, extraData, null);
        if (originalQuads.isEmpty()) {
            return originalQuads;
        }

        BakedModel mimickedModel = mimickedModel(mimicked);
        ChunkRenderTypeSet mimicTypes = mimickedModel.getRenderTypes(mimicked, rand, ModelData.EMPTY);
        boolean mimicInLayer = renderType == null || mimicTypes.contains(renderType);
        WarframeMimicable mimicable = state != null && state.getBlock() instanceof WarframeMimicable m ? m : null;
        Set<ResourceLocation> excludedTextures = mimicable != null ? mimicable.getMimicExcludedTextures(state) : Set.of();
        ChunkRenderTypeSet baseTypes = null;

        List<BakedQuad> layerQuads = null;
        List<BakedQuad> allQuads = null;
        boolean primaryLayer = false;
        WarframeMimicCulling.Neighbor[] neighbors = extraData.get(WarframeMimicCulling.NEIGHBORS);

        List<BakedQuad> result = new ArrayList<>();
        for (BakedQuad quad : originalQuads) {
            if (!excludedTextures.isEmpty() && excludedTextures.contains(quad.getSprite().contents().name())) {
                if (baseTypes == null) {
                    baseTypes = super.getRenderTypes(state, rand, extraData);
                }
                if (renderType == null || baseTypes.contains(renderType)) {
                    result.add(quad);
                }
                continue;
            }
            if (!mimicInLayer) {
                continue;
            }
            if (WarframeMimicCulling.isCulled(quad, mimicked, neighbors)) {
                continue;
            }

            if (layerQuads == null) {
                layerQuads = collectQuads(mimickedModel, mimicked, rand, renderType);
                allQuads = renderType == null ? layerQuads : collectQuads(mimickedModel, mimicked, rand, null);
                primaryLayer = renderType == null || isPrimaryLayer(mimicTypes, renderType);
            }

            Direction face = quad.getDirection();
            List<BakedQuad> layerMatches = matching(layerQuads, face);

            if (!layerMatches.isEmpty()) {
                for (BakedQuad source : layerMatches) {
                    result.add(WarframeResprite.resprite(quad, source.getSprite(), source.getTintIndex()));
                }
            } else if (primaryLayer && matching(allQuads, face).isEmpty()) {
                if (allQuads.isEmpty()) {
                    result.add(WarframeResprite.resprite(quad, mimickedModel.getParticleIcon(ModelData.EMPTY), -1));
                } else {
                    BakedQuad source = allQuads.get(0);
                    result.add(WarframeResprite.resprite(quad, source.getSprite(), source.getTintIndex()));
                }
            }
        }
        return result;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@Nonnull BlockState state, @Nonnull RandomSource rand, @Nonnull ModelData data) {
        BlockState mimicked = data.get(WarframeMimicProperties.MIMICKED_BLOCK);
        if (mimicked == null) {
            return super.getRenderTypes(state, rand, data);
        }
        ChunkRenderTypeSet mimicTypes = mimickedModel(mimicked).getRenderTypes(mimicked, rand, ModelData.EMPTY);
        if (hasExcludedTextures(state)) {
            return ChunkRenderTypeSet.union(mimicTypes, super.getRenderTypes(state, rand, data));
        }
        return mimicTypes;
    }

    private static boolean hasExcludedTextures(BlockState state) {
        return state.getBlock() instanceof WarframeMimicable mimicable
                && !mimicable.getMimicExcludedTextures(state).isEmpty();
    }

    private static BakedModel mimickedModel(BlockState mimicked) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(mimicked);
    }

    private static List<BakedQuad> collectQuads(BakedModel model, BlockState state, RandomSource rand, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>();
        for (Direction side : Direction.values()) {
            quads.addAll(model.getQuads(state, side, rand, ModelData.EMPTY, renderType));
        }
        quads.addAll(model.getQuads(state, null, rand, ModelData.EMPTY, renderType));
        return quads;
    }

    private static List<BakedQuad> matching(List<BakedQuad> quads, Direction face) {
        List<BakedQuad> matches = new ArrayList<>();
        for (BakedQuad quad : quads) {
            if (quad.getDirection() == face) {
                matches.add(quad);
            }
        }
        return matches;
    }

    private static boolean isPrimaryLayer(ChunkRenderTypeSet types, RenderType renderType) {
        Iterator<RenderType> layers = types.iterator();
        return !layers.hasNext() || layers.next() == renderType;
    }
}