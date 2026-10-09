package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeMultiMimicBlockEntity;
import net.evo_mc.warframes.config.WarframesServerConfig;
import net.evo_mc.warframes.item.custom.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;

public interface WarframeMultiMimicable extends EntityBlock, IForgeBlock {

    int getMimicSlotPresence(BlockState state, int slot);

    int getMimicSlotForHit(BlockState state, BlockPos pos, BlockHitResult hitResult);

    static boolean canMimic(Block block) {
        return !(block instanceof WarframeMimicable)
                && !(block instanceof WarframeMultiMimicable)
                && isFullCube(block.defaultBlockState())
                && WarframesServerConfig.canMimic(block.defaultBlockState());
    }
    static boolean isOrientable(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            Class<?> valueClass = property.getValueClass();
            if (valueClass == Direction.class || valueClass == Direction.Axis.class) {
                return true;
            }
        }
        return false;
    }

    default InteractionResult multiMimicUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                            InteractionHand pHand, BlockHitResult pHitResult) {
        if (!(pLevel.getBlockEntity(pPos) instanceof WarframeMultiMimicBlockEntity mimicBlockEntity)) {
            return InteractionResult.PASS;
        }

        int slot = getMimicSlotForHit(pState, pPos, pHitResult);
        if (slot < 0 || slot >= mimicBlockEntity.getSlotCount() || getMimicSlotPresence(pState, slot) <= 0) {
            return InteractionResult.PASS;
        }

        ItemStack pStack = pPlayer.getItemInHand(pHand);
        BlockState currentlyMimicked = mimicBlockEntity.getMimickedBlock(slot);

        if (currentlyMimicked != null
                && pStack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() == currentlyMimicked.getBlock()
                && isOrientable(currentlyMimicked)
                && !isWrench(pStack)) {
            BlockState rotated = placementState(blockItem, pPlayer, pHand, pStack, pHitResult);
            if (rotated != currentlyMimicked) {
                mimicBlockEntity.setMimickedBlock(slot, rotated);
                pLevel.playSound(pPlayer, pPos, SoundEvents.NETHERITE_BLOCK_STEP, SoundSource.BLOCKS, 1f, 1f);
            }
            return InteractionResult.SUCCESS;
        }

        if (currentlyMimicked == null
                && pStack.getItem() instanceof BlockItem blockItem
                && !isWrench(pStack)
                && canMimic(blockItem.getBlock())) {
            mimicBlockEntity.setMimickedBlock(slot, placementState(blockItem, pPlayer, pHand, pStack, pHitResult));

            if (!pPlayer.getAbilities().instabuild && WarframesServerConfig.shouldConsume()) {
                pStack.shrink(1);
            }

            pLevel.playSound(pPlayer, pPos, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.BLOCKS, 1f, 1f);
            return InteractionResult.SUCCESS;
        }

        if (isWrench(pStack) && currentlyMimicked != null) {
            if (!pLevel.isClientSide() && !pPlayer.getAbilities().instabuild && WarframesServerConfig.shouldConsume()) {
                giveBack(pPlayer, currentlyMimicked);
            }

            mimicBlockEntity.clearMimickedBlock(slot);
            pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    default void multiMimicSetPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer) {
        if (!(pLevel.getBlockEntity(pPos) instanceof WarframeMultiMimicBlockEntity mimicBlockEntity)) {
            return;
        }

        int slot = mimicBlockEntity.registerPlacement(pState);
        if (slot < 0) {
            return;
        }
        if (!(pPlacer instanceof Player player)) {
            return;
        }
        if (mimicBlockEntity.getMimickedBlock(slot) != null) {
            return;
        }

        ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhandStack.isEmpty()) {
            return;
        }
        if (!(offhandStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        if (!canMimic(blockItem.getBlock())) {
            return;
        }

        mimicBlockEntity.setMimickedBlock(slot, blockItem.getBlock().defaultBlockState());

        if (!player.getAbilities().instabuild && WarframesServerConfig.shouldConsume()) {
            offhandStack.shrink(1);
        }


    }

    default void multiMimicPlayerWillDestroy(Level pLevel, BlockPos pPos, Player pPlayer) {
        if (pPlayer.getAbilities().instabuild
                && pLevel.getBlockEntity(pPos) instanceof WarframeMultiMimicBlockEntity mimicBlockEntity) {
            mimicBlockEntity.suppressDrops();
        }
    }

    default void multiMimicOnRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState) {
        if (!pState.is(pNewState.getBlock())
                && WarframesServerConfig.shouldConsume()
                && pLevel.getBlockEntity(pPos) instanceof WarframeMultiMimicBlockEntity mimicBlockEntity) {
            mimicBlockEntity.drops();
        }
    }

    private static void giveBack(Player player, BlockState mimicked) {
        ItemStack returned = new ItemStack(mimicked.getBlock());
        if (returned.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(returned)) {
            player.drop(returned, false);
        }
    }

    private static BlockState placementState(BlockItem blockItem, Player player, InteractionHand hand,
                                             ItemStack stack, BlockHitResult hitResult) {
        BlockState placed = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(player, hand, stack, hitResult));
        return placed != null && isFullCube(placed) ? placed : blockItem.getBlock().defaultBlockState();
    }

    private static boolean isWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }

    private static boolean isFullCube(BlockState state) {
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        return Block.isShapeFullBlock(shape);
    }

    @Override
    default ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (!player.isShiftKeyDown() && target instanceof BlockHitResult blockHit
                && level.getBlockEntity(pos) instanceof WarframeMultiMimicBlockEntity mimicBlockEntity) {
            int slot = getMimicSlotForHit(state, pos, blockHit);
            if (slot >= 0 && slot < mimicBlockEntity.getSlotCount()) {
                BlockState mimicked = mimicBlockEntity.getMimickedBlock(slot);
                if (mimicked != null) {
                    ItemStack stack = new ItemStack(mimicked.getBlock());
                    if (!stack.isEmpty()) {
                        return stack;
                    }
                }
            }
        }
        return IForgeBlock.super.getCloneItemStack(state, target, level, pos, player);
    }

    static boolean coversNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        VoxelShape ownFace = state.getShape(level, pos).getFaceShape(dir);
        VoxelShape neighborFace = neighborState.getShape(level, pos.relative(dir)).getFaceShape(dir.getOpposite());
        return !neighborFace.isEmpty() && !Shapes.joinIsNotEmpty(neighborFace, ownFace, BooleanOp.ONLY_FIRST);
    }

    @Override
    default boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        if (level.getBlockEntity(pos) instanceof WarframeMultiMimicBlockEntity mimicBlockEntity) {
            BlockState shared = null;
            for (int slot = 0; slot < mimicBlockEntity.getSlotCount(); slot++) {
                if (getMimicSlotPresence(state, slot) <= 0) {
                    continue;
                }
                BlockState mimicked = mimicBlockEntity.getMimickedBlock(slot);
                if (mimicked == null || (shared != null && !mimicked.is(shared.getBlock()))) {
                    shared = null;
                    break;
                }
                shared = mimicked;
            }
            if (shared != null && neighborState.skipRendering(shared, dir.getOpposite())) {
                return coversNeighborFace(level, pos, state, neighborState, dir);
            }
        }
        return IForgeBlock.super.hidesNeighborFace(level, pos, state, neighborState, dir);
    }
}