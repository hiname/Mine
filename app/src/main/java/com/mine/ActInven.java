package com.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by USER on 2016-11-09.
 */
public class ActInven extends Activity {
    final int myInvenResId[] = {
            R.drawable.woodblock,
            R.drawable.stoneblock,
            R.drawable.goldblock,
            R.drawable.stick,
            R.drawable.hammer,
            R.drawable.maul,
            R.drawable.mace,
            R.drawable.spear,
            R.drawable.morningstar,

    };

    static final String myItemName[] = {
            "목재블럭",
            "석재블럭",
            "황금블럭",
            "막대기",
            "망치",
            "큰망치",
            "메이스",
            "창",
            "모닝스타",
    };

    DataMgr dataMgr;
    static int invenSize = 9;
    Dialog dlg;
    ImageView iv;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("d", "onCreate");
        setContentView(R.layout.act_inven);

        dataMgr = DataMgr.getInstance(this);
        dlg = new Dialog(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        iv = new ImageView(this);
        iv.setImageResource(R.mipmap.ic_launcher);
        btn = new Button(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellItem();
                dlg.dismiss();
            }
        });

        ll.addView(iv);
        ll.addView(btn);
        dlg.setContentView(ll);

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

    public void sellItem() {
        int sellMoney = dataMgr.sellMyItem(lastSelIdx);
        if (sellMoney > 0)
            Toast.makeText(ActInven.this, sellMoney + "원 획득", Toast.LENGTH_SHORT).show();
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
                ((ImageView) llChild.getChildAt(j)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelIdx = selIdx;
                        if (dataMgr.getFastSell()) {
                            sellItem();
                        } else {
                            int getItemId = dataMgr.getMyItemId(selIdx);
                            if (getItemId == -1) return;
                            iv.setImageResource(myInvenResId[getItemId]);
                            btn.setText(myItemName[getItemId] + " 판매");
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
                            ImageView ivItem = (ImageView) llChild.getChildAt(j);

                            int getItemIdx = Integer.parseInt(itemList[itemCnt]);
                            Log.d("d", "getItemIdx : " + getItemIdx);
                            ivItem.setImageResource(myInvenResId[getItemIdx]);
                            itemCnt++;
                            if (itemCnt >= itemList.length)
                                break loop;
                        }
                    }
    }

    public void clearInven() {
        LinearLayout llInven = (LinearLayout) findViewById(R.id.llInven);
        for (int i = 0; i < llInven.getChildCount(); i++) {
            LinearLayout llChild = (LinearLayout) llInven.getChildAt(i);
            for (int j = 0; j < llChild.getChildCount(); j++)
                ((ImageView) llChild.getChildAt(j)).setImageResource(0);
        }
    }

}
