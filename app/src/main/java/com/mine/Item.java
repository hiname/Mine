package com.mine;

import android.util.Log;

/**
 * Created by USER on 2016-11-16.
 */
public class Item implements Cloneable{
	private int id;
	private int modelId;
	private int resId;
	private String name;
	private int price;
	private String type;
	private float findChance;
	private int durability;
	private int maxDurability;

	public Object clone() {
		try {
			return (Item)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Item(int id, int modelId, int resId, String name, int price, String type, float addFindChance, int durability) {
		this.id = id;
		this.modelId = modelId;
		this.resId = resId;
		this.name = name;
		this.price = price;
		this.type = type;
		this.findChance = addFindChance;
		this.durability = durability;
		this.maxDurability = durability;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public int getModelId() {
		return modelId;
	}

	public int getResId() {
		return resId;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public String getType() {
		return type;
	}

	public float getFindChance() {
		return findChance;
	}

	public int getDurability() {
		return durability;
	}

	public int getMaxDurability() {
		return maxDurability;
	}

	public void setFindChance(float findChance) {
		this.findChance = findChance;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}

	private String toPack(Object... obj) {
		String pack = "";
		for (int i = 0; i < obj.length; i++) pack += obj[i] + spliter;
		//
		pack = pack.substring(0, pack.length() - spliter.length());
		return pack;
	}

	String spliter = "@";

	public String getModifyPack() {
		return toPack(id, modelId, durability);
	}

	public int consumeDurability() {
		setDurability(durability - 1);
		return durability;
	}
}