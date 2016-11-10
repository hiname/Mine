package com.mine;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by USER on 2016-11-09.
 */
public class DataMgr {

    static final String saveFileName = "pref";
    static final String destoryTimeKey = "destoryTimeKey";
    static final String matAmountDataKey = "matAmountDataKey";
    static final String locSelectCodeKey = "locSelectCodeKey";
    static final String combinePackKey = "combinePackKey";
    static final String myItemPackKey = "myItemPackKey";
    static final String myMoneyKey = "myMoneyKey";

    private long myMoney = 0;

    static final float[] mineHitCount = {0.125f, 0.05f, 0.025f};
    private float[] matAmount = new float[mineHitCount.length];
    private int locSelectCode = 0;
    private int combineInvenSize = 5;
    private long lastIdleSecTime;
    private int lastIdleMineCount;
    private ArrayList<Integer> combineInvenItemList = new ArrayList<Integer>();
    private ArrayList<Integer> myItemList = new ArrayList<Integer>();
    private static SharedPreferences spf;

    public int getMyItemId(int idx) {
        if (myItemList.size() <= idx) return -1;
        return myItemList.get(idx);
    }

    public long getMyMoney() {
        return myMoney;
    }

    public String getMyInvenItemPack() {
        return Arrays.toString(myItemList.toArray());
    }

    private int myItemPrice[][] = {
            {0, 100},
            {1, 300},
            {2, 1000},
    };

    public String tryAddMyItem(int itemId) {

        if (ActInven.invenSize <= myItemList.size()) {
            return "인벤 가득 참";
        }

        myItemList.add(itemId);

        return "add";
    }

    public int sellMyItem(int idx) {
        if (idx >= myItemList.size()) return -1;

        int sellItemId = myItemList.remove(idx);
        addMyMoney(myItemPrice[sellItemId][1]);

        return myItemPrice[sellItemId][1];
    }

    private void addMyMoney(int money) {
        myMoney += money;
    }

    private DataMgr () {}
    private static class Singleton {
        private static final DataMgr instance = new DataMgr();
    }

    public static DataMgr getInstance (Context context) {
        System.out.println("create instance");
        spf = context.getSharedPreferences(saveFileName, Context.MODE_PRIVATE);
        return Singleton.instance;
    }

    private final String mixFormula[][] = {
            {"00000", "0"},
            {"11111", "1"},
            {"22222", "2"},
    };

    public String tryMix() {
        String msg = "조합식이 없습니다.";

        String tmpMixTable = "";
        Collections.sort(combineInvenItemList);
        for (int i = 0; i < combineInvenItemList.size(); i++) {
            tmpMixTable += combineInvenItemList.get(i);
        }

        for (int i = 0; i < mixFormula.length; i++) {
            if (tmpMixTable.equals(mixFormula[i][0])) {
                msg = tryAddMyItem(Integer.parseInt(mixFormula[i][1]));
                if (msg.equals("add")){
                    msg = ActMain.mineName[i] + " 조합 성공";
                    combineInvenItemList.clear();
                }
            }
        }

        Log.d("d", "mixTable : " + tmpMixTable);
        return msg;
    }

    public int getLastIdleMineCount() {
        return lastIdleMineCount;
    }

    public long getLastIdleSecTime() {
        return lastIdleSecTime;
    }

    public String hitMine() {
        String msg = "hit!";
        int find = (Math.random() < mineHitCount[locSelectCode]) ? 1 : 0;
        addMatAmount(locSelectCode, find);
        if (find > 0) msg = ActMain.mineName[locSelectCode] + " + " + find;

//        if (mineHitCount[locSelectCode] > (matAmount[locSelectCode] - (int)(matAmount[locSelectCode]))){
//            msg = ActMain.mineName[locSelectCode] + " + 1";
//        }
        return msg;
    }

    public void setLocSelectCode(int code) {
        locSelectCode = code;
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

    public void loadData() {

        // myMoney
        myMoney = spf.getLong(myMoneyKey, 0);

        // locselectCode
        locSelectCode = spf.getInt(locSelectCodeKey, 0);
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
        lastIdleMineCount = (int) (mineHitCount[locSelectCode] * (lastIdleSecTime / 16));
        matAmount[locSelectCode] += lastIdleMineCount;

        // myItem
        String myItemPack = spf.getString(myItemPackKey, null);
        myItemList.clear();
        if (myItemPack != null)
            for (String loadItem : myItemPack.split(",")){
                myItemList.add(Integer.parseInt(loadItem));
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

    }

    public void saveData() {
        SharedPreferences.Editor editor = spf.edit();
        //

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
            editor.putString(combinePackKey, combineDataPack);
        }

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

            editor.putString(myItemPackKey, myItemPack);
        }

        editor.commit();
        Log.d("d", "save");
        Log.d("d", "└locSelectCode : " + locSelectCode);
        Log.d("d", "└combineDataPack : " + combineDataPack);
        Log.d("d", "└matAmountDataPack : " + matAmountDataPack);
        Log.d("d", "└myItemPack : " + myItemPack);
        Log.d("d", "└myItemList.size() : " + myItemList.size());
    }
}
