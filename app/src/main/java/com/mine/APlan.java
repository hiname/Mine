package com.mine;

import android.util.Log;

/**
 * Created by USER on 2016-11-10.
 */
public class APlan {

    /**
     * count를 통한 다량 mix
     *   - 재료 대량 처리
     */
    public void plan() {}

    /**
     * 피로도(체력) 게이지
     *   - 빠르게 채집하되 해당 수치에 따른 제한
     *     (빠르게 채집하면 재료가 너무 많이 생기고(희소성 하락),
     *     느리게 채집하면 쉽게 지루할 수 있다.)
     *
     */
    public void plan2() {}

    /**
     * loc 변경시 일정량 까지 채집 후 정지(또는 허공)
     *
     * 해당 자원 선택시 이동 애니메이션
     *   - 일정 확률로 해당 자원 채집(미발견 확률)
     *   - 히든 자원 발견 확률
     *
     */
    public void plan3() {}

    /**
     * JSON 타입의 x,y 좌표 문자열을 배열로 추출
     *  [{"x":1, "y":2}, {"x":3, "y":4}]
     */
    public int[][] splitJson(String jsonStr) {
        // String str = "[{\"x\":1, \"y\":2}, {\"x\":3, \"y\":4}, {\"x\":5, \"y\":6}, {\"x\":7, \"y\":8}]";
        jsonStr = jsonStr.replaceAll("\"", "");
        Log.d("d", "str : " + jsonStr);
        String[] s = jsonStr.split("\\}\\,");
        int[][] xy = new int[s.length][2];

        for (int i = 0; i < s.length; i++){
            String ss[] = s[i].replaceAll("[\\[\\]\\{\\}]", "").split(",");

            xy[i][0] = Integer.parseInt(ss[0].split(":")[1]);
            xy[i][1] = Integer.parseInt(ss[1].split(":")[1]);
            // Log.d("d", "s[" + i + "] : " + s[i]);
        }
        return xy;

    }

}
