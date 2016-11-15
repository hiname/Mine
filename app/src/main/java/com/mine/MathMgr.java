package com.mine;

/**
 * Created by USER on 2016-11-15.
 */
public class MathMgr {
	public static float roundAndPercent(float value) {
		return (int)(value * 1000f + 0.5f) / 10f;
		// * 1 : 0.031234
		// * 100 : 3.1234
		// * 1000 : 3.1
	}

	public static float roundAndDecimal(float value) {
		return (int)(value * 1000f + 0.5f) / 1000f;
	}
}
