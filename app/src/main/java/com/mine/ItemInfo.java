package com.mine;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemInfo {
	static final String TYPE_MATERIAL = "TYPE_MATERIAL";
	static final String TYPE_WEAPON = "TYPE_WEAPON";
	static final String TYPE_FOOD = "TYPE_FOOD";

	private ItemInfo() {
		Object obj[][] = {
				// resId					Name		Price	Type			FindChance	Durability
				{R.drawable.woodblock, "목재블럭", 200, TYPE_MATERIAL, 0.0f, 1},
				{R.drawable.stoneblock, "석재블럭", 300, TYPE_MATERIAL, 0.0f, 1},
				{R.drawable.food_chicken, "치킨", 1000, TYPE_FOOD, 0.2f, 10},
				{R.drawable.stick, "막대기", 300, TYPE_WEAPON, 0.01f, 10},
				{R.drawable.hammer, "망치", 400, TYPE_WEAPON, 0.02f, 50},
				{R.drawable.maul, "큰망치", 500, TYPE_WEAPON, 0.03f, 1000},
				{R.drawable.mace, "메이스", 600, TYPE_WEAPON, 0.04f, 1000},
				{R.drawable.spear, "창", 700, TYPE_WEAPON, 0.05f, 1000},
				{R.drawable.morningstar, "모닝스타", 800, TYPE_WEAPON, 0.06f, 1000},
		};

		for (int i = 0; i < obj.length; i++) {
			Item item = new Item(i, i, (Integer) obj[i][0], obj[i][1].toString(), (Integer) obj[i][2], obj[i][3].toString(), (Float) obj[i][4], (Integer) obj[i][5]);
			addItem(item);

		}
	}

	private static class Singleton {
		private static final ItemInfo instance = new ItemInfo();
	}

	public static ItemInfo getInstance() {
		return Singleton.instance;
	}

	private ArrayList<Item> itemList = new ArrayList<Item>();

	private void addItem(Item item) {
		itemList.add(item);
	}

	public int getListSize() {
		return itemList.size();
	}

	public int getResId(int idx) {
		return itemList.get(idx).getResId();
	}

	public String getName(int idx) {
		return itemList.get(idx).getName();
	}

	public int getPrice(int idx) {
		return itemList.get(idx).getPrice();
	}

	public String getType(int idx) {
		return itemList.get(idx).getType();
	}

	public float getFindChance(int idx) {
		if (idx == -1) return 0;
		return itemList.get(idx).getFindChance();
	}

	public int getDurability(int id) {
		return itemList.get(id).getDurability();
	}

	public Item getItem(int idx) {
		return (Item) itemList.get(idx).clone();
	}

	final int pack_id = 0;
	final int pack_modelId = 1;
	final int pack_durability = 2;

	public Item modifyToItem(String modifyPackStr) {
		Log.d("d", "modifyToItem");
		if (modifyPackStr == null) {
			Log.d("d", "modifyPackStr is null");
			return null;
		}
		String[] modifyPack = modifyPackStr.split("@");
		if (modifyPack == null || modifyPack.length <= 2) {
			Log.d("d", "isNull, len => modifyPack : " + modifyPack + ", modifyPack.lengt : " + modifyPack.length);
			return null;
		}

		Item item = getItem(Integer.parseInt(modifyPack[pack_modelId]));
		item.setId(Integer.parseInt(modifyPack[pack_id]));
		item.setDurability(Integer.parseInt(modifyPack[pack_durability]));
		return item;
	}
}
