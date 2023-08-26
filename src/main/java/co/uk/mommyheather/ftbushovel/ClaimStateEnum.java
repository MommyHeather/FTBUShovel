package co.uk.mommyheather.ftbushovel;

import co.uk.mommyheather.ftbushovel.config.ConfiguredBlocks;
import net.minecraft.block.Block;

public enum ClaimStateEnum {
    NONE(),
    ENEMY(),
    OWNER(),
    LOADED();

    public Block getMarker() {
        switch (this) {
            case ENEMY : return ConfiguredBlocks.enemy();
            case OWNER : return ConfiguredBlocks.owner();
            case LOADED : return ConfiguredBlocks.loaded();
            case NONE :
            default : return ConfiguredBlocks.indicatorNone();

        }
    }
}
