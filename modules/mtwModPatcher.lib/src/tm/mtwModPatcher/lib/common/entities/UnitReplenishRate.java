package tm.mtwModPatcher.lib.common.entities;

import tm.mtwModPatcher.lib.fileEntities.data.common.Format;

/**
 * Created by Tomek on 2016-05-12.
 */
public class UnitReplenishRate {

	public static final double R1 = 1.0;

	public static final double R2 = 0.5;
	public static final double R2a = 0.75;

	public static final double R3 = 0.34;
	public static final double R4 = 0.25;
	public static final double R5 = 0.2;
	public static final double R6 = 0.17;

	public static final double R7 = 0.15;

	public static final double R8 = 0.13;

	public static final double R9 = 0.12;
	public static final double R10 = 0.101;
	//public static double R11 = 1.0;
	public static final double R12 = 0.085;

	public static final double R13 = 0.08;
	public static final double R14 = 0.072;
	public static final double R15 = 0.067;

	public static double getRate(int turns) {
		double rate = 1.0 / ((double)turns);

		rate = Format.round(rate, 3);

		return rate;
	}


}
