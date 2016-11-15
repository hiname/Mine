package com.mine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by USER on 2016-11-09.
 */
public class EffectCanvas extends View {

    ArrayList<String> arrayListStr = new ArrayList<String>();
    ArrayList<Integer> arrayListY = new ArrayList<Integer>();
    ArrayList<Paint> arrayListPaint = new ArrayList<Paint>();
    boolean isEffecting = false;
    int hitX = 100;

    public void addStr(String str) {
        arrayListStr.add(str);
        arrayListY.add(220);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(36);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        arrayListPaint.add(paint);
        if (!isEffecting) {
            Log.d("d", "addInvali");
            invalidate();}
        //
    }

    public EffectCanvas(Context context) {
        super(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        isEffecting = true;
        for (int i = 0; i < arrayListStr.size(); i++) {
            arrayListY.set(i, arrayListY.get(i) - 3);
            arrayListPaint.get(i).setAlpha(arrayListPaint.get(i).getAlpha() - 16);
            canvas.drawText(arrayListStr.get(i), hitX, arrayListY.get(i), arrayListPaint.get(i));
            if(arrayListPaint.get(i).getAlpha() < 16) {
                arrayListStr.remove(i);
                arrayListY.remove(i);
                arrayListPaint.remove(i);
                i--;
            }

        }

        if (arrayListStr.size() > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            }, 40);
        else
            isEffecting = false;
    }
}
