package com.boxedironfarm;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxedIronFarmMod implements ModInitializer {
	public static final String MOD_ID = "boxedironfarm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.register();
		ModBlockEntities.register();
		ModRecipeSerializers.register();
		LOGGER.info("[盒装刷铁机] 已加载！");
	}
}
