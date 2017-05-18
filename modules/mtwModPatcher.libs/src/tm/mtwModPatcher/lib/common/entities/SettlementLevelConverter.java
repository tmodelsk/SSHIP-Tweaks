package tm.mtwModPatcher.lib.common.entities;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

/**
 * Created by Tomek on 2016-11-12.
 */
public class SettlementLevelConverter {

	public static String toSettlementLevelStr(SettlementLevel level) throws PatcherLibBaseEx {
		String result;

		switch (level) {

			case L1_Village:
				result="village";
				break;
			case L2_Town:
				result="town";
				break;
			case L3_LargeTown:
				result="large_town";
				break;
			case L4_City:
				result="city";
				break;
			case L5_LargeCity:
				result="large_city";
				break;
			case L6_HugeCity:
				result="huge_city";
				break;
			default:
				throw new PatcherLibBaseEx("Not implemented exception !");
		}

		return result;
	}

	public static SettlementLevel parse(String settLevelStr) throws PatcherLibBaseEx {
		SettlementLevel result;

		switch (settLevelStr) {

			case "village":
				result = SettlementLevel.L1_Village;
				break;
			case "town" :
				result = SettlementLevel.L2_Town;
				break;
			case "large_town":
				result = SettlementLevel.L3_LargeTown;
				break;
			case "city":
				result = SettlementLevel.L4_City;
				break;
			case "large_city":
				result = SettlementLevel.L5_LargeCity;
				break;
			case "huge_city":
				result = SettlementLevel.L6_HugeCity;
				break;
			default:
				throw new PatcherLibBaseEx("Not implemented exception !");
		}

		return result;
	}
}
