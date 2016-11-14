package com.mine;

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
import android.widget.Toast;

/**
 * Created by USER on 2016-11-09.
 */
public class ActInven extends Activity {

    DataMgr dataMgr;
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

        // iv.setImageResource(R.mipmap.ic_launcher);

        btnPopupOpt = new Button(this);
        LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(-1, 0);
        lpBtn.weight = 1;
        btnPopupOpt.setLayoutParams(lpBtn);
        btnPopupOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sellItem();
                useItem();
                dlg.dismiss();
            }
        });

        btnPopupSell = new Button(this);
        btnPopupSell.setLayoutParams(lpBtn);
        btnPopupSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellItem();
                dlg.dismiss();
            }
        });

        llDlgBottom.addView(btnPopupOpt);
        llDlgBottom.addView(btnPopupSell);

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

        updateInven();
        setOnClickListener();

    }

    public void useItem() {
        int useItemId = dataMgr.useMyItem(lastSelIdx);
        if (useItemId != -1) {
            String useItemName = itemInfo.getName(useItemId);
            float useItemFindChance = itemInfo.getFindChance(useItemId);
            String useMsg = useItemName + " 사용. 발견 확률 " + useItemFindChance + " 상승";
            Toast.makeText(ActInven.this, useMsg, Toast.LENGTH_SHORT).show();
            dataMgr.updateSystemMsg(useMsg);
        }
        updateInven();
    }

    public void sellItem() {
        int sellItemId = dataMgr.sellMyItem(lastSelIdx);
        if (sellItemId != -1) {
            String sellItemName = itemInfo.getName(sellItemId);
            int sellItemPrice = itemInfo.getPrice(sellItemId);
            String sellMsg = sellItemName + " 판매 " + sellItemPrice + "원 획득";
            Toast.makeText(ActInven.this, sellMsg, Toast.LENGTH_SHORT).show();
            // ActMain.tvSystemMsg.setText(ActMain.tvSystemMsg.getText().toString() + sellMsg + "\n");
            dataMgr.updateSystemMsg(sellMsg);
        }
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
                                int getItemId = dataMgr.getMyItemId(selIdx);
                                if (getItemId == -1) return;

                                tvDlgTitle.setText(itemInfo.getName(getItemId));
                                // ivPopupIcon.setImageResource(myInvenResId[getItemId]);
                                // btnPopupSell.setText(myItemName[getItemId] + " 판매");
                                ivPopupIcon.setImageResource(itemInfo.getResId(getItemId));
                                btnPopupSell.setText("판매");
                                btnPopupOpt.setVisibility(View.VISIBLE);
                                if (itemInfo.getType(getItemId).equals("무기")) {
                                    btnPopupOpt.setText("장착");
                                } else if (itemInfo.getType(getItemId).equals("음식")) {
                                    btnPopupOpt.setText("먹기");
                                } else {
                                    btnPopupOpt.setVisibility(View.GONE);
                                }

                                dlg.show();
                            }
                        }
                    });
                itemCnt++;
            }
        }
    }

    public void updateInven() {
        clearInven();
        LinearLayout llInven = (LinearLayout) findViewById(R.id.llInven);
        int itemCnt = 0;
        //
        // itemList = itemList.replace("[\"\"]", "").replace("]", "").replaceAll(", ", "");
        String itemList[] = dataMgr.getMyInvenItemPack().split(",");
        Log.d("d", "Arrays.toString(itemList) : " + Arrays.toString(itemList));
        if (itemList != null && itemList.length > 0 && itemList[0].length() > 0)
            loop:
                    for (int i = 0; i < llInven.getChildCount(); i++) {
                        LinearLayout llChild = (LinearLayout) llInven.getChildAt(i);
                        for (int j = 0; j < llChild.getChildCount(); j++) {
                            Object obj = llChild.getChildAt(j);
                            if (obj instanceof ImageView) {
                                ImageView ivItem = (ImageView) obj;
                                int getItemId = Integer.parseInt(itemList[itemCnt]);
                                Log.d("d", "getItemId : " + getItemId);
                                ivItem.setImageResource(itemInfo.getResId(getItemId));
                                itemCnt++;
                                if (itemCnt >= itemList.length)
                                    break loop;
                            }
                        }
                    }
    }

    public void clearInven() {
        LinearLayout llInven = (LinearLayout) findViewById(R.id.llInven);
        for (int i = 0; i < llInven.getChildCount(); i++) {
            LinearLayout llChild = (LinearLayout) llInven.getChildAt(i);
            for (int j = 0; j < llChild.getChildCount(); j++) {
                Object obj = llChild.getChildAt(j);
                if (obj instanceof ImageView) {
                    ((ImageView) llChild.getChildAt(j)).setImageResource(0);
                } else if (obj instanceof TextView) {
                    ((TextView) llChild.getChildAt(j)).setText("");
                }
            }
        }
    }

}
