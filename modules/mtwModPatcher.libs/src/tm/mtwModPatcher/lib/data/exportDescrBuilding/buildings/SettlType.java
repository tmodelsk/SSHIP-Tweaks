package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

/** */
public enum SettlType {

	City,
	Castle;

	public static String toLabelString(SettlType settlType) {
		if(settlType == null) return null;

		String castleOrCity;
		switch (settlType) {
			case City:
				castleOrCity = Buildings.CityType;
				break;
			case Castle:
				castleOrCity = Buildings.CastleType;
				break;
			default:
				throw new PatcherLibBaseEx("Unexpected! : "+settlType);
		}

		return castleOrCity;
	}

	public String toLabelString() {
		return toLabelString(this);
	}
}
