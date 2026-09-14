package net.evo_mc.warframes.block.entity.renderer;

import net.evo_mc.warframes.block.entity.WarframeMimicProperties;
import net.evo_mc.warframes.util.WarframeResprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarframeMimicBakedModel extends BakedModelWrapper<BakedModel> {

    private static final Direction[] CYCLE_ORDER = {
            Direction.UP, Direction.SOUTH, Direction.EAST,
            Direction.DOWN, Direction.NORTH, Direction.WEST
    };

    public WarframeMimicBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    private Direction shiftDirection(Direction direction, int offset) {
        int index = Arrays.asList(CYCLE_ORDER).indexOf(direction);
        if (index == -1) {
            return direction;
        }
        int shiftedIndex = (index + offset) % 6;
        return CYCLE_ORDER[shiftedIndex];
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction,
                                    RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {

        List<BakedQuad> originalQuads = super.getQuads(state, direction, rand, extraData, renderType);

        BlockState mimicked = extraData.get(WarframeMimicProperties.MIMICKED_BLOCK);
        if (mimicked == null || originalQuads.isEmpty()) {
            return originalQuads;
        }

        Integer offset = extraData.get(WarframeMimicProperties.TEXTURE_CYCLE_OFFSET);
        int cycleOffset = (offset != null) ? offset : 0;

        BakedModel mimickedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimicked);

        Direction lookupDirection = (direction != null) ? shiftDirection(direction, cycleOffset) : Direction.UP;
        List<BakedQuad> mimickedQuads = mimickedModel.getQuads(mimicked, lookupDirection, rand);

        if (mimickedQuads.isEmpty()) {
            return originalQuads;
        }

        var targetSprite = mimickedQuads.get(0).getSprite();
        boolean rotate = (direction != null) && shouldRotate(direction, cycleOffset);

        return originalQuads.stream()
                .map(quad -> WarframeResprite.resprite(quad, targetSprite, 0, rotate))
                .collect(Collectors.toList());
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@Nonnull BlockState state, @Nonnull RandomSource rand, @Nonnull ModelData data) {
        BlockState mimicked = data.get(WarframeMimicProperties.MIMICKED_BLOCK);
        if (mimicked != null) {
            return ItemBlockRenderTypes.getRenderLayers(mimicked);
        }
        return super.getRenderTypes(state, rand, data);
    }

    private boolean shouldRotate(Direction direction, int offset) {
        int mod = offset % 3;
        if (mod == 0) {
            return false;
        }
        boolean isNSUD = direction == Direction.NORTH || direction == Direction.SOUTH
                || direction == Direction.UP || direction == Direction.DOWN;
        boolean isEW = direction == Direction.EAST || direction == Direction.WEST;

        if (mod == 1) {
            return isNSUD;
        } else {
            return isEW;
        }
    }
}