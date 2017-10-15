package tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;

/**
 * Created by tomek on 12.10.2017.
 */
public enum SettlType {

	City,
	Castle;

	public String toLabelString() {

		SettlType settlType =  this;

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
}
