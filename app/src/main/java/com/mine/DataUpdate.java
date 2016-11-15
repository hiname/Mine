package com.mine;

/**
 * Created by USER on 2016-11-14.
 */
interface DataUpdate {
	public void addSystemMsg(String msg);

	public void equipItem(int id);

	public void lunchItem(int id);

	public String releaseItem(int id);

	public void updateItemStatMsg();

	public void hit();
}
