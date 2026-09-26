package com.boxedironfarm.item;

/**
 * 盒装刷铁机物品的捕捉类型：
 * EMPTY —— 空的盒装刷铁机，可对僵尸或村民使用
 * ZOMBIE —— 有僵尸的盒装刷铁机，可对村民使用
 * VILLAGER —— 有村民的盒装刷铁机，可对僵尸使用
 */
public enum CaptureType {
	EMPTY,
	ZOMBIE,
	VILLAGER
}
