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
    static final float[] mineHitCount = {0.5f, 0.2f, 0.1f};
    private float[] matAmount = new float[mineHitCount.length];
    private int locSelectCode = 0;
    private int combineInvenSize = 5;
    private long lastIdleSecTime;
    private int lastIdleMineCount;
    private ArrayList<Integer> combineInvenItemList = new ArrayList<Integer>();
    static ArrayList<Integer> myInvenItemList = new ArrayList<Integer>();
    private static SharedPreferences spf;

    public String getMyInvenItemPack() {
        return Arrays.toString(myInvenItemList.toArray());
    }

    public void removeMyInvenItem(int idx) {
        if (idx >= myInvenItemList.size()) return;
        myInvenItemList.remove(idx);
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

    public String goMix() {
        String msg = "조합식이 없습니다.";

        String tmpMixTable = "";
        Collections.sort(combineInvenItemList);
        for (int i = 0; i < combineInvenItemList.size(); i++) {
            tmpMixTable += combineInvenItemList.get(i);
        }

        if (tmpMixTable.equals("00000")) {
            myInvenItemList.add(0);
            msg = ActMain.mineName[0] + " 조합 성공";
            combineInvenItemList.clear();
        } else if (tmpMixTable.equals("11111")) {
            myInvenItemList.add(1);
            msg = ActMain.mineName[1] + " 조합 성공";
            combineInvenItemList.clear();
        } else if (tmpMixTable.equals("22222")) {
            myInvenItemList.add(2);
            msg = ActMain.mineName[2] + " 조합 성공";
            combineInvenItemList.clear();
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
        addMatAmount(locSelectCode, mineHitCount[locSelectCode]);

        if (mineHitCount[locSelectCode] > (matAmount[locSelectCode] - (int)(matAmount[locSelectCode]))){
            msg = ActMain.mineName[locSelectCode] + " + 1";
        }
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
        long nowMillis = System.currentTimeMillis();
        long destroyMillis = spf.getLong(destoryTimeKey, nowMillis);
        lastIdleSecTime = (nowMillis - destroyMillis) / 1000;
        lastIdleMineCount = (int) (mineHitCount[locSelectCode] * (lastIdleSecTime / 16));
        matAmount[locSelectCode] += lastIdleMineCount;
        Log.d("d", "load");
        Log.d("d", "└locSelectCode : " + locSelectCode);
        Log.d("d", "└combineDataPack : " + combineDataPack);
        Log.d("d", "└matAmountDataPack : " + matAmountDataPack);
    }

    public void saveData() {
        SharedPreferences.Editor editor = spf.edit();
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
        editor.commit();
        Log.d("d", "save");
        Log.d("d", "└locSelectCode : " + locSelectCode);
        Log.d("d", "└combineDataPack : " + combineDataPack);
        Log.d("d", "└matAmountDataPack : " + matAmountDataPack);
    }
}
