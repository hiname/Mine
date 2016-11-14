package com.mine;

import java.util.HashMap;


public class ItemInfo {

    private ItemInfo(){
        int cntId = 0;
        addItem(cntId++, R.drawable.woodblock, "목재블럭", 200, "재료", 0.0f);
        addItem(cntId++, R.drawable.stoneblock, "석재블럭", 300, "재료", 0.0f);
        addItem(cntId++, R.drawable.goldblock, "황금블럭", 1000, "재료", 0.0f);
        addItem(cntId++, R.drawable.stick, "막대기", 300, "무기", 0.0f);
        addItem(cntId++, R.drawable.hammer, "망치", 400, "무기", 0.0f);
        addItem(cntId++, R.drawable.maul, "큰망치", 500, "무기", 0.0f);
        addItem(cntId++, R.drawable.mace, "메이스", 600, "무기", 0.0f);
        addItem(cntId++, R.drawable.spear, "창", 700, "무기", 0.0f);
        addItem(cntId++, R.drawable.morningstar, "모닝스타", 800, "무기", 0.0f);
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
        private float addFindChance;

        public Item(int resId, String name, int price, String type, float addFindChance) {
            this.resId = resId;
            this.name = name;
            this.price = price;
            this.type = type;
            this.addFindChance = addFindChance;
        }
    }

    private HashMap<Integer, Item> itemList = new HashMap<Integer, Item>();

    public void addItem(int id, int resId, String name, int price, String type, float addFindChance) {
        itemList.put(id, new Item(resId, name, price, type, addFindChance));
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
        return itemList.get(id).addFindChance;
    }

    public Item getItem(int id) {
        return itemList.get(id);
    }

}
