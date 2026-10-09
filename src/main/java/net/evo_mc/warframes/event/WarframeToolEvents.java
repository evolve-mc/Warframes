package net.evo_mc.warframes.event;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.*;
import net.evo_mc.warframes.item.custom.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Warframes.MOD_ID)
public class WarframeToolEvents {

    private static final int COOLDOWN_TICKS = 2;
    private static final ResourceLocation MULTI_CONFIGURE_TOOL = new ResourceLocation("valkyrien_warium", "multi_configure_tool");
    private static final ResourceLocation CONTROL_SHIFTER = new ResourceLocation("valkyrien_warium", "control_shifter");

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getItemStack().getItem() instanceof WrenchItem wrench)) {
            return;
        }

        Level level = event.getLevel();
        BlockState state = level.getBlockState(event.getPos());

        if (state.getBlock() instanceof WarframeLandingWheelBlock || state.getBlock() instanceof WarframeLandingStrutBlock) {
            event.setCanceled(true);
            if (level.isClientSide()) {
                return;
            }
            Player player = event.getEntity();
            if (player.getCooldowns().isOnCooldown(wrench)) {
                return;
            }
            BlockPos wheelPos = state.getBlock() instanceof WarframeLandingStrutBlock ? event.getPos().below() : event.getPos();
            BlockState wheelState = level.getBlockState(wheelPos);
            BlockPos bayPos = WarframeLandingGearBayBlock.findBayAbove(level, wheelPos);
            BlockState bayState = bayPos == null ? null : level.getBlockState(bayPos);
            if (wheelState.getBlock() instanceof WarframeLandingWheelBlock
                    && bayState != null
                    && bayState.getBlock() instanceof WarframeLandingGearBayBlock bay
                    && bayState.getValue(WarframeLandingGearBayBlock.OPEN)) {
                bay.cycleVariant(level, bayPos, bayState, wheelPos, wheelState, player.isShiftKeyDown() ? -1 : 1);
                player.getCooldowns().addCooldown(wrench, COOLDOWN_TICKS);
            }
            return;
        }

        if (state.getBlock() instanceof WarframeCountermeasureLauncherSlabBlock) {
            event.setCanceled(true);
            if (level.isClientSide()) {
                return;
            }
            Player player = event.getEntity();
            if (player.getCooldowns().isOnCooldown(wrench)) {
                return;
            }
            level.setBlock(event.getPos(), WarframeCountermeasureLauncherSlabBlock.flipFace(state), Block.UPDATE_ALL);
            level.playSound(null, event.getPos(), SoundEvents.NETHERITE_BLOCK_STEP, SoundSource.BLOCKS, 1f, 1f);
            player.getCooldowns().addCooldown(wrench, COOLDOWN_TICKS);
            return;
        }

        if (!(state.getBlock() instanceof WarframeExtendableBlock extendable)) {
            return;
        }

        event.setCanceled(true);
        if (level.isClientSide()) {
            return;
        }

        Player player = event.getEntity();
        if (player.getCooldowns().isOnCooldown(wrench)) {
            return;
        }

        int current = extendable.getLayers(state);
        boolean decrease = player.isShiftKeyDown();
        int updated = decrease
                ? Math.max(1, current - 1)
                : Math.min(extendable.getMaxLayers(), current + 1);

        if (updated != current) {
            level.setBlock(event.getPos(), extendable.setLayers(state, updated), 3);
            player.getCooldowns().addCooldown(wrench, COOLDOWN_TICKS);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSoundPacket(
                        Holder.direct(SoundEvents.NETHERITE_BLOCK_PLACE),
                        SoundSource.PLAYERS,
                        player.getX(), player.getY(), player.getZ(),
                        1.0F, 1.0F,
                        level.getRandom().nextLong()
                ));
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ModList.get().isLoaded("valkyrien_warium")) {
            return;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        if (!MULTI_CONFIGURE_TOOL.equals(itemId) && !CONTROL_SHIFTER.equals(itemId)) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!(level.getBlockState(pos).getBlock() instanceof WarframeFullLandingGearBayBlock)) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, WarframeLandingGearWarium.createGearConfigMenu(pos), pos);
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.NEUTRAL, 1.0f, 3.0f);
        }
    }
}