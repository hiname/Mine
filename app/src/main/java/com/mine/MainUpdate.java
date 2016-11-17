package com.mine;

/**
 * Created by USER on 2016-11-14.
 */
interface MainUpdate {
	public void addSystemMsg(String msg);

	public void equipItem(Item item);
	public void lunchItem(Item item);

	public void removeEquipment();
	public void removeLunch();

	public void updateFindChanceMsg();
	public void updateMyMoney();

	public void hit();
}
