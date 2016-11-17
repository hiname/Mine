package com.mine;

import android.util.Log;

import java.util.LinkedHashMap;

/**
 * Created by USER on 2016-11-16.
 */
public class MyItemList {
	private LinkedHashMap<Integer, Item> myItemList = new LinkedHashMap<Integer, Item>();
	private InvenUpdate invenUpdate;

	public void setInvenUpdate(InvenUpdate invenUpdate) {
		this.invenUpdate = invenUpdate;
	}

	public void clear() {
		myItemList.clear();
	}

	public Item getItemByIdx(int idx) {
		if (idx < 0 || getSize() <= idx) return null;
		int id = ((Item) myItemList.values().toArray()[idx]).getId();
		return getItem(id);
	}

	public Item[] getItemList() {
		Item[] itemList = new Item[getSize()];
		Integer[] idKeys = myItemList.keySet().toArray(new Integer[itemList.length]);
		for (int i = 0; i < itemList.length; i++) {
			itemList[i] = getItem(idKeys[i]);
		}
		return itemList;
	}


	public Item remove(int id) {
		return myItemList.remove(id);
	}

	public Item removeByIdx(int idx) {
		int key = (Integer) myItemList.keySet().toArray()[idx];
		return myItemList.remove(key);
	}

	private MyItemList() {}

	private static class Singleton {
		private static MyItemList instance = new MyItemList();
	}

	public static MyItemList getInstance() {
		return Singleton.instance;
	}

	public int getSize() {
		return myItemList.size();
	}

	public int getListSize() {
		return myItemList.size();
	}

	public int getResId(int id) {
		return myItemList.get(id).getResId();
	}

	public String getName(int id) {
		return myItemList.get(id).getName();
	}

	public int getPrice(int id) {
		Log.d("d", "getPrice");
		Log.d("d", "└id : " + id);
		Log.d("d", "└myItemList.get(id) : " + myItemList.get(id));
		Log.d("d", "└entry : " + myItemList.entrySet().toString());
		return myItemList.get(id).getPrice();
	}

	public String getType(int id) {
		return myItemList.get(id).getType();
	}

	public float getFindChance(int id) {
		if (id == -1) return 0;
		return myItemList.get(id).getFindChance();
	}

	public int getDurability(int id) {
		return myItemList.get(id).getDurability();
	}

	public int getMaxDurability(int id) {
		return myItemList.get(id).getMaxDurability();
	}

	public Item getItem(int id) {
		return myItemList.get(id);
	}

	public void setFindChance(int id, float findChance) {
		myItemList.get(id).setFindChance(findChance);
	}

	public void setDurability(int id, int durability) {
		myItemList.get(id).setDurability(durability);
	}


	public String getModifyPack() {
		String itemPack = "";
		for (Item item : myItemList.values()) {
			itemPack += item.getModifyPack() + ",";
		}
		if (!itemPack.equals("")) itemPack = itemPack.substring(0, itemPack.length() - 1);
		Log.d("d", "myItemList.getItemIdPack : " + itemPack);
		return itemPack;
	}

	public String getItemIdListPack() {
		String itemIdPack = "";
		for (Item item : myItemList.values()) {
			itemIdPack += item.getId() + ",";
		}
		if (!itemIdPack.equals("")) itemIdPack.substring(0, itemIdPack.length() - 1);
		Log.d("d", "myItemList.getItemIdPack : " + itemIdPack);
		return itemIdPack;
	}

	public String getItemIdPack() {
		// String itemPack = myItemList.keySet().toString().replace("[", "").replace("]", "").replaceAll("\\s", "");
		String itemPack = "";
		for (Item item : myItemList.values()) {
			itemPack += item.getId() + ",";
		}
		if (!itemPack.equals("")) itemPack.substring(0, itemPack.length() - 1);
		Log.d("d", "myItemList.getItemIdPack : " + itemPack);
		return itemPack;
	}

	public String getItemModelIdPack() {
		// String mdPack = myItemList.keySet().toString().replace("[", "").replace("]", "").replaceAll("\\s", "");
		String mdPack = "";
		for (Item item : myItemList.values()) {
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
		if (ActInven.invenSize <= myItemList.size()) {
			return DataMgr.resultCode_myInvenFull;
		} else if (item == null) {
			return "└item is null ";
		}

		addItem(item);
		Log.d("d", "└addItem Id, name : " + item.getId() + ", " + item.getName());
		Log.d("d", "myItemLastIdx : " + myItemLastIdx);

		return DataMgr.resultCode_myItemAddOK;
	}

	public Item sellItemByIdx(int idx) {
		if (idx == -1 || idx >= getSize()) return null;

		Item sellItem = getItemByIdx(idx);
		addMyMoney(getSellPrice(sellItem.getId()));
		return remove(sellItem.getId());
	}

	public float getDurabilityRatio(int id) {
		Item item = getItem(id);
		int durability = item.getDurability();
		int maxDurability = item.getMaxDurability();
		return (float) durability / maxDurability;
	}

	public int getSellPrice(int id) {
		Item item = getItem(id);
		float durabilityRatio = getDurabilityRatio(id);
		int sellPrice = item.getPrice();
		int calcPrice = (int)(sellPrice * durabilityRatio);
		return calcPrice;
	}

	private long myMoney;

	public long getMyMoney() {
		return myMoney;
	}

	private void addMyMoney(int money) {
		Log.d("d", "addMyMoney : " + money);
		myMoney += money;

		if (mainUpdate != null) mainUpdate.updateMyMoney();
	}

	public void setMainUpdate(MainUpdate du) {
		mainUpdate = du;
	}

	MainUpdate mainUpdate;

	public void setMyMoney(long myMoney) {
		this.myMoney = myMoney;
	}

	public void addItem(Item item) {
		Log.d("d", "addItem");
		Log.d("d", "└myItemLastIdx : " + myItemLastIdx);
		item.setId(myItemLastIdx);
		myItemList.put(myItemLastIdx, item);
		myItemLastIdx++;
		//
		if (invenUpdate != null) invenUpdate.updateInven();
		Log.d("d", "└entry : " + myItemList.entrySet().toString());
	}

	public void setModifyPackToData(String packListStr) {
		Log.d("d", "packListStr : " + packListStr);
		String[] packList = packListStr.split(",");
		for (String packStr : packList) {
			Item item = itemInfo.modifyToItem(packStr);
			myItemList.put(item.getId(), item);
		}

	}
}
