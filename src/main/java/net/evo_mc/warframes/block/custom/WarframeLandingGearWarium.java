package net.evo_mc.warframes.block.custom;

import com.mojang.logging.LogUtils;
import net.mcreator.valkyrienwarium.init.ValkyrienWariumModBlocks;
import net.mcreator.valkyrienwarium.procedures.WheelCasterDetectionProcedure;
import net.mcreator.valkyrienwarium.ships.ForceData;
import net.mcreator.valkyrienwarium.ships.ForceDirectionMode;
import net.mcreator.valkyrienwarium.ships.ForceMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import io.netty.buffer.Unpooled;
import net.mcreator.valkyrienwarium.world.inventory.GearConfigGUIMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public final class WarframeLandingGearWarium {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TIRE_SOUND = new ResourceLocation("valkyrien_warium", "tirestress");

    private static boolean initialized = false;
    private static boolean available = false;
    private static MethodHandle getShip;
    private static Method getVelocity;
    private static Method getWorldToShip;
    private static Method getInertiaData;
    private static Method getMass;
    private static Method getOrCreateForces;
    private static Method addForce;

    private WarframeLandingGearWarium() {
    }

    public static boolean readLandingGear(ServerLevel level, BlockPos nodePos) {
        BlockEntity node = getNode(level, nodePos);
        return node != null && node.getPersistentData().getBoolean("LandingGear");
    }

    public static void brake(ServerLevel level, BlockPos wheelPos, BlockState wheelState, BlockPos nodePos) {
        BlockEntity node = getNode(level, nodePos);
        if (node == null || node.getPersistentData().getDouble("Throttle") >= 0.0 || !init()) {
            return;
        }
        try {
            Object ship = getShip.invoke(level, (Vec3i) wheelPos);
            if (ship == null) {
                return;
            }
            Vector3dc velocity = (Vector3dc) getVelocity.invoke(ship);
            double speed = velocity.length();
            if (speed < 0.05) {
                return;
            }
            if (!WheelCasterDetectionProcedure.execute(level, wheelPos.getX(), wheelPos.getY() + 1, wheelPos.getZ(), wheelState)) {
                return;
            }
            if (speed > 10.0) {
                level.sendParticles(ParticleTypes.CLOUD,
                        wheelPos.getX() + 0.5, wheelPos.getY(), wheelPos.getZ() + 0.5,
                        2, 0.0, 0.0, 0.0, 0.01);
                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(TIRE_SOUND);
                if (sound != null) {
                    level.playSound(null, wheelPos, sound, SoundSource.BLOCKS, 2.0f,
                            Mth.randomBetween(level.getRandom(), 0.9f, 1.1f));
                }
            }
            Matrix4dc worldToShip = (Matrix4dc) getWorldToShip.invoke(ship);
            Vector3d direction = worldToShip.transformDirection(velocity, new Vector3d()).negate();
            double mass = ((Number) getMass.invoke(getInertiaData.invoke(ship))).doubleValue();
            Object forces = getOrCreateForces.invoke(null, ship);
            addForce.invoke(forces, wheelPos,
                    new ForceData(direction, Math.abs(0.75 * mass), ForceMode.POSITION, ForceDirectionMode.SHIP));
        } catch (Throwable e) {
            available = false;
            LOGGER.warn("Warframes landing gear braking disabled after an error", e);
        }
    }

    private static boolean init() {
        if (initialized) {
            return available;
        }
        initialized = true;
        try {
            ClassLoader loader = WarframeLandingGearWarium.class.getClassLoader();
            Class<?> gameUtils = Class.forName("org.valkyrienskies.mod.common.VSGameUtilsKt", true, loader);
            Class<?> loadedServerShip = Class.forName("org.valkyrienskies.core.api.ships.LoadedServerShip", true, loader);
            Class<?> forceInducedShips = Class.forName("net.mcreator.valkyrienwarium.ships.ForceInducedShips", true, loader);
            getShip = MethodHandles.publicLookup().findStatic(gameUtils, "getLoadedShipManagingPos",
                    MethodType.methodType(loadedServerShip, ServerLevel.class, Vec3i.class));
            getVelocity = loadedServerShip.getMethod("getVelocity");
            getWorldToShip = loadedServerShip.getMethod("getWorldToShip");
            getInertiaData = loadedServerShip.getMethod("getInertiaData");
            getMass = getInertiaData.getReturnType().getMethod("getMass");
            getOrCreateForces = forceInducedShips.getMethod("getOrCreate", loadedServerShip);
            addForce = forceInducedShips.getMethod("addForce", BlockPos.class, ForceData.class);
            available = true;
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.warn("Warframes landing gear braking disabled: Valkyrien Skies API not found", e);
        }
        return available;
    }

    @Nullable
    private static BlockEntity getNode(ServerLevel level, BlockPos nodePos) {
        if (level.getBlockState(nodePos).getBlock() != ValkyrienWariumModBlocks.VEHICLE_CONTROL_NODE.get()) {
            return null;
        }
        return level.getBlockEntity(nodePos);
    }

    public static MenuProvider createGearConfigMenu(BlockPos pos) {
        return new SimpleMenuProvider(
                (id, inventory, player) -> new GearConfigGUIMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos)),
                Component.literal("Gear Config"));
    }
}