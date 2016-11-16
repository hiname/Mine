package com.mine;

/**
 * Created by USER on 2016-11-14.
 */
interface DataUpdate {
	public void addSystemMsg(String msg);

	public void equipItem(Item item);

	public void lunchItem(Item item);

	public void releaseEquipment();
	public void releaseLunch();

	public void removeEquipment();
	public void removeLunch();

	public void updateItemStatMsg();
	public void updateMyMoney();

	public void hit();
}
