package net.evo_mc.warframes.block.entity.renderer;

import net.evo_mc.warframes.block.entity.WarframeBombBayDoorBlockEntity;
import net.evo_mc.warframes.block.entity.WarframeBombBayDoorModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WarframeBombBayDoorRenderer extends GeoBlockRenderer<WarframeBombBayDoorBlockEntity> {
    public WarframeBombBayDoorRenderer(BlockEntityRendererProvider.Context context) {
        super(new WarframeBombBayDoorModel());
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}