package com.mine;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final String saveFileName = "pref";
    final String matListKey = "matListKey";
    final String locSelectCodeKey = "locSelectCodeKey";

    final float[] mineCount = {0.5f, 0.2f, 0.1f};
    float[] matMineAmount = new float[mineCount.length];
    int locSelectCode = 0;
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

    ArrayList<Integer> combineResId = new ArrayList<Integer>();

    ImageView ivLoc[];
    ImageView ivCombine[];
    ImageView ivMat[];
    TextView tvMat[];
    Button btnGo;
    ImageView ivMineObj, ivMineWorker;
    boolean isMineHit = false;
    TextView tvStat;
    int gatherIdx = 0;

    Handler mainAnimHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivMineObj = (ImageView) findViewById(R.id.ivMineObj);
        ivMineWorker = (ImageView) findViewById(R.id.ivMineWorker);
        tvStat = (TextView) findViewById(R.id.tvStat);
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
                    locSelectCode = idx;
                    ivMineObj.setImageResource(locResId[idx]);
                }
            });
        }
        //
        ivCombine = new ImageView[]{
                (ImageView) findViewById(R.id.ivCombine1),
                (ImageView) findViewById(R.id.ivCombine2),
                (ImageView) findViewById(R.id.ivCombine3),
                (ImageView) findViewById(R.id.ivCombine4),
                (ImageView) findViewById(R.id.ivCombine5),
        };

        for (int i = 0; i < ivCombine.length; i++){
            final int idx = i;
            ivCombine[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeCombine(idx);
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
                    addCombine(idx);
                }
            };
            ivMat[i].setOnClickListener(lst);
            tvMat[i].setOnClickListener(lst);
        }
        //
        btnGo = (Button) findViewById(R.id.btnGo);
        //
        String getMatListValue = getSharedPreferences(saveFileName, MODE_PRIVATE).getString(matListKey, "0,0,0");
        Log.d("d", "getMatListValue : " + getMatListValue);
        String matList[] = getMatListValue.split(",");
        //
        for (int i = 0; i < matResId.length; i++)
            matMineAmount[i] = Float.parseFloat(matList[i]);
        //
        locSelectCode = getSharedPreferences(saveFileName, MODE_PRIVATE).getInt(locSelectCodeKey, 0);
        ivMineObj.setImageResource(locResId[locSelectCode]);

        printStat();

        mainAnimHandler.post(mainAnimRnb);
    }

    Runnable mainAnimRnb = new Runnable() {
        @Override
        public void run() {
            isMineHit ^= true;
            if (isMineHit) {
                ivMineWorker.setImageResource(R.drawable.mineworker);
                matMineAmount[locSelectCode] += mineCount[locSelectCode];
            } else {
                ivMineWorker.setImageResource(R.drawable.mineworker2);
            }

            printStat();
            mainAnimHandler.postDelayed(this, 1000);
        }
    };

    public void addCombine(int itemId) {
        int combineSize = combineResId.size();
        if (combineSize >= ivCombine.length) {
            Toast.makeText(this, "꽉참", Toast.LENGTH_SHORT).show();
            return;
        }

        if (matMineAmount[itemId] < 1) {
            Toast.makeText(this, "수량 부족", Toast.LENGTH_SHORT).show();
            return;
        }

        matMineAmount[itemId]--;
        combineResId.add(itemId);

        for (int i = 0; i < ivCombine.length; i++)
            ivCombine[i].setImageResource(0);

        for (int i = 0; i < combineResId.size(); i++) {
            ivCombine[i].setImageResource(matResId[combineResId.get(i)]);
        }

        printStat();
    }

    public void removeCombine(int idx) {
        int combineSize = combineResId.size();
        if (combineSize <= 0 || combineSize <= idx) {
            Toast.makeText(this, "없음", Toast.LENGTH_SHORT).show();
            return;
        }

        matMineAmount[combineResId.get(idx)]++;
        combineResId.remove(idx);

        for (int i = 0; i < ivCombine.length; i++)
            ivCombine[i].setImageResource(0);

        for (int i = 0; i < combineResId.size(); i++) {
            ivCombine[i].setImageResource(matResId[combineResId.get(i)]);
        }
        printStat();
    }

    public void printStat() {
        // tvStat.setText("wood : " + matMineAmount[0] + ", stone : " + matMineAmount[1] + ", gold : " + matMineAmount[2]);
        for (int i = 0; i < tvMat.length; i++)
            tvMat[i].setText(String.valueOf((int) matMineAmount[i]));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAnimHandler.removeCallbacks(mainAnimRnb);

        String matList = "";

        for (int i = 0; i < matResId.length; i++) {
            matList += matMineAmount[i] + ",";
        }

        matList = matList.substring(0, matList.length() - 1);

        SharedPreferences.Editor editor = getSharedPreferences(saveFileName, MODE_PRIVATE).edit();
        editor.putString(matListKey, matList);
        editor.putInt(locSelectCodeKey, locSelectCode);
        editor.commit();

        Log.d("d", "matList : " + matList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainAnimHandler.removeCallbacks(mainAnimRnb);
    }
}
