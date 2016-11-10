package com.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActMain extends Activity {
    final static String mineName[] = {
            "나무",
            "돌",
            "금",
    };
    final int locResId[] = {
            R.drawable.loc_wood,
            R.drawable.loc_stone,
            R.drawable.loc_gold
    };
    final int matResId[] = {
            R.drawable.wood,
            R.drawable.stone,
            R.drawable.gold
    };
    ImageView[] ivLoc, ivCombineInven, ivMat;
    TextView[] tvMat;
    Button btnMix;
    ImageView ivMineObj, ivMineWorker;
    boolean isMineHit = false;
    TextView tvIdleMsg, tvMyMoney;
    int gatherIdx = 0;
    Handler mainAnimHandler = new Handler();
    DataMgr dataMgr;
    Button btnStartInven;
    EffectCanvas effectCanvas;
    final static String itemListKey = "itemListKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("d", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        effectCanvas = new EffectCanvas(this);
        ((FrameLayout) findViewById(R.id.rootFl)).addView(effectCanvas);
        btnStartInven = (Button) findViewById(R.id.btnInven);
        btnStartInven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActMain.this, ActInven.class);
//                Log.d("d", "my : " + dataMgr.getMyInvenItemPack());
//                intent.putExtra(itemListKey, dataMgr.getMyInvenItemPack());
                startActivity(intent);
            }
        });
        ivMineObj = (ImageView) findViewById(R.id.ivMineObj);
        ivMineWorker = (ImageView) findViewById(R.id.ivMineWorker);
        tvIdleMsg = (TextView) findViewById(R.id.tvIdleMsg);
        tvMyMoney = (TextView) findViewById(R.id.tvMyMoney);
        //
        ivLoc = new ImageView[]{
                (ImageView) findViewById(R.id.ivLoc1),
                (ImageView) findViewById(R.id.ivLoc2),
                (ImageView) findViewById(R.id.ivLoc3),
        };
        //
        for (int i = 0; i < ivLoc.length; i++) {
            final int idx = i;
            ivLoc[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataMgr.setLocSelectCode(idx);
                    ivMineObj.setImageResource(locResId[idx]);
                }
            });
        }
        //
        ivCombineInven = new ImageView[]{
                (ImageView) findViewById(R.id.ivCombine1),
                (ImageView) findViewById(R.id.ivCombine2),
                (ImageView) findViewById(R.id.ivCombine3),
                (ImageView) findViewById(R.id.ivCombine4),
                (ImageView) findViewById(R.id.ivCombine5),
        };
        for (int i = 0; i < ivCombineInven.length; i++) {
            final int idx = i;
            ivCombineInven[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataMgr.removeCombine(idx);
                    updateCombineInven();
                    updateMatCount();
                }
            });
        }
        //
        ivMat = new ImageView[]{
                (ImageView) findViewById(R.id.ivMaterial1),
                (ImageView) findViewById(R.id.ivMaterial2),
                (ImageView) findViewById(R.id.ivMaterial3),
        };
        //
        tvMat = new TextView[]{
                (TextView) findViewById(R.id.tvMaterial1),
                (TextView) findViewById(R.id.tvMaterial2),
                (TextView) findViewById(R.id.tvMaterial3),
        };
        for (int i = 0; i < tvMat.length; i++) {
            final int idx = i;
            View.OnClickListener lst = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataMgr.addCombine(idx);
                    updateCombineInven();
                    updateMatCount();
                }
            };
            ivMat[i].setOnClickListener(lst);
            tvMat[i].setOnClickListener(lst);
        }
        //
        btnMix = (Button) findViewById(R.id.btnMix);
        btnMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mixResultMsg = dataMgr.tryMix();
                Toast.makeText(ActMain.this, mixResultMsg, Toast.LENGTH_SHORT).show();
                updateCombineInven();
                updateMatCount();
            }
        });
        //
        dataMgr = DataMgr.getInstance(this);
        dataMgr.loadData();
        ivMineObj.setImageResource(locResId[dataMgr.getLocSelectCode()]);
        long lastIdleSecTime = dataMgr.getLastIdleSecTime();
        long lastIdleMineCount = dataMgr.getLastIdleMineCount();
        String lastIdleSecTimeMsg = "혼자일함 : " + lastIdleSecTime + "초";
        String lastIdleMineCountMsg = "채집됨 => " + mineName[dataMgr.getLocSelectCode()] + ":" + lastIdleMineCount + "개";
        Toast.makeText(this, lastIdleSecTimeMsg, Toast.LENGTH_LONG).show();
        Toast.makeText(this, lastIdleMineCountMsg, Toast.LENGTH_LONG).show();
        tvIdleMsg.setText(lastIdleSecTimeMsg + "\n" + lastIdleMineCountMsg);
        updateMyMoney();
        updateCombineInven();
        updateMatCount();
        mainAnimHandler.post(mainAnimRnb);
    }

    int tmpPlayFrameCount = 0;
    Runnable mainAnimRnb = new Runnable() {
        @Override
        public void run() {
            isMineHit ^= true;
            if (isMineHit) {
                ivMineWorker.setImageResource(R.drawable.mineworker);
                String msg = dataMgr.hitMine();
                Log.d("d", msg);
                effectCanvas.addStr(msg);
                // effectCanvas.invalidate();
            } else {
                ivMineWorker.setImageResource(R.drawable.mineworker2);
            }
            updateCombineInven();
            updateMatCount();
            mainAnimHandler.postDelayed(this, hitDelay);
            Log.d("d", "play : " + tmpPlayFrameCount++);
        }
    };
    int hitDelay = 250;

    public void updateCombineInven() {
        for (int i = 0; i < ivCombineInven.length; i++) {
            ivCombineInven[i].setImageResource(0);
            if (i < dataMgr.getCombineInvenItemCount()) {
                ivCombineInven[i].setImageResource(matResId[dataMgr.getCombineInvenItem(i)]);
            }
        }
    }

    public void updateMatCount() {
        // tvStat.setText("wood : " + matAmount[0] + ", stone : " + matAmount[1] + ", gold : " + matAmount[2]);
        for (int i = 0; i < tvMat.length; i++)
            tvMat[i].setText(String.valueOf((int) dataMgr.getMatAmount(i)));
    }

    public void updateMyMoney() {
        tvMyMoney.setText("$" + dataMgr.getMyMoney());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMyMoney();
    }

    @Override
    protected void onStop() {
        Log.d("d", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("d", "onDestroy");
        super.onDestroy();
        dataMgr.saveData();
        mainAnimHandler.removeCallbacks(mainAnimRnb);
    }
}
