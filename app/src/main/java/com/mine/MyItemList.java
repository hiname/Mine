package com.mine;

import android.util.Log;

import java.util.LinkedHashMap;

/**
 * Created by USER on 2016-11-16.
 */
public class MyItemList {
	private LinkedHashMap<Integer, Item> itemList = new LinkedHashMap<Integer, Item>();

	public void clear() {
		itemList.clear();
	}

	public Item getByIdx(int idx) {
		return (Item) itemList.values().toArray()[idx];
	}

	public Item removeByIdx(int idx) {
		int key = (Integer) itemList.keySet().toArray()[idx];
		return itemList.remove(key);
	}

	private MyItemList() {}

	private static class Singleton {
		private static MyItemList instance = new MyItemList();
	}

	public static MyItemList getInstance() {
		return Singleton.instance;
	}

	public int getSize() {
		return itemList.size();
	}

	public int getListSize() {
		return itemList.size();
	}

	public int getResId(int id) {
		return itemList.get(id).getResId();
	}

	public String getName(int id) {
		return itemList.get(id).getName();
	}

	public int getPrice(int id) {
		return itemList.get(id).getPrice();
	}

	public String getType(int id) {
		return itemList.get(id).getType();
	}

	public float getFindChance(int id) {
		if (id == -1) return 0;
		return itemList.get(id).getFindChance();
	}

	public int getDurability(int id) {
		return itemList.get(id).getDurability();
	}

	public Item getItem(int id) {
		return itemList.get(id);
	}

	public void setFindChance(int id, float findChance) {
		itemList.get(id).setFindChance(findChance);
	}

	public void setDurability(int id, int durability) {
		itemList.get(id).setDurability(durability);
	}


	public String getModifyPack() {
		String itemPack = "";
		for (Item item : itemList.values()) {
			itemPack += item.getModifyPack() + ",";
		}
		if (!itemPack.equals("")) itemPack.substring(0, itemPack.length() - 1);
		Log.d("d", "myItemList.getItemIdPack : " + itemPack);
		return itemPack;
	}

	public String getItemIdListPack() {
		String itemIdPack = "";
		for (Item item : itemList.values()) {
			itemIdPack += item.getId() + ",";
		}
		if (!itemIdPack.equals("")) itemIdPack.substring(0, itemIdPack.length() - 1);
		Log.d("d", "myItemList.getItemIdPack : " + itemIdPack);
		return itemIdPack;
	}

	public String getItemIdPack() {
        // String itemPack = itemList.keySet().toString().replace("[", "").replace("]", "").replaceAll("\\s", "");
		String itemPack = "";
		for (Item item : itemList.values()) {
			itemPack += item.getId() + ",";
		}
		if (!itemPack.equals("")) itemPack.substring(0, itemPack.length() - 1);
        Log.d("d", "myItemList.getItemIdPack : " + itemPack);
        return itemPack;
    }

	public String getItemModelIdPack() {
		// String mdPack = itemList.keySet().toString().replace("[", "").replace("]", "").replaceAll("\\s", "");
		String mdPack = "";
		for (Item item : itemList.values()) {
			mdPack += item.getModelId() + ",";
		}
		if (!mdPack.equals("")) mdPack.substring(0, mdPack.length() - 1);
		Log.d("d", "myItemList.getItemModelIdPack : " + mdPack);
		return mdPack;
	}

	private ItemInfo itemInfo = ItemInfo.getInstance();
	private int myItemLastIdx = 0;

	public int getMyItemLastIdx() {
		return myItemLastIdx;
	}

	public void setMyItemLastIdx(int myItemLastIdx) {
		this.myItemLastIdx = myItemLastIdx;
	}

	public String tryAddItem(Item item) {
        Log.d("d", "tryAddItem");
        if (ActInven.invenSize <= itemList.size()) {
            return DataMgr.resultCode_myInvenFull;
        } else if (item == null) {
			return "└item is null ";
		}

		addItem(item);
        Log.d("d", "└addItem Id, name : " + item.getId() + ", " + item.getName());
		Log.d("d", "myItemLastIdx : " + myItemLastIdx);

        return DataMgr.resultCode_myItemAddOK;
    }

	public int sellItem(int idx) {
		if (idx == -1 || idx >= getSize()) return -1;
		int sellItemId = removeByIdx(idx).getId();
		addMyMoney(getPrice(sellItemId));
		return sellItemId;
	}

	private long myMoney;

	public long getMyMoney() {
		return myMoney;
	}

	private void addMyMoney(int money) {
		Log.d("d", "addMyMoney : " + money);
		myMoney += money;

		if (dataUpdate != null) dataUpdate.updateMyMoney();
	}

	public void setDataUpdate(DataUpdate du) {
		dataUpdate = du;
	}

	DataUpdate dataUpdate;

	public void setMyMoney(long myMoney) {
		this.myMoney = myMoney;
	}

	public void addItem(Item addItem) {
		Log.d("d", "addItem_myItemLastIdx : " + myItemLastIdx);
		addItem.setId(myItemLastIdx);
		itemList.put(myItemLastIdx, addItem);
		myItemLastIdx++;
	}

	public void setModifyPackToData(String packListStr) {
		Log.d("d", "packListStr : " + packListStr);
		String[] packList = packListStr.split(",");
		for (String packStr : packList) {
			Item item = itemInfo.modifyToItem(packStr);
			itemList.put(item.getId(), item);
		}

	}
}
