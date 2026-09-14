package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeMimicBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.evo_mc.warframes.item.custom.WrenchItem;

import javax.annotation.Nullable;

public interface WarframeMimicable extends EntityBlock {

    @Nullable
    @Override
    default BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WarframeMimicBlockEntity(pPos, pState);
    }

    default InteractionResult mimicUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                       InteractionHand pHand, BlockHitResult pHitResult) {
        ItemStack pStack = pPlayer.getItemInHand(pHand);

        if(pLevel.getBlockEntity(pPos) instanceof WarframeMimicBlockEntity mimicBlockEntity) {

            BlockState currentlyMimicked = mimicBlockEntity.getMimickedBlock();
            if(currentlyMimicked != null
                    && pStack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() == currentlyMimicked.getBlock()
                    && !isWrench(pStack)) {
                mimicBlockEntity.cycleTexture();
                pLevel.playSound(pPlayer, pPos, SoundEvents.NETHERITE_BLOCK_STEP, SoundSource.BLOCKS, 1f, 1f);
                return InteractionResult.SUCCESS;
            }

            if(mimicBlockEntity.inventory.getStackInSlot(0).isEmpty()
                    && pStack.getItem() instanceof BlockItem blockItem
                    && !isWrench(pStack)
                    && !(blockItem.getBlock() instanceof WarframeMimicable)
                    && isFullCube(blockItem.getBlock().defaultBlockState())) {
                mimicBlockEntity.setMimickedBlock(blockItem.getBlock().defaultBlockState());
                mimicBlockEntity.inventory.insertItem(0, pStack.copy().split(1), false);

                if(!pPlayer.getAbilities().instabuild) {
                    pStack.shrink(1);
                }

                pLevel.playSound(pPlayer, pPos, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.BLOCKS, 1f, 1f);
                return InteractionResult.SUCCESS;
            }

            if(isWrench(pStack) && !mimicBlockEntity.inventory.getStackInSlot(0).isEmpty()) {
                ItemStack stored = mimicBlockEntity.inventory.extractItem(0, 1, false);

                if(!pPlayer.getAbilities().instabuild) {
                    if(!pPlayer.getInventory().add(stored)) {
                        pPlayer.drop(stored, false);
                    }
                }

                mimicBlockEntity.clearMimickedBlock();
                pLevel.playSound(pPlayer, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    default void mimicPlayerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if(pPlayer.getAbilities().instabuild) {
            if(pLevel.getBlockEntity(pPos) instanceof WarframeMimicBlockEntity mimicBlockEntity) {
                mimicBlockEntity.clearContents();
            }
        }
    }

    default void mimicOnRemove(BlockState pState, Level pLevel, BlockPos pPos,
                               BlockState pNewState, boolean pMovedByPiston) {
        if(pState.getBlock() != pNewState.getBlock()) {
            if(pLevel.getBlockEntity(pPos) instanceof WarframeMimicBlockEntity mimicBlockEntity) {
                mimicBlockEntity.drops();
            }
        }
    }

    private boolean isWrench(ItemStack stack) {
        return stack.getItem() instanceof WrenchItem;
    }

    private boolean isFullCube(BlockState state) {
        VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        return Block.isShapeFullBlock(shape);
    }
}