package com.mine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by USER on 2016-11-09.
 */
public class EffectCanvas extends View {

    ArrayList<String> arrayListStr = new ArrayList<String>();
    ArrayList<Integer> arrayListY = new ArrayList<Integer>();
    ArrayList<Paint> arrayListPaint = new ArrayList<Paint>();

    public void addStr(String str) {
        arrayListStr.add(str);
        arrayListY.add(220);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(26);
        paint.setAlpha(200);
        arrayListPaint.add(paint);
    }

    public EffectCanvas(Context context) {
        super(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < arrayListStr.size(); i++) {
            canvas.drawText(arrayListStr.get(i), 270, arrayListY.get(i), arrayListPaint.get(i));
            arrayListY.set(i, arrayListY.get(i) - 20);
            arrayListPaint.get(i).setAlpha(arrayListPaint.get(i).getAlpha() - 20);
            if(arrayListPaint.get(i).getAlpha() < 30) {
                arrayListStr.remove(i);
                arrayListY.remove(i);
                arrayListPaint.remove(i);
                i--;
            }
        }
    }
}
