package net.evo_mc.warframes.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class WarframesServerConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue CONSUME_MIMICKED_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue RESTRICT_MIMICKED_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MIMIC_WHITELIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("mimic");

        CONSUME_MIMICKED_BLOCKS = builder
                .comment("When true, placing a block into a Warframe block in survival uses up that block, and wrenching or breaking the Warframe block gives it back.",
                        "When false, the block is not used up and nothing is given back.",
                        "Set this when creating a world. Changing it later also changes what Warframes blocks drop.")
                .define("consumeMimickedBlocks", true);

        RESTRICT_MIMICKED_BLOCKS = builder
                .comment("When true, only blocks in mimicWhitelist can be mimicked.",
                        "When false, any block can be mimicked.")
                .define("restrictMimickedBlocks", false);

        MIMIC_WHITELIST = builder
                .comment("Blocks that can be mimicked when restrictMimickedBlocks is true.",
                        "Accepts both block IDs and tags.")
                .defineListAllowEmpty(List.of("mimicWhitelist"),
                        () -> List.of("#crusty_chunks:aluminum_armor_blocks", "#crusty_chunks:steel_armor_blocks"),
                        WarframesServerConfig::isValidEntry);

        builder.pop();
        SPEC = builder.build();
    }

    private WarframesServerConfig() {
    }

    private static boolean isValidEntry(Object entry) {
        if (!(entry instanceof String text)) {
            return false;
        }
        String id = text.startsWith("#") ? text.substring(1) : text;
        return ResourceLocation.tryParse(id) != null;
    }

    public static boolean canMimic(BlockState state) {
        if (!RESTRICT_MIMICKED_BLOCKS.get()) {
            return true;
        }
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        for (String entry : MIMIC_WHITELIST.get()) {
            if (entry.startsWith("#")) {
                ResourceLocation tagId = ResourceLocation.tryParse(entry.substring(1));
                if (tagId != null && state.is(TagKey.create(Registries.BLOCK, tagId))) {
                    return true;
                }
            } else if (blockId != null && blockId.equals(ResourceLocation.tryParse(entry))) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldConsume() {
        return CONSUME_MIMICKED_BLOCKS.get();
    }
}