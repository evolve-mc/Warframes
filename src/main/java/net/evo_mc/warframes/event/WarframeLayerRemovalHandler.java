package net.evo_mc.warframes.event;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.WarframeLayered;
import net.evo_mc.warframes.block.entity.WarframeMimicStorage;
import net.evo_mc.warframes.block.entity.WarframeMultiMimicBlockEntity;
import net.evo_mc.warframes.item.custom.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Warframes.MOD_ID)
public class WarframeLayerRemovalHandler {

    private static final int COOLDOWN_TICKS = 4;

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack held = player.getMainHandItem();
        if (!player.isShiftKeyDown() || !(held.getItem() instanceof WrenchItem)) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof WarframeLayered layered)) {
            return;
        }

        event.setCanceled(true);

        if (level.isClientSide()
                || event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START
                || player.getCooldowns().isOnCooldown(held.getItem())) {
            return;
        }

        BlockState reduced = layered.removeLayer(state, hitResult(player, level, pos, event.getFace()));
        if (reduced == null) {
            return;
        }

        player.getCooldowns().addCooldown(held.getItem(), COOLDOWN_TICKS);
        boolean creative = player.getAbilities().instabuild;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        List<BlockState> returnedMimics = new ArrayList<>();

        if (reduced.isAir()) {
            returnedMimics.addAll(collectMimics(blockEntity));
            suppressDrops(blockEntity);
            level.setBlock(pos, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
        } else {
            level.setBlock(pos, reduced, Block.UPDATE_ALL);
            if (blockEntity instanceof WarframeMultiMimicBlockEntity multi) {
                returnedMimics.addAll(multi.releaseRemovedSlots(reduced));
            }
        }

        if (!creative) {
            give(player, new ItemStack(state.getBlock()));
            for (BlockState mimic : returnedMimics) {
                give(player, new ItemStack(mimic.getBlock()));
            }
        }

        level.playSound(null, pos, state.getSoundType(level, pos, player).getBreakSound(), SoundSource.BLOCKS, 1f, 1f);
    }

    private static BlockHitResult hitResult(Player player, Level level, BlockPos pos, @Nullable Direction face) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(player.getBlockReach() + 1.0D));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK && hit.getBlockPos().equals(pos)) {
            return hit;
        }
        return new BlockHitResult(Vec3.atCenterOf(pos), face != null ? face : Direction.UP, pos, false);
    }

    private static List<BlockState> collectMimics(@Nullable BlockEntity blockEntity) {
        List<BlockState> mimics = new ArrayList<>();
        if (blockEntity instanceof WarframeMimicStorage storage && storage.getMimickedBlock() != null) {
            mimics.add(storage.getMimickedBlock());
        } else if (blockEntity instanceof WarframeMultiMimicBlockEntity multi) {
            for (int slot = 0; slot < multi.getSlotCount(); slot++) {
                BlockState mimicked = multi.getMimickedBlock(slot);
                if (mimicked != null) {
                    mimics.add(mimicked);
                }
            }
        }
        return mimics;
    }

    private static void suppressDrops(@Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof WarframeMimicStorage storage) {
            storage.suppressDrops();
        } else if (blockEntity instanceof WarframeMultiMimicBlockEntity multi) {
            multi.suppressDrops();
        }
    }

    private static void give(Player player, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}