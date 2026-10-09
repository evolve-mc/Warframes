package net.evo_mc.warframes.block.entity.renderer;

import net.evo_mc.warframes.block.custom.WarframeMultiMimicable;
import net.evo_mc.warframes.block.entity.WarframeMultiMimicProperties;
import net.evo_mc.warframes.util.WarframeResprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class WarframeMultiMimicBakedModel extends BakedModelWrapper<BakedModel> {

    public static final int MAX_SLOTS = 6;

    protected WarframeMultiMimicBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    public static int encodeTint(int slot, int sourceTint) {
        return sourceTint < 0 ? -1 : sourceTint * MAX_SLOTS + slot;
    }

    public static int tintSlot(int tintIndex) {
        return tintIndex % MAX_SLOTS;
    }

    public static int tintSource(int tintIndex) {
        return tintIndex / MAX_SLOTS;
    }

    protected abstract int getOwningSlot(BlockState state, BakedQuad quad);

    protected boolean isHiddenQuad(BlockState state, BakedQuad quad, int slot) {
        return false;
    }

    @Nullable
    protected BlockState getSlotOnlyState(BlockState state, int slot) {
        return null;
    }

    private boolean isInternallyCulled(BlockState state, BakedQuad quad, int slot, BlockState slotMimic, BlockState[] mimicked) {
        Direction face = quad.getDirection();
        for (int other = 0; other < mimicked.length; other++) {
            if (other == slot || mimicked[other] == null || !hasSlot(state, other)
                    || !slotMimic.skipRendering(mimicked[other], face)) {
                continue;
            }
            BlockState otherOnly = getSlotOnlyState(state, other);
            if (otherOnly == null) {
                continue;
            }
            VoxelShape otherShape = otherOnly.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
            if (WarframeMimicCulling.isQuadCoveredBy(quad, otherShape)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSlot(BlockState state, int slot) {
        return state.getBlock() instanceof WarframeMultiMimicable mimicable
                && mimicable.getMimicSlotPresence(state, slot) > 0;
    }

    @Nonnull
    @Override
    public ModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData modelData) {
        ModelData data = super.getModelData(level, pos, state, modelData);
        if (data.get(WarframeMultiMimicProperties.MIMICKED_BLOCKS) == null) {
            return data;
        }
        return WarframeMimicCulling.withNeighbors(level, pos, data);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction,
                                    RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {

        BlockState[] mimicked = extraData.get(WarframeMultiMimicProperties.MIMICKED_BLOCKS);
        if (state == null || mimicked == null || !(state.getBlock() instanceof WarframeMultiMimicable)) {
            return super.getQuads(state, direction, rand, extraData, renderType);
        }

        List<BakedQuad> originalQuads = super.getQuads(state, direction, rand, extraData, null);
        if (originalQuads.isEmpty()) {
            return originalQuads;
        }

        ChunkRenderTypeSet baseRenderTypes = super.getRenderTypes(state, rand, extraData);
        SlotSource[] sources = new SlotSource[mimicked.length];
        WarframeMimicCulling.Neighbor[] neighbors = extraData.get(WarframeMimicCulling.NEIGHBORS);

        List<BakedQuad> result = new ArrayList<>(originalQuads.size());
        for (BakedQuad quad : originalQuads) {
            int slot = getOwningSlot(state, quad);
            if (slot >= 0 && isHiddenQuad(state, quad, slot)) {
                continue;
            }
            BlockState slotMimic = slot >= 0 && slot < mimicked.length ? mimicked[slot] : null;

            if (slotMimic == null) {
                if (renderType == null || baseRenderTypes.contains(renderType)) {
                    result.add(quad);
                }
                continue;
            }

            if (WarframeMimicCulling.isCulled(quad, slotMimic, neighbors)) {
                continue;
            }
            if (isInternallyCulled(state, quad, slot, slotMimic, mimicked)) {
                continue;
            }

            if (sources[slot] == null) {
                sources[slot] = SlotSource.of(slotMimic, rand, renderType);
            }
            SlotSource source = sources[slot];
            if (!source.inLayer()) {
                continue;
            }


            addRespritten(quad, slot, source, result);
        }
        return result;
    }

    private static void addRespritten(BakedQuad quad, int slot, SlotSource source, List<BakedQuad> result) {
        Direction face = quad.getDirection();
        List<BakedQuad> layerMatches = matching(source.layerQuads(), face);

        if (!layerMatches.isEmpty()) {
            for (BakedQuad match : layerMatches) {
                result.add(WarframeResprite.resprite(quad, match.getSprite(), encodeTint(slot, match.getTintIndex())));
            }
        } else if (source.primaryLayer() && matching(source.allQuads(), face).isEmpty()) {
            if (source.allQuads().isEmpty()) {
                result.add(WarframeResprite.resprite(quad, source.model().getParticleIcon(ModelData.EMPTY), -1));
            } else {
                BakedQuad first = source.allQuads().get(0);
                result.add(WarframeResprite.resprite(quad, first.getSprite(), encodeTint(slot, first.getTintIndex())));
            }
        }
    }

    private record SlotSource(BakedModel model, List<BakedQuad> layerQuads, List<BakedQuad> allQuads,
                              boolean inLayer, boolean primaryLayer) {

        static SlotSource of(BlockState mimic, RandomSource rand, @Nullable RenderType renderType) {
            BakedModel model = mimickedModel(mimic);
            ChunkRenderTypeSet types = model.getRenderTypes(mimic, rand, ModelData.EMPTY);
            if (renderType != null && !types.contains(renderType)) {
                return new SlotSource(model, List.of(), List.of(), false, false);
            }
            List<BakedQuad> layerQuads = collectQuads(model, mimic, rand, renderType);
            List<BakedQuad> allQuads = renderType == null ? layerQuads : collectQuads(model, mimic, rand, null);
            Iterator<RenderType> layers = types.iterator();
            boolean primaryLayer = renderType == null || !layers.hasNext() || layers.next() == renderType;
            return new SlotSource(model, layerQuads, allQuads, true, primaryLayer);
        }
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

    @Override
    public ChunkRenderTypeSet getRenderTypes(@Nonnull BlockState state, @Nonnull RandomSource rand, @Nonnull ModelData data) {
        ChunkRenderTypeSet baseRenderTypes = super.getRenderTypes(state, rand, data);
        BlockState[] mimicked = data.get(WarframeMultiMimicProperties.MIMICKED_BLOCKS);
        if (mimicked == null) {
            return baseRenderTypes;
        }

        List<ChunkRenderTypeSet> sets = new ArrayList<>();
        boolean anyUnmimicked = false;
        for (int slot = 0; slot < mimicked.length; slot++) {
            if (!hasSlot(state, slot)) {
                continue;
            }
            if (mimicked[slot] == null) {
                anyUnmimicked = true;
            } else {
                sets.add(mimickedModel(mimicked[slot]).getRenderTypes(mimicked[slot], rand, ModelData.EMPTY));
            }
        }
        if (anyUnmimicked || sets.isEmpty()) {
            sets.add(baseRenderTypes);
        }
        return ChunkRenderTypeSet.union(sets.toArray(new ChunkRenderTypeSet[0]));
    }
}