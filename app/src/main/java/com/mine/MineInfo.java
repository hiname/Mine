package com.mine;

/**
 * Created by USER on 2016-11-14.
 */
public class MineInfo {

    static final String mineName[] = {
            "나무",
            "돌",
            "닭",
            "사과",
            "황금",
            "돼지",
    };
    //
    static final int locResId[] = {
            R.drawable.loc_wood,
            R.drawable.loc_stone,
            R.drawable.loc_hunt,
    };

    static final int locBgResId[] = {
            R.drawable.wood_bg,
            R.drawable.stone_bg,
            R.drawable.hunt_bg,
    };

    //
    static final int matResId[] = {
            R.drawable.wood,
            R.drawable.stone,
            R.drawable.chicken,
            R.drawable.apple,
            R.drawable.gold,
            R.drawable.pig,
    };

    public class Mine {
        private int resId;
        private String name;

    }

    private MineInfo(){}

    private static class Singleton {
        private static MineInfo instance = new MineInfo();
    }

    public static MineInfo getInstance() {
        return Singleton.instance;
    }


}
