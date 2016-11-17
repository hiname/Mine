package com.mine;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by USER on 2016-11-09.
 */
public class ActInven extends Activity implements InvenUpdate {
	DataMgr dataMgr;
	MyItemList myItemList = MyItemList.getInstance();
	ItemInfo itemInfo = ItemInfo.getInstance();
	static int invenSize = 9;
	Dialog dlg;
	ImageView ivPopupIcon;
	Button btnPopupSell, btnPopupOpt;
	TextView tvDlgTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("d", "onCreate");
		setContentView(R.layout.act_inven);
		dataMgr = DataMgr.getInstance(this);
		dlg = new Dialog(this);
		LinearLayout llDlgRoot = new LinearLayout(this);
		llDlgRoot.setOrientation(LinearLayout.VERTICAL);
		LinearLayout llDlgTop = new LinearLayout(this);
		llDlgTop.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lpTop = new LinearLayout.LayoutParams(-1, 0);
		lpTop.weight = 0.7f;
		llDlgTop.setLayoutParams(lpTop);
		//
		LinearLayout llDlgBottom = new LinearLayout(this);
		llDlgBottom.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lpBottom = new LinearLayout.LayoutParams(-1, 0);
		lpBottom.weight = 0.3f;
		llDlgBottom.setLayoutParams(lpBottom);
		tvDlgTitle = new TextView(this);
		llDlgTop.addView(tvDlgTitle);
		tvDlgTitle.setGravity(Gravity.CENTER);
		ivPopupIcon = new ImageView(this);
		ivPopupIcon.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		llDlgTop.addView(ivPopupIcon);
		// 팝업 하단 버튼
		LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(-1, 0);
		btnPopupSell = new Button(this);
		btnPopupSell.setLayoutParams(lpBtn);
		btnPopupSell.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sellItem();
				dlg.dismiss();
			}
		});
		//
		btnPopupOpt = new Button(this);
		lpBtn.weight = 1;
		btnPopupOpt.setLayoutParams(lpBtn);
		btnPopupOpt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				useItem();
				dlg.dismiss();
			}
		});
		btnPopupOpt.setVisibility(View.GONE);
		llDlgBottom.addView(btnPopupSell);
		llDlgBottom.addView(btnPopupOpt);
		llDlgRoot.addView(llDlgTop);
		llDlgRoot.addView(llDlgBottom);
		dlg.setContentView(llDlgRoot);
		CheckBox cbFastSell = (CheckBox) findViewById(R.id.cbFastSell);
		cbFastSell.setChecked(dataMgr.getFastSell());
		cbFastSell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				dataMgr.setFastSell(isChecked);
			}
		});

		initIvItemList();
		myItemList.setInvenUpdate(this);
		updateInven();
		setOnClickListener();
	}

	public void useItem() {
		Item useItem = myItemList.getItemByIdx(lastSelIdx);
		dataMgr.useItem(useItem);
		Log.d("d", "useItem : " + useItem);
		myItemList.remove(useItem.getId());
		Log.d("d", "useItem.getId() : " + useItem.getId());
		String useItemMsg = ActMain.TOAST_TOKEN + useItem.getName() + " 사용."
				+ "\n발견 확률 "
				+ MathMgr.roundPer(useItem.getFindChance()) + "% 상승";
		dataMgr.updateSystemMsg(useItemMsg);
		updateInven();
	}

	public void sellItem() {
		Item sellItem = myItemList.getItemByIdx(lastSelIdx);
		if (sellItem == null) return;

		String sellItemName = sellItem.getName();
		int sellItemPrice = myItemList.getSellPrice(sellItem.getId());
		String sellItemMsg = ActMain.TOAST_TOKEN + sellItemName + " 판매 " + sellItemPrice + "원 획득";
		myItemList.sellItemByIdx(lastSelIdx);
		dataMgr.updateSystemMsg(sellItemMsg);
		updateInven();
	}

	int lastSelIdx = -1;

	public void setOnClickListener() {
		LinearLayout llInven = (LinearLayout) findViewById(R.id.llInven);
		int itemCnt = 0;
		for (int i = 0; i < llInven.getChildCount(); i++) {
			LinearLayout llChild = (LinearLayout) llInven.getChildAt(i);
			for (int j = 0; j < llChild.getChildCount(); j++) {
				final int selIdx = itemCnt;
				Object obj = llChild.getChildAt(j);
				if (obj instanceof ImageView)
					((ImageView) obj).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							lastSelIdx = selIdx;
							if (dataMgr.getFastSell()) {
								sellItem();
							} else {
								Item getItem = myItemList.getItemByIdx(lastSelIdx);
								if (getItem == null) return;
								// int getItemId = getItem.getId();
								String title = getItem.getName()
										+ "\n" + getItem.getDurability() + "/" + getItem.getMaxDurability();
								tvDlgTitle.setText(title);
								ivPopupIcon.setImageResource(getItem.getResId());
								btnPopupSell.setText("판매");
								btnPopupOpt.setVisibility(View.VISIBLE);
								btnPopupOpt.setEnabled(true);
								String type = getItem.getType();
								String btnText = null;
								if (type.equals(ItemInfo.TYPE_WEAPON)) {
									btnText = "장착";
								} else if (type.equals(ItemInfo.TYPE_FOOD)) {
									btnText = "먹기";
								}
								if (getItem.getDurability() <= 0) {
									btnText += "(망가짐)";
									btnPopupOpt.setEnabled(false);
								}
								if (btnText != null)
									btnPopupOpt.setText(btnText);
								dlg.show();
							}
						}
					});
				itemCnt++;
			}
		}
	}

	ArrayList<ImageView> ivItemList = new ArrayList<ImageView>();

	private void initIvItemList() {
		LinearLayout llInven = (LinearLayout) findViewById(R.id.llInven);
		for (int i = 0; i < llInven.getChildCount(); i++) {
			LinearLayout llChild = (LinearLayout) llInven.getChildAt(i);
			for (int j = 0; j < llChild.getChildCount(); j++) {
				Object getChildView = llChild.getChildAt(j);
				if (getChildView instanceof ImageView) {
					ivItemList.add((ImageView) getChildView);
				}
			}
		}
	}

	@Override
	public void updateInven() {
		clearInven();
		int itemCnt = 0;
		int myItemListSize = myItemList.getSize();
		Item[] showItemList =  myItemList.getItemList();
		if (myItemList == null || myItemListSize <= 0 || showItemList == null) return;
		//

		for (int i = 0; i < ivItemList.size(); i++) {
			int resId = 0;
			if (itemCnt < myItemListSize)
				resId = showItemList[itemCnt].getResId();
			ivItemList.get(i).setImageResource(resId);
			itemCnt++;
		}
	}

	public void clearInven() {
		for (ImageView ivItem : ivItemList) ivItem.setImageResource(0);
	}
}
