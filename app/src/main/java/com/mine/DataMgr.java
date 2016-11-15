package com.mine;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final String myItemPackKey = "myItemPackKey";
    private final String myMoneyKey = "myMoneyKey";
    private final String fastSellKey = "fastSellKey";
    private final String fastMixKey = "fastMixKey";
    private final String equipItemKey = "equipItemKey";
    private final String lunchItemKey = "lunchItemKey";
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

    public void setUpdateObject(DataUpdate du) {
        dataUpdate = du;
    }

    public void updateSystemMsg(String msg) {
        if (dataUpdate != null) dataUpdate.addSystemMsg(msg);
    }

    //
    private final float[] mineFindChance = {0.007f, 0.003f, 0.005f};
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
    private long myMoney;
    private ArrayList<Integer> myItemList = new ArrayList<Integer>();
    private ItemInfo itemInfo = ItemInfo.getInstance();

    //
    private int penaltyFactor = 16;

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

    public int getMyItemId(int idx) {
        if (myItemList.size() <= idx) return -1;
        return myItemList.get(idx);
    }

    public long getMyMoney() {
        return myMoney;
    }

    public String getMyInvenItemPack() {
        String itemPack = Arrays.toString(myItemList.toArray()).replace("[", "").replace("]", "").replaceAll("\\s", "");
        Log.d("d", "getMyInvenItemPack : " + itemPack);
        return itemPack;
    }

    public String tryAddMyItem(int itemId) {
        Log.d("d", "tryAddMyItem");
        if (ActInven.invenSize <= myItemList.size()) {
            return resultCode_myInvenFull;
        }
        Log.d("d", "tryAddMyItem_myItemAdd : " + itemId);
        myItemList.add(itemId);
        return resultCode_myItemAddOK;
    }

    private int equipItemId = -1;
    private int lunchItemId = -1;

    public int getEquipItemId() {
        return equipItemId;
    }

    public int getLunchItemId() {
        return lunchItemId;
    }

    public int useMyItem(int idx) {
        if (idx == -1 || myItemList.size() <= idx) return -1;
        int useItemId = myItemList.get(idx);
        // addMyMoney(itemInfo.getPrice(useItemId));
        String type = itemInfo.getType(useItemId);
        if (type.equals(ItemInfo.TYPE_WEAPON)) {
            myItemList.remove(idx);
            dataUpdate.equipItem(useItemId);
            setEquipItemId(useItemId);
        } else if (type.equals(ItemInfo.TYPE_FOOD)) {
            myItemList.remove(idx);
            dataUpdate.lunchItem(useItemId);
            setLunchItemId(useItemId);

        } else {
            //
        }
        return useItemId;
    }

    private void setLunchItemId(int id) {
        lunchItemId = id;
        updateFindChance();
    }

    private void setEquipItemId(int id) {
        equipItemId = id;
        updateFindChance();
    }

    public String releaseItem(int id) {
        if (id < 0) return "잘못된 입력";
        String type = itemInfo.getType(id);
        if (type.equals(ItemInfo.TYPE_WEAPON)) {
            setEquipItemId(-1);
        } else if (type.equals(ItemInfo.TYPE_FOOD)) {
            setLunchItemId(-1);
        }

        return "release";
    }

    public int sellMyItem(int idx) {
        if (idx >= myItemList.size()) return -1;
        int sellItemId = myItemList.remove(idx);
        addMyMoney(itemInfo.getPrice(sellItemId));
        return sellItemId;
    }

    private void addMyMoney(int money) {
        Log.d("d", "addMyMoney : " + money);
        myMoney += money;
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

                resultItemName = tryAddMyItem(Integer.parseInt(mixFormula[i][1]));

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
        float equipFindChance = itemInfo.getFindChance(equipItemId);
        float lunchFindChance = itemInfo.getFindChance(lunchItemId);
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
        // equipItemId
        editor.putInt(equipItemKey, equipItemId);
        // lunchItemId
        editor.putInt(lunchItemKey, lunchItemId);
        // isFastSell
        editor.putBoolean(fastSellKey, isFastSell);
        // isFastMix
        editor.putBoolean(fastMixKey, isFastMix);
        // myMoney
        editor.putLong(myMoneyKey, myMoney);
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
        String myItemPack = "";
        if (myItemList.size() > 0) {
            for (int i = 0; i < myItemList.size(); i++)
                myItemPack += myItemList.get(i) + ",";
            myItemPack = myItemPack.substring(0, myItemPack.length() - 1);
        }
        editor.putString(myItemPackKey, myItemPack);
        editor.commit();
        Log.d("d", "save");
        Log.d("d", "└locSelectCode : " + locSelectCode);
        Log.d("d", "└combineDataPack : " + combineDataPack);
        Log.d("d", "└matAmountDataPack : " + matAmountDataPack);
        Log.d("d", "└myItemPack : " + myItemPack);
        Log.d("d", "└myItemList.size() : " + myItemList.size());
        Log.d("d", "└equipItemId : " + equipItemId);

    }

    public void loadData() {
        // - - -
        // equipItemId
        useMyItem(spf.getInt(equipItemKey, equipItemId));
        // lunchItemId
        lunchItemId = spf.getInt(lunchItemKey, lunchItemId);
        // isFastSell
        setFastSell(spf.getBoolean(fastSellKey, isFastSell));
        // isFastMix
        setFastMix(spf.getBoolean(fastMixKey, isFastMix));
        // myMoney
        myMoney = spf.getLong(myMoneyKey, 0);
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
        String myItemPack = spf.getString(myItemPackKey, null);
        myItemList.clear();
        if (myItemPack != null)
            for (String loadItem : myItemPack.split(",")) {
                if (loadItem != null && loadItem !="") {
                    Log.d("d", "loadData_myItemAdd : " + loadItem);
                    myItemList.add(Integer.parseInt(loadItem));
                }
            }
        Log.d("d", "load");
        Log.d("d", "└locSelectCode : " + locSelectCode);
        Log.d("d", "└combineDataPack : " + combineDataPack);
        Log.d("d", "└matAmountDataPack : " + matAmountDataPack);
        Log.d("d", "└nowMillis : " + nowMillis);
        Log.d("d", "└destroyMillis : " + destroyMillis);
        Log.d("d", "└lastIdleSecTime : " + lastIdleSecTime);
        Log.d("d", "└myItemPack : " + myItemPack);
        Log.d("d", "└myItemList.size() : " + myItemList.size());
        Log.d("d", "└equipItemId : " + equipItemId);
    }
}
