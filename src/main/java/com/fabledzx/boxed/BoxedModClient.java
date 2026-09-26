
package com.fabledzx.boxed;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BoxedModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(BoxedMod.BOX_SCREEN_HANDLER,
            BoxedIronFarmScreen::new);
        HandledScreens.register(BoxedMod.STRING_SCREEN_HANDLER,
            BoxedStringFarmScreen::new);
    }
}
