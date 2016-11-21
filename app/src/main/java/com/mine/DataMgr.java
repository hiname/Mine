package com.mine;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by USER on 2016-11-09.
 */
public class DataMgr {
	
	private DataMgr() {	}

	private static class Singleton {
		private static final DataMgr instance = new DataMgr();
	}

	public static DataMgr getInstance(Context context) {
		spf = context.getSharedPreferences(saveFileName, Context.MODE_PRIVATE);
		return Singleton.instance;
	}
	
	private final String mixFormula[][] = {
			{"0,0,0,0,0", "0"}, // wood
			{"1,1,1,1,1", "1"}, // stone
			{"2,2,2,2,2", "2"}, // chicken
			{"0,0,0,0,1", "3"}, // stick
			{"0,0,0,1,1", "4"}, // hammer
			{"0,0,1,1,1", "5"}, // maul
			{"0,1,1,1,1", "6"}, // mace
			{"0,0,0,1,2", "7"}, // spear
			{"0,0,1,1,2", "8"}, // morningstar
	};
	
	private float[] matAmount = new float[MineInfo.mineFindChance.length * 2];
	private int locSelectCode = 0;
	//
	private MyItemList myItemList = MyItemList.getInstance();
	private ItemInfo itemInfo = ItemInfo.getInstance();
	//
	private int penaltyFactor = 10;
	
	private void setIdle() {
		long nowMillis = System.currentTimeMillis();
		long destroyMillis = spf.getLong(destoryTimeKey, nowMillis);
		lastIdleSecond = (nowMillis - destroyMillis) / 1000;
		lastIdleMineCount = 0;
		int idleLoopCnt = (int) (lastIdleSecond / penaltyFactor);
		for (int i = 0; i < idleLoopCnt; i++) {
			if (Math.random() < sumFindChance) lastIdleMineCount++;
		}
		matAmount[locSelectCode] += lastIdleMineCount;
	}

	private Item equipItem, lunchItem;

	public Item getEquipItem() {
		return equipItem;
	}

	public Item getLunchItem() {
		return lunchItem;
	}

	public void useItem(Item item) {
		if (item == null) return;
		String type = item.getType();
		if (type.equals(ItemInfo.TYPE_WEAPON))
			equipItem(item);
		else if (type.equals(ItemInfo.TYPE_FOOD))
			lunchItem(item);
	}

	private void equipItem(Item item) {
		Log.d("d", "equipItem_item : " + item);
		if (equipItem != null) releaseEquipment();
		equipItem = item;
		if (mainUpdate != null) mainUpdate.equipItem(equipItem);
		updateFindChance();
	}

	private void lunchItem(Item item) {
		Log.d("d", "lunchItem_item : " + item);
		if (lunchItem != null) releaseLunch();
		lunchItem = item;
		if (mainUpdate != null) mainUpdate.lunchItem(lunchItem);
		updateFindChance();
	}

	public void releaseEquipment() {
		String code = myItemList.tryAddItem(equipItem);
		if (code.equals(DataMgr.resultCode_myItemAddOK)) {
			removeEquipment();
		}
	}

	public void releaseLunch() {
		String code = myItemList.tryAddItem(lunchItem);
		if (code.equals(DataMgr.resultCode_myItemAddOK)) {
			removeLunch();
		}
	}

	public void removeEquipment() {
		Log.d("d", "removeEquipment");
		mainUpdate.removeEquipment();
		//
		equipItem = null;
		updateFindChance();
	}

	public void removeLunch() {
		mainUpdate.removeLunch();
		//
		lunchItem = null;
		updateFindChance();
	}

	public String hitMine() {
		String msg = "hit!";
		double chkRnd = Math.random();
		int resultSelectCode = locSelectCode;
		int find = (chkRnd < sumFindChance) ? 1 : 0;
		if (find > 0) {
			find = (int) (Math.random() * 3) + 1;
			if (find >= 2) {
				chkRnd = Math.random();
				if (chkRnd < sumFindChance) {
					find = 1;
					resultSelectCode += 3;
				}
			}
		}
		addMatAmount(resultSelectCode, find);
		if (find > 0) msg = MineInfo.mineName[resultSelectCode] + " + " + find;
		if (mainUpdate != null) mainUpdate.hit();
		return msg;
	}

	private float sumFindChance;
	private float itemFindChance;

	public float getSumFindChance() {
		return sumFindChance;
	}

	public float getItemFindChance() {
		return itemFindChance;
	}

	private void updateFindChance() {
		Log.d("d", "updateFindChance");
		float mineStandChance = MineInfo.mineFindChance[locSelectCode];
		//
		float equipFindChance = equipItem == null ? 0 : equipItem.getFindChance();
		float lunchFindChance = lunchItem == null ? 0 : lunchItem.getFindChance();
		itemFindChance = equipFindChance + lunchFindChance;
		//
		sumFindChance = mineStandChance + itemFindChance;
		// sumFindChance = MathMgr.roundAndDecimal(sumFindChance);
		//
		if (mainUpdate != null) mainUpdate.updateFindChanceMsg();
	}

	public void setLocSelectCode(int code) {
		locSelectCode = code;
		updateFindChance();
	}

	public int getLocSelectCode() {
		return locSelectCode;
	}

	private ArrayList<Integer> combineInvenItemList = new ArrayList<Integer>();
	private int combineInvenSize = 5;
	
	public String addCombine(int itemId) {
		int combineItemCount = combineInvenItemList.size();
		if (combineItemCount >= combineInvenSize) {
			return "인벤 가득 참";
		}
		if (matAmount[itemId] < 1) {
			return "해당 아이템 수량 부족";
		}
		matAmount[itemId]--;
		combineInvenItemList.add(itemId);
		return "add";
	}

	public String tryMixGetItemName() {
		String resultItemName = DataMgr.resultCode_formulaNotFound;
		String tmpMixTable = "";
		Collections.sort(combineInvenItemList);
		//
		for (int i = 0; i < combineInvenItemList.size(); i++) {
			tmpMixTable += combineInvenItemList.get(i) + ",";
		}
		if (tmpMixTable.length() > 0)
			tmpMixTable = tmpMixTable.substring(0, tmpMixTable.length() - 1);
		//
		for (int i = 0; i < mixFormula.length; i++) {
			// 조합식 발견
			if (tmpMixTable.equals(mixFormula[i][0])) {
				Item mixItem = itemInfo.getItem(Integer.parseInt(mixFormula[i][1]));
				resultItemName = myItemList.tryAddItem(mixItem);
				if (resultItemName.equals(DataMgr.resultCode_myItemAddOK)) {
					resultItemName = itemInfo.getName(i);
					combineInvenItemList.clear();
					break;
				}
			}
		}
		//
		Log.d("d", "mixTable : " + tmpMixTable);
		return resultItemName;
	}
	
	public int getCombineInvenItemCount() {
		return combineInvenItemList.size();
	}

	public int getCombineInvenItem(int idx) {
		if (idx < 0 || combineInvenItemList.size() <= 0)
			return -1;
		return combineInvenItemList.get(idx);
	}

	public void addMatAmount(int idx, float add) {
		if (add != 0) {
			matAmount[idx] += add;
			mainUpdate.updateMatCount();
		}
	}
	
	public float getMatAmount(int idx) {
		return matAmount[idx];
	}

	public String loadString(String name, String def) {
		return spf.getString(locSelectCodeKey, def);
	}

	public void releaseCombine(int idx) {
		int combineItemCount = combineInvenItemList.size();
		if (combineItemCount <= 0) {
			updateSystemMsg(ActMain.TOAST_TOKEN + "아이템 없음");
			return;
		} else if (combineItemCount <= idx) {
			updateSystemMsg(ActMain.TOAST_TOKEN + "잘못된 입력");
		} else {
			int removeItemId = combineInvenItemList.remove(idx);
			matAmount[removeItemId]++;		
		}
	}

	private boolean isFastSell = false;
	private boolean isFastMix = false;
	
	public void setFastSell(boolean isFastSell) {
		this.isFastSell = isFastSell;
	}

	public void setFastMix(boolean isFastMix) {
		this.isFastMix = isFastMix;
	}

	public boolean getFastSell() {
		return isFastSell;
	}

	public boolean getFastMix() {
		return isFastMix;
	}
	
	//
	private MainUpdate mainUpdate;

	public void setMainUpdate(MainUpdate du) {
		mainUpdate = du;
	}
	
	public void updateSystemMsg(String msg) {
		if (mainUpdate != null) mainUpdate.addSystemMsg(msg);
	}
	//
	
	private static SharedPreferences spf;
	static final String saveFileName = "pref";
	//
	private final String destoryTimeKey = "destoryTimeKey";
	private final String matAmountDataKey = "matAmountDataKey";
	private final String locSelectCodeKey = "locSelectCodeKey";
	private final String combinePackKey = "combinePackKey";
	private final String myModifyPackKey = "myModifyPackKey";
	private final String myMoneyKey = "myMoneyKey";
	private final String fastSellKey = "fastSellKey";
	private final String fastMixKey = "fastMixKey";
	private final String equipItemKey = "equipItemKey";
	private final String lunchItemKey = "lunchItemKey";
	private final String myItemLastIdxKey = "myItemLastIdxKey";
	//
	static final String resultCode_formulaNotFound = "resultCode_formulaNotFound";
	static final String resultCode_myInvenFull = "resultCode_myInvenFull";
	static final String resultCode_myItemAddOK = "resultCode_myItemAddOK";
	
	private long lastIdleSecond;
	private int lastIdleMineCount;
	
	public int getLastIdleMineCount() {
		return lastIdleMineCount;
	}

	public long getLastIdleSecTime() {
		return lastIdleSecond;
	}
	
	public void saveData() {
		Log.d("d", "save");
		SharedPreferences.Editor editor = spf.edit();
		// - - -
		// equipItem
		String equipModifyPack = equipItem != null ? equipItem.getModifyPack() : null;
		editor.putString(equipItemKey, equipModifyPack);
		// lunchItem
		String lunchModifyPack = lunchItem != null ? lunchItem.getModifyPack() : null;
		editor.putString(lunchItemKey, lunchModifyPack);
		// isFastSell
		editor.putBoolean(fastSellKey, isFastSell);
		// isFastMix
		editor.putBoolean(fastMixKey, isFastMix);
		// myMoney
		editor.putLong(myMoneyKey, myItemList.getMyMoney());
		// locselectCode
		editor.putInt(locSelectCodeKey, locSelectCode);
		// destoryTime
		editor.putLong(destoryTimeKey, System.currentTimeMillis());
		// combineInvenItemList
		String combineDataPack = "";
		if (combineInvenItemList.size() > 0) {
			for (int combine : combineInvenItemList)
				combineDataPack += String.valueOf(combine) + ",";
			combineDataPack = combineDataPack.substring(0, combineDataPack.length() - 1);
		}
		editor.putString(combinePackKey, combineDataPack);
		// material
		String matAmountDataPack = "";
		for (int i = 0; i < matAmount.length; i++)
			matAmountDataPack += String.valueOf(matAmount[i]) + ",";
		matAmountDataPack = matAmountDataPack.substring(0, matAmountDataPack.length() - 1);
		editor.putString(matAmountDataKey, matAmountDataPack);
		// myItem
		String myModifyPack = myItemList.getModifyPack();
		editor.putString(myModifyPackKey, myModifyPack);
		// myItemLastIdx
		editor.putInt(myItemLastIdxKey, myItemList.getMyItemLastIdx());
		editor.commit();

		Log.d("d", "└equipItem : " + ((equipItem != null) ? equipItem.getId() + ", " + equipItem.getModelId() + ", " + equipItem.getName() : null));
		Log.d("d", "└equipModifyPack : " + equipModifyPack);
		Log.d("d", "└lunchModifyPack : " + lunchModifyPack);
		Log.d("d", "└myModifyPack : " + myModifyPack);
	}

	public void loadData() {
		Log.d("d", "load");
		// - - -
		// equipItem
		String equipModifyPack = spf.getString(equipItemKey, null);
		Log.d("d", "load_equipModifyPack : " + equipModifyPack);
		Item equipItem = itemInfo.modifyToItem(equipModifyPack);
		Log.d("d", "load_equipItem : " + equipItem);
		equipItem(equipItem);
		// lunchItem
		String lunchModifyPack = spf.getString(lunchItemKey, null);
		Item lunchItem = itemInfo.modifyToItem(lunchModifyPack);
		lunchItem(lunchItem);
		// isFastSell
		setFastSell(spf.getBoolean(fastSellKey, isFastSell));
		// isFastMix
		setFastMix(spf.getBoolean(fastMixKey, isFastMix));
		// myMoney
		myItemList.setMyMoney(spf.getLong(myMoneyKey, 0));
		// locselectCode
		setLocSelectCode(spf.getInt(locSelectCodeKey, 0));
		// combineInvenItemList
		String combineDataPack = spf.getString(combinePackKey, null);
		combineInvenItemList.clear();
		if (combineDataPack != null && !combineDataPack.equals("")) {
			for (String combine : combineDataPack.split(","))
				combineInvenItemList.add(Integer.parseInt(combine));
		}
		// material
		String matAmountDataPack = spf.getString(matAmountDataKey, null);
		if (matAmountDataPack != null && !matAmountDataPack.equals("")) {
			String[] matLoadDataList = matAmountDataPack.split(",");
			for (int i = 0; i < matLoadDataList.length; i++) {
				matAmount[i] = Float.parseFloat(matLoadDataList[i]);
			}
		}
		// idle time
		setIdle();
		// myItem
		String myModifyPack = spf.getString(myModifyPackKey, null);
		myItemList.clear();
		if (myModifyPack != null && !myModifyPack.equals(""))
			myItemList.setModifyPackToData(myModifyPack);
		// myItemLastIdx
		int myItemLastIdx = spf.getInt(myItemLastIdxKey, myItemList.getMyItemLastIdx());
		myItemList.setMyItemLastIdx(myItemLastIdx);

		Log.d("d", "└equipItem : " + ((equipItem != null) ? equipItem.getId() + ", " + equipItem.getModelId() + ", " + equipItem.getName() : null));
		Log.d("d", "└equipModifyPack : " + equipModifyPack);
		Log.d("d", "└lunchModifyPack : " + lunchModifyPack);
		Log.d("d", "└myItemModifyPack : " + myModifyPack);
	}
}
