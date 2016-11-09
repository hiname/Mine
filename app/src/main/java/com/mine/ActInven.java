package com.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by USER on 2016-11-09.
 */
public class ActInven extends AppCompatActivity {
    final int myInvenResId[] = {
            R.drawable.woodblock,
            R.drawable.stoneblock,
            R.drawable.goldblock,
    };
    DataMgr dataMgr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataMgr = DataMgr.getInstance(this);
        Log.d("d", "onCreate");
        setContentView(R.layout.act_inven);
        updateInven();
        setOnClickListener();
    }

    public void setOnClickListener() {
        LinearLayout llInven = (LinearLayout) findViewById(R.id.llInven);
        int itemCnt = 0;
        for (int i = 0; i < llInven.getChildCount(); i++) {
            LinearLayout llChild = (LinearLayout) llInven.getChildAt(i);
            for (int j = 0; j < llChild.getChildCount(); j++) {
                final int idx = itemCnt;
                ((ImageView) llChild.getChildAt(j)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataMgr.removeMyInvenItem(idx);
                        updateInven();
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
        String itemList = dataMgr.getMyInvenItemPack();
        itemList = itemList.replace("[", "").replace("]", "").replaceAll(", ", "");
        if (itemList.length() > 0)
            loop:
                    for (int i = 0; i < llInven.getChildCount(); i++) {
                        LinearLayout llChild = (LinearLayout) llInven.getChildAt(i);
                        for (int j = 0; j < llChild.getChildCount(); j++) {
                            ImageView ivItem = (ImageView) llChild.getChildAt(j);
                            int getItemIdx = itemList.charAt(itemCnt) - 48;
                            ivItem.setImageResource(myInvenResId[getItemIdx]);
                            itemCnt++;
                            if (itemCnt >= itemList.length())
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
