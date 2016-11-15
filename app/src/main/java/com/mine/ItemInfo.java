package com.mine;

import java.util.HashMap;

public class ItemInfo {
	static final String TYPE_MATERIAL = "TYPE_MATERIAL";
	static final String TYPE_WEAPON = "TYPE_WEAPON";
	static final String TYPE_FOOD = "TYPE_FOOD";

	private ItemInfo() {

		Object obj[][] = {
				// resId					Name		Price	Type			FindChance	Durability
				{R.drawable.woodblock,		"목재블럭",	200,	TYPE_MATERIAL, 0.0f, 1},
				{R.drawable.stoneblock,	"석재블럭",	300,	TYPE_MATERIAL, 0.0f, 1},
				{R.drawable.food_chicken,	"치킨",		1000, 	TYPE_FOOD, 0.2f, 100},
				{R.drawable.stick,			"막대기",	300,	TYPE_WEAPON, 0.01f, 500},
				{R.drawable.hammer,			"망치",		400, 	TYPE_WEAPON, 0.02f, 50},
				{R.drawable.maul,			"큰망치",	500, 	TYPE_WEAPON, 0.03f, 1000},
				{R.drawable.mace,			"메이스",	600, 	TYPE_WEAPON, 0.04f, 1000},
				{R.drawable.spear,			"창",		700, 	TYPE_WEAPON, 0.05f, 1000},
				{R.drawable.morningstar,	"모닝스타",	800, 	TYPE_WEAPON, 0.06f, 1000},

		};
		int cntId = 0;

		for (int i = 0; i < obj.length; i++) {
			addItem(cntId++, new Item((int)obj[i][0], obj[i][1].toString(), (int)obj[i][2], obj[i][3].toString(), (float)obj[i][4], (int)obj[i][5]));
		}


	}

	private static class Singleton {
		private static final ItemInfo instance = new ItemInfo();
	}

	public static ItemInfo getInstance() {
		return Singleton.instance;
	}

	private class Item {
		private int resId;
		private String name;
		private int price;
		private String type;
		private float findChance;
		private int durability;

		public Item(int resId, String name, int price, String type, float addFindChance, int durability) {
			this.resId = resId;
			this.name = name;
			this.price = price;
			this.type = type;
			this.findChance = addFindChance;
			this.durability = durability;
		}
	}

	private HashMap<Integer, Item> itemList = new HashMap<Integer, Item>();

	private void addItem(int id, Item item) {
		itemList.put(id, item);
	}

	public int getListSize() {
		return itemList.size();
	}

	public int getResId(int id) {
		return itemList.get(id).resId;
	}

	public String getName(int id) {
		return itemList.get(id).name;
	}

	public int getPrice(int id) {
		return itemList.get(id).price;
	}

	public String getType(int id) {
		return itemList.get(id).type;
	}

	public float getFindChance(int id) {
		if (id == -1) return 0;
		return itemList.get(id).findChance;
	}

	public int getDurability(int id) {
		return itemList.get(id).durability;
	}

	public Item getItem(int id) {
		return itemList.get(id);
	}
}
