package tm.mtwModPatcher.sship.lib;

import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.WallsCastle;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.WallsCity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tomek on 14.10.2017.
 */
public class Buildings {

	// #### Markets ####
	public  static final String MarketCity = "market";
	public  static final String MarketCastle = "market_castle";
	public static final List<String> MarketCityLevels = Arrays.asList("corn_exchange", "market", "fairground", "great_market", "merchants_quarter");
	public static final List<String> MarketCastleLevels = Arrays.asList("corn_exchange", "market", "fairground");

	public  static final String RoadCity = "hinterland_roads";
	public  static final String RoadCastle = "hinterland_castle_roads";
	public static final List<String> RoadCityLevels = Arrays.asList("roads", "paved_roads", "highways");
	public static final List<String> RoadCastleLevels = Arrays.asList("c_roads", "c_paved_roads", "c_highways");

	public  static final String PortCity = "port";
	public  static final String PortCastle = "castle_port";
	public static final List<String> PortCityLevels = Arrays.asList("port", "shipwright" ,"dockyard" ,"naval_drydock");
	public static final List<String> PortCastleLevels = Arrays.asList("c_port", "c_shipwright", "c_dockyard", "c_naval_drydock");

	public static final List<String> SeaTradeCityLevels = Arrays.asList("merchants_wharf", "warehouse", "docklands");
	public static final List<String> SeaTradeCastleLevels = Arrays.asList("merchants_wharf");



	public final static tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.WallsCity WallsCity =
			tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.WallsCity;

	public final static tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.WallsCastle WallsCastle =
			tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.WallsCastle;

	public final static String CityType = tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.CityType;
	public final static String CastleType = tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.CastleType;
}