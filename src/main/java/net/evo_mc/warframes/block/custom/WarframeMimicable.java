package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeMimicBlockEntity;
import net.evo_mc.warframes.block.entity.WarframeMimicStorage;
import net.evo_mc.warframes.config.WarframesServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.evo_mc.warframes.item.custom.WrenchItem;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.Set;

public interface WarframeMimicable extends EntityBlock, IForgeBlock {

    @Nullable
    @Override
    default BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WarframeMimicBlockEntity(pPos, pState);
    }

    default InteractionResult mimicUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                       InteractionHand pHand, BlockHitResult pHitResult) {
        ItemStack pStack = pPlayer.getItemInHand(pHand);

        if(pLevel.getBlockEntity(pPos) instanceof WarframeMimicStorage mimicBlockEntity) {

            BlockState currentlyMimicked = mimicBlockEntity.getMimickedBlock();
            if(currentlyMimicked != null
                    && pStack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() == currentlyMimicked.getBlock()
                    && WarframeMultiMimicable.isOrientable(currentlyMimicked)
                    && !isWrench(pStack)) {
                BlockState rotated = placementState(blockItem, pPlayer, pHand, pStack, pHitResult);
                if(rotated != currentlyMimicked) {
                    mimicBlockEntity.setMimickedBlock(rotated);
                    pLevel.playSound(pPlayer, pPos, SoundEvents.NETHERITE_BLOCK_STEP, SoundSource.BLOCKS, 1f, 1f);
                }
                return InteractionResult.SUCCESS;
            }

            if(currentlyMimicked == null
                    && pStack.getItem() instanceof BlockItem blockItem
                    && !isWrench(pStack)
                    && WarframeMultiMimicable.canMimic(blockItem.getBlock())) {
                mimicBlockEntity.setMimickedBlock(placementState(blockItem, pPlayer, pHand, pStack, pHitResult));

                if(!pPlayer.getAbilities().instabuild && WarframesServerConfig.shouldConsume()) {
                    pStack.shrink(1);
                }

                pLevel.playSound(pPlayer, pPos, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.BLOCKS, 1f, 1f);
                return InteractionResult.SUCCESS;
            }

            if(isWrench(pStack) && currentlyMimicked != null) {
                if(!pLevel.isClientSide() && !pPlayer.getAbilities().instabuild && WarframesServerConfig.shouldConsume()) {
                    giveBack(pPlayer, currentlyMimicked);
                }

                mimicBlockEntity.clearMimickedBlock();
                pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    default void mimicSetPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (!(pPlacer instanceof Player player)) {
            return;
        }

        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhandStack.isEmpty()) {
            return;
        }

        if (!(offhandStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        if (!WarframeMultiMimicable.canMimic(blockItem.getBlock())) {
            return;
        }

        if (!(pLevel.getBlockEntity(pPos) instanceof WarframeMimicStorage mimicBlockEntity)) {
            return;
        }

        if (mimicBlockEntity.getMimickedBlock() != null) {
            return;
        }

        mimicBlockEntity.setMimickedBlock(blockItem.getBlock().defaultBlockState());

        if (!player.getAbilities().instabuild && WarframesServerConfig.shouldConsume()) {
            offhandStack.shrink(1);
        }


    }

    default void mimicPlayerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if(pPlayer.getAbilities().instabuild) {
            if(pLevel.getBlockEntity(pPos) instanceof WarframeMimicStorage mimicBlockEntity) {
                mimicBlockEntity.suppressDrops();
            }
        }
    }

    default void mimicOnRemove(BlockState pState, Level pLevel, BlockPos pPos,
                               BlockState pNewState, boolean pMovedByPiston) {
        if(pState.getBlock() != pNewState.getBlock() && WarframesServerConfig.shouldConsume()) {
            if(pLevel.getBlockEntity(pPos) instanceof WarframeMimicStorage mimicBlockEntity) {
                mimicBlockEntity.drops();
            }
        }
    }

    private static void giveBack(Player player, BlockState mimicked) {
        ItemStack returned = new ItemStack(mimicked.getBlock());
        if(returned.isEmpty()) {
            return;
        }
        if(!player.getInventory().add(returned)) {
            player.drop(returned, false);
        }
    }

    private BlockState placementState(BlockItem blockItem, Player player, InteractionHand hand,
                                      ItemStack stack, BlockHitResult hitResult) {
        BlockState placed = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(player, hand, stack, hitResult));
        return placed != null && isFullCube(placed) ? placed : blockItem.getBlock().defaultBlockState();
    }

    private boolean isWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }

    private boolean isFullCube(BlockState state) {
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        return Block.isShapeFullBlock(shape);
    }

    @Override
    default ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if(!player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof WarframeMimicStorage storage) {
            BlockState mimicked = storage.getMimickedBlock();
            if(mimicked != null) {
                ItemStack stack = new ItemStack(mimicked.getBlock());
                if(!stack.isEmpty()) {
                    return stack;
                }
            }
        }
        return IForgeBlock.super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if(!getMimicExcludedTextures(state).isEmpty()) {
            return IForgeBlock.super.hidesNeighborFace(level, pos, state, neighborState, dir);
        }
        if(level.getBlockEntity(pos) instanceof WarframeMimicStorage storage) {
            BlockState mimicked = storage.getMimickedBlock();
            if(mimicked != null && neighborState.skipRendering(mimicked, dir.getOpposite())) {
                return WarframeMultiMimicable.coversNeighborFace(level, pos, state, neighborState, dir);
            }
        }
        return IForgeBlock.super.hidesNeighborFace(level, pos, state, neighborState, dir);
    }

    default Set<ResourceLocation> getMimicExcludedTextures(BlockState state) {
        return Set.of();
    }
}