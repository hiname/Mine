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
    //
    //
    private DataMgr() {}

    private static class Singleton {
        private static final DataMgr instance = new DataMgr();
    }

    public static DataMgr getInstance(Context context) {
        spf = context.getSharedPreferences(saveFileName, Context.MODE_PRIVATE);
        return Singleton.instance;
    }
    //
    DataUpdate dataUpdate;

    public void setDataUpdate(DataUpdate du) {
        dataUpdate = du;
    }

    public void updateSystemMsg(String msg) {
        if (dataUpdate != null) dataUpdate.addSystemMsg(msg);
    }

    //
    private final float[] mineFindChance = {0.7f, 0.3f, 0.5f};
    //
    private float[] matAmount = new float[mineFindChance.length * 2];
    private int locSelectCode = 0;
    //
    private ArrayList<Integer> combineInvenItemList = new ArrayList<Integer>();
    private int combineInvenSize = 5;
    //
    private long lastIdleSecTime;
    private int lastIdleMineCount;
    //
    private boolean isFastSell = false;
    private boolean isFastMix = false;
    //
    // private long myMoney;
    private MyItemList myItemList = MyItemList.getInstance();
    // private MyItemList myItemList = new MyItemList();
    private ItemInfo itemInfo = ItemInfo.getInstance();

    //
    private int penaltyFactor = 10;

    public void setFastSell(boolean isFastSell) {
        this.isFastSell = isFastSell;
    }
    public void setFastMix(boolean isFastMix) {
        this.isFastMix = isFastMix;
    }

    public boolean getFastSell() {
        return isFastSell;
    }

    public boolean getFastMix() { return isFastMix; }

    private Item equipItem;
    private Item lunchItem;

    public Item getEquipItem() {
        return equipItem;
    }

    public Item getLunchItem() {
        return lunchItem;
    }

    public Item useMyItem(Item item) {
        if (idx == -1 || myItemList.getSize() <= idx) return null;
        Item useItem = myItemList.getByIdx(idx);
        // addMyMoney(itemInfo.getPrice(useItemId));
        String type = useItem.getType();
        if (type.equals(ItemInfo.TYPE_WEAPON)) {
            myItemList.removeByIdx(idx);
            dataUpdate.equipItem(useItem);
            setEquipItem(useItem);
        } else if (type.equals(ItemInfo.TYPE_FOOD)) {
            myItemList.removeByIdx(idx);
            dataUpdate.lunchItem(useItem);
            setLunchItem(useItem);

        } else {
            //
        }
        return useItem;
    }

    private void setLunchItem(Item item) {
        lunchItem = item;
        updateFindChance();
    }

    private void setEquipItem(Item item) {
        equipItem = item;
        updateFindChance();
    }

    public String releaseEquipment() {
        String code = myItemList.tryAddItem(equipItem);
        if (code.equals(DataMgr.resultCode_myItemAddOK)) {
            setEquipItem(null);
            dataUpdate.removeEquipment();
        }
        return code;
    }

    public String releaseLunch() {
        String code = myItemList.tryAddItem(lunchItem);
        if (code.equals(DataMgr.resultCode_myItemAddOK)) {
            setLunchItem(null);
            dataUpdate.removeEquipment();
        }
        return code;
    }

    public void removeLunch() {}

    public void removeEquipment() {}

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

    public String tryMixGetItemName() {
        String resultItemName = resultCode_formulaNotFound;
        String tmpMixTable = "";
        Collections.sort(combineInvenItemList);
        //
        for (int i = 0; i < combineInvenItemList.size(); i++) {
            tmpMixTable += combineInvenItemList.get(i) + ",";
        }
        if (tmpMixTable.length() > 0) tmpMixTable = tmpMixTable.substring(0, tmpMixTable.length() - 1);
        //
        for (int i = 0; i < mixFormula.length; i++) {
            // 조합식 발견
            if (tmpMixTable.equals(mixFormula[i][0])) {

                Item mixItem = itemInfo.getItem(Integer.parseInt(mixFormula[i][1]));
                resultItemName = myItemList.tryAddItem(mixItem);

                if (resultItemName.equals(resultCode_myItemAddOK)) {
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

    public int getLastIdleMineCount() {
        return lastIdleMineCount;
    }

    public long getLastIdleSecTime() {
        return lastIdleSecTime;
    }

    public String hitMine() {
        String msg = "hit!";
        double chkRnd = Math.random();

        int resultSelectCode = locSelectCode;
        int find = (chkRnd < findChance) ? 1 : 0;
        if (find > 0) {
            find = (int)(Math.random() * 3) + 1;
            if (find >= 2) {
                chkRnd = Math.random();
                if (chkRnd < findChance) {
                    find = 1;
                    resultSelectCode += 3;
                }
            }
        }
        addMatAmount(resultSelectCode, find);
        if (find > 0) msg = MineInfo.mineName[resultSelectCode] + " + " + find;
        if (dataUpdate != null) dataUpdate.hit();
        return msg;
    }

    private float findChance;

    public float getFindChance() {
        return findChance;
    }

    private void updateFindChance() {
        float mineStandChance = mineFindChance[locSelectCode];
        float equipFindChance = equipItem.getFindChance();
        float lunchFindChance = lunchItem.getFindChance();
        //
        findChance = mineStandChance + equipFindChance + lunchFindChance;
        findChance = MathMgr.roundAndDecimal(findChance);
        if (dataUpdate != null) dataUpdate.updateItemStatMsg();
    }

    public void setLocSelectCode(int code) {
        locSelectCode = code;
        updateFindChance();
    }

    public int getLocSelectCode() {
        return locSelectCode;
    }

    public void addMatAmount(int idx, float add) {
        matAmount[idx] += add;
    }

    public int getCombineInvenItemCount() {
        return combineInvenItemList.size();
    }

    public int getCombineInvenItem(int idx) {
        if (idx < 0 || combineInvenItemList.size() <= 0)
            return -1;
        return combineInvenItemList.get(idx);
    }

    public float getMatAmount(int idx) {
        return matAmount[idx];
    }

    public String loadString(String name, String def) {
        return spf.getString(locSelectCodeKey, def);
    }

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

    public String removeCombine(int idx) {
        int combineItemCount = combineInvenItemList.size();
        if (combineItemCount <= 0) {
            return "아이템 없음";
        }
        if (combineItemCount <= idx) {
            return "범위를 벗어남";
        }
        int removeItem = combineInvenItemList.remove(idx);
        matAmount[removeItem]++;
        return "remove";
    }

    public void saveData() {
        SharedPreferences.Editor editor = spf.edit();
        // - - -
        // equipItem
        editor.putString(equipItemKey, equipItem.getModifyPack());
        // lunchItem
        editor.putString(lunchItemKey, lunchItem.getModifyPack());
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
        String modifyPack = myItemList.getModifyPack();
        editor.putString(myModifyPackKey, modifyPack);
        // myItemLastIdx
        editor.putInt(myItemLastIdxKey, myItemList.getMyItemLastIdx());

        editor.commit();
        Log.d("d", "save");
        Log.d("d", "└locSelectCode : " + locSelectCode);
        Log.d("d", "└combineDataPack : " + combineDataPack);
        Log.d("d", "└matAmountDataPack : " + matAmountDataPack);
        Log.d("d", "└modifyPack : " + modifyPack);
        Log.d("d", "└equipItem : " + equipItem);

    }

    public void loadData() {
        // - - -
        // equipItem
		// Item equipItem = itemInfo.getItem();
		// spf.getString(equipItemKey, null)

		// equipItem
		Item equipItem = itemInfo.modifyToItem(spf.getString(equipItemKey, null));
        useMyItem(equipItem);

        // lunchItem
		Item lunchItem = itemInfo.modifyToItem(spf.getString(lunchItemKey, null));
		useMyItem(lunchItem);

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
        long nowMillis = System.currentTimeMillis();
        long destroyMillis = spf.getLong(destoryTimeKey, nowMillis);
        lastIdleSecTime = (nowMillis - destroyMillis) / 1000;
        lastIdleMineCount = (int) (findChance * (lastIdleSecTime / penaltyFactor));
        matAmount[locSelectCode] += lastIdleMineCount;
        // myItem
        String myModifyPack = spf.getString(myModifyPackKey, null);
        myItemList.clear();
        if (myModifyPack != null && !myModifyPack.equals("")) myItemList.setModifyPackToData(myModifyPack);
        // myItemLastIdx
        int myItemLastIdx = spf.getInt(myItemLastIdxKey, myItemList.getMyItemLastIdx());
        myItemList.setMyItemLastIdx(myItemLastIdx);
        Log.d("d", "load");
        Log.d("d", "└locSelectCode : " + locSelectCode);
        Log.d("d", "└combineDataPack : " + combineDataPack);
        Log.d("d", "└matAmountDataPack : " + matAmountDataPack);
        Log.d("d", "└nowMillis : " + nowMillis);
        Log.d("d", "└destroyMillis : " + destroyMillis);
        Log.d("d", "└lastIdleSecTime : " + lastIdleSecTime);
        Log.d("d", "└myModifyPack : " + myModifyPack);
        Log.d("d", "└equipItem : " + equipItem);
    }
}
