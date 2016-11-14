package com.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActMain extends Activity implements DataUpdate{
    //

    ImageView[] ivLoc, ivCombineInven, ivMat;
    TextView[] tvMat;
    Button btnMix;
    ImageView ivMineObj, ivMineWorker;
    boolean isMineHit = false;
    int hitFrame = 0;
    TextView tvIdleMsg, tvMyMoney;
    int gatherIdx = 0;
    Handler mainAnimHandler = new Handler();
    DataMgr dataMgr;
    Button btnOpenInven;
    EffectCanvas hitEffectCanvas;

    ArrayAdapter<String> systemMsgAdapter;

    ImageView ivEquipItem, ivLunchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("d", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        ivEquipItem = (ImageView) findViewById(R.id.ivEquipItem);
        ivLunchItem = (ImageView) findViewById(R.id.ivLunchItem);

        hitEffectCanvas = new EffectCanvas(this);
        ((FrameLayout) findViewById(R.id.rootFl)).addView(hitEffectCanvas);
        btnOpenInven = (Button) findViewById(R.id.btnInven);

        btnOpenInven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActMain.this, ActInven.class);
                startActivity(intent);
            }
        });
        ivMineObj = (ImageView) findViewById(R.id.ivMineObj);
        ivMineWorker = (ImageView) findViewById(R.id.ivMineWorker);
        tvIdleMsg = (TextView) findViewById(R.id.tvIdleMsg);
        tvMyMoney = (TextView) findViewById(R.id.tvMyMoney);

        ListView lvSystemMsg = (ListView) findViewById(R.id.lvSystemMsg);

        systemMsgAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item);
        lvSystemMsg.setAdapter(systemMsgAdapter);
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
                    ivMineObj.setImageResource(MineInfo.locResId[idx]);
                    ((LinearLayout) findViewById(R.id.llbg)).setBackgroundResource(MineInfo.locBgResId[idx]);
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
                (ImageView) findViewById(R.id.ivMaterial4),
                (ImageView) findViewById(R.id.ivMaterial5),
                (ImageView) findViewById(R.id.ivMaterial6),
        };
        //
        tvMat = new TextView[]{
                (TextView) findViewById(R.id.tvMaterial1),
                (TextView) findViewById(R.id.tvMaterial2),
                (TextView) findViewById(R.id.tvMaterial3),
                (TextView) findViewById(R.id.tvMaterial4),
                (TextView) findViewById(R.id.tvMaterial5),
                (TextView) findViewById(R.id.tvMaterial6),
        };
        for (int i = 0; i < tvMat.length; i++) {
            final int idx = i;
            View.OnClickListener lst = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataMgr.addCombine(idx);
                    if (dataMgr.getCombineInvenItemCount() == 5 && dataMgr.getFastMix()) {
                        for (ImageView iv : ivMat) {
                            iv.setClickable(false);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnMix.performClick();
                                for (ImageView iv : ivMat) {
                                    iv.setClickable(true);
                                }
                            }
                        }, 200);

                    }
                    updateCombineInven();
                    updateMatCount();
                }
            };
            ivMat[i].setOnClickListener(lst);

        }
        //
        btnMix = (Button) findViewById(R.id.btnMix);
        Log.d("d", "btnMix : " + btnMix);
        btnMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mixItemName = dataMgr.tryMixGetItemName();
                String formulaMsg = "";
                if (mixItemName.equals(DataMgr.resultCode_formulaNotFound)){
                    formulaMsg = "조합식 없음";
                } else if (mixItemName.equals(DataMgr.resultCode_myInvenFull)){
                    formulaMsg = "인벤 가득참";
                }  else {
                    formulaMsg = mixItemName + " 조합 완료";
                }

                Toast.makeText(ActMain.this, formulaMsg, Toast.LENGTH_SHORT).show();
                Log.d("d", "mixItemName : " + mixItemName);
                Log.d("d", "formulaMsg : " + formulaMsg);

                updateCombineInven();
                updateMatCount();
            }
        });
        //
        dataMgr = DataMgr.getInstance(this);
        dataMgr.loadData();
        ivMineObj.setImageResource(MineInfo.locResId[dataMgr.getLocSelectCode()]);
        ((LinearLayout) findViewById(R.id.llbg)).setBackgroundResource(MineInfo.locBgResId[dataMgr.getLocSelectCode()]);
        long lastIdleSecTime = dataMgr.getLastIdleSecTime();
        long lastIdleMineCount = dataMgr.getLastIdleMineCount();
        String lastIdleSecTimeMsg = "혼자일함 : " + lastIdleSecTime + "초";
        String lastIdleMineCountMsg = "채집됨 => " + MineInfo.mineName[dataMgr.getLocSelectCode()] + ":" + lastIdleMineCount + "개";
        Toast.makeText(this, lastIdleSecTimeMsg, Toast.LENGTH_LONG).show();
        Toast.makeText(this, lastIdleMineCountMsg, Toast.LENGTH_LONG).show();
        tvIdleMsg.setText(lastIdleSecTimeMsg + "\n" + lastIdleMineCountMsg);
        updateMyMoney();
        updateCombineInven();
        updateMatCount();
        shake = AnimationUtils.loadAnimation(ActMain.this, R.anim.shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShakeAnim = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mainAnimHandler.post(mainAnimRnb);
        dataMgr.addUpdateObject(this);

        CheckBox cbFastMix = (CheckBox) findViewById(R.id.cbFastMix);
        cbFastMix.setChecked(dataMgr.getFastMix());
        cbFastMix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dataMgr.setFastMix(isChecked);
            }
        });
    }

    boolean isShakeAnim = false;
    Animation shake;

    int[] hitFrameResId = {
            R.drawable.mineworker,
            R.drawable.mineworker1_2,
            R.drawable.mineworker1_3,
            R.drawable.mineworker2,
            R.drawable.mineworker2,
    };

    int hitFrameLen = hitFrameResId.length;

    Runnable mainAnimRnb = new Runnable() {
        @Override
        public void run() {
            isMineHit ^= true;
            hitFrame++;

            if (hitFrame < (hitFrameLen - 1)) {
                ivMineWorker.setImageResource(hitFrameResId[hitFrame]);
            } else {
                ivMineWorker.setImageResource(hitFrameResId[hitFrame]);
                hitFrame = 0;
                String hitMsg = dataMgr.hitMine();
                hitEffectCanvas.addStr(hitMsg);

                if (hitMsg.contains("+")) {
                    // ActMain.tvSystemMsg.setText(ActMain.tvSystemMsg.getText().toString() + hitMsg + "\n");
                    systemMsgAdapter.add(hitMsg);
                    systemMsgAdapter.notifyDataSetChanged();

                }

                if (!isShakeAnim) {
                    ivMineObj.startAnimation(shake);
                    isShakeAnim = true;
                }
            }

            updateCombineInven();
            updateMatCount();
            mainAnimHandler.postDelayed(this, hitDelay);
        }
    };

    int hitDelay = 120;

    public void updateCombineInven() {
        for (int i = 0; i < ivCombineInven.length; i++) {
            ivCombineInven[i].setImageResource(0);
            if (i < dataMgr.getCombineInvenItemCount()) {
                ivCombineInven[i].setImageResource(MineInfo.matResId[dataMgr.getCombineInvenItem(i)]);
            }
        }
    }

    public void updateMatCount() {

        for (int i = 0; i < ivMat.length; i++) {
            ivMat[i].setVisibility(View.INVISIBLE);
            tvMat[i].setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < tvMat.length; i++) {
            int amuount = (int) dataMgr.getMatAmount(i);
            if (amuount > 0) {
                tvMat[i].setText(String.valueOf(amuount));

                if (ivMat[i].getVisibility() == View.INVISIBLE) {
                    ivMat[i].setVisibility(View.VISIBLE);
                    tvMat[i].setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void updateMyMoney() {
        Log.d("d", "updateMyMoney : " + dataMgr.getMyMoney());
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

    @Override
    public void addSystemMsg(String msg) {
        systemMsgAdapter.add(msg);
        systemMsgAdapter.notifyDataSetChanged();
    }

    int equipItemId = -1;
    int lunchItemId = -1;

    @Override
    public void equipItem(int id) {
        if (equipItemId != -1) {
            releaseItem(equipItemId);
        }
        equipItemId = id;
        ivEquipItem.setImageResource(id);
    }

    @Override
    public String releaseItem(int id) {
        if (id != -1) {
            String code = dataMgr.tryAddMyItem(id);
            if (code.equals(DataMgr.resultCode_myInvenFull)) {
                Toast.makeText(this, "인벤 가득참", Toast.LENGTH_LONG).show();
                return DataMgr.resultCode_myInvenFull;
            } else if (code.equals(DataMgr.resultCode_myItemAddOK)) {
                equipItemId = -1;
                ivEquipItem.setImageResource(0);
                return DataMgr.resultCode_myItemAddOK;
            }
        }
        return null;
    }
}
