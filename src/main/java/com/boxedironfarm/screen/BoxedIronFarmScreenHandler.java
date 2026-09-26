package com.boxedironfarm.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.HopperScreenHandler;

/**
 * 盒装刷铁机的容器菜单：直接复用原版漏斗的 ScreenHandlerType 与布局（5 格容器）。
 * 客户端会因此自动打开漏斗式 GUI，标题由服务器发送的“盒装刷铁机”提供。
 */
public class BoxedIronFarmScreenHandler extends HopperScreenHandler {

	public BoxedIronFarmScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		super(syncId, playerInventory, inventory);
	}
}
