package tm.mtwModPatcher.sship.lib;

import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingTree;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;

import java.util.Arrays;
import java.util.List;

/** SSHIP Buildings */
public class Buildings {

	public static final BuildingTree CouncilCity = new BuildingTree("city_hall", SettlType.City, "town_hall council_chambers city_hall mayors_palace");

	// ### Guilds ###
	public static final String MerchantGuild = "guild_merchants_guild";
	public static final List<String> MerchantGuildLevels = Arrays.asList("merchants_guild", "m_merchants_guild", "gm_merchants_guild");

	public static final String ExplorersGuild = "guild_explorers_guild";
	public static final List<String> ExplorersLevels = Arrays.asList("explorers_guild", "m_explorers_guild", "gm_explorers_guild");

	// #### Markets ####
	public static final String MarketCity = "market";
	public static final String MarketCastle = "market_castle";
	public static final List<String> MarketCityLevels = Arrays.asList("corn_exchange", "market", "fairground", "great_market", "merchants_quarter");
	public static final List<String> MarketCastleLevels = Arrays.asList("corn_exchange", "market", "fairground");

	public static final BuildingTree MarketCityTree = new BuildingTree(MarketCity, MarketCityLevels, SettlType.City);
	public static final BuildingTree MarketCastleTree = new BuildingTree(MarketCastle, MarketCastleLevels, SettlType.Castle);

	public  static final String RoadCity = "hinterland_roads";
	public  static final String RoadCastle = "hinterland_castle_roads";
	public static final List<String> RoadCityLevels = Arrays.asList("roads", "paved_roads", "highways");
	public static final List<String> RoadCastleLevels = Arrays.asList("c_roads", "c_paved_roads", "c_highways");

	public static final String RiverAccess = "river_access";
	public static final String RiverAccessLevel = "river_port";

	public  static final String PortCity = "port";
	public static final List<String> PortCityLevels = Arrays.asList("port", "shipwright" ,"dockyard" ,"naval_drydock");
	public  static final String PortCastle = "castle_port";
	public static final List<String> PortCastleLevels = Arrays.asList("c_port", "c_shipwright", "c_dockyard", "c_naval_drydock");

	public  static final String SeaTradeCity = "sea_trade";
	public  static final String SeaTradeCastle = "castle_sea_trade";
	public static final List<String> SeaTradeCityLevels = Arrays.asList("merchants_wharf", "warehouse", "docklands");
	public static final List<String> SeaTradeCastleLevels = Arrays.asList("merchants_wharf");

	private static final String TavernCity1 = "taverns";
	private static final List<String> TavernCityLevels1 = Arrays.asList("brothel", "inn", "tavern", "coaching_house", "pleasure_palace");
	public static final BuildingTree TavernCity = new BuildingTree(TavernCity1, TavernCityLevels1, SettlType.City);

	public static final BuildingTree TavernCastle = new BuildingTree("castle_taverns", Arrays.asList("c_brothel"), SettlType.Castle);


	public static final String StoneMason = "stonemason";
	public static final List<String> StoneMasonLevels = Arrays.asList("sitehut", "stonemason");

	public static final String LoggingCamp = "logging_camp";
	public static final List<String> LoggingCampLevels = Arrays.asList("logging_camp", "carpenter");

	public static final String SmithCity = "smith";
	public static final List<String> SmithCityLevels = Arrays.asList("leather_tanner", "blacksmith", "armourer", "heavy_armourer", "plate_armourer", "gothic_armourer");
	public static final String SmithCastle = "castle_smith";
	public static final List<String> SmithCastleLevels = Arrays.asList("c_leather_tanner", "c_blacksmith", "c_armourer", "c_heavy_armourer", "c_plate_armourer", "c_gothic_armourer");

	public static final String BakeryCity = "bakery";
	public static final List<String> BakeryCityLevels = Arrays.asList("bakery", "pastry_shop");
	public static final String BakeryCastle = "castle_bakery";
	public static final String BakeryCastleLevel = "c_bakery";

	public static final BuildingTree TempleMuslimCity = new BuildingTree("temple_muslim", SettlType.City,"small_masjid masjid minareted_masjid jama great_jama");
	public static final BuildingTree TempleMuslimCastle = new BuildingTree("temple_muslim_castle", SettlType.Castle,"c_small_masjid c_masjid");

	public static final String TempleCatholicCity = "temple_catholic";
	public static final BuildingTree TempleCathCity = new BuildingTree(TempleCatholicCity, Arrays.asList("small_church", "church", "abbey", "cathedral", "huge_cathedral"), SettlType.City);

	public static final String TempleCatholicCastle = "temple_catholic_castle";
	public static final List<String> TempleCatholicCastleLevels = Arrays.asList("small_chapel", "chapel");
	public static final BuildingTree TempleCathCastle = new BuildingTree(TempleCatholicCastle, TempleCatholicCastleLevels, SettlType.Castle);

	public static final BuildingTree TempleOrthodoxCity = new BuildingTree("temple_orthodox" , SettlType.City, "small_church_o church_o abbey_o cathedral_o huge_cathedral_o");
	public static final BuildingTree TempleOrthodoxCastle = new BuildingTree("temple_orthodox_castle" , SettlType.Castle, "small_chapel_o chapel_o");

	public static final String MonasteryCatholicCity = "friar";
	public static final List<String> MonasteryCatholicCityLevels = Arrays.asList("monastery", "medical_care", "hospital");
	public static final String MonasteryCatholicCastle = "monastery_castle";
	public static final List<String> MonasteryCatholicCastleLevels = Arrays.asList("castle_monastery");

	public static final String Farms = "farms";
	public static final List<String> FarmsLevels = Arrays.asList("farms","farms+1","farms+2","farms+3","farms+4");

	public static final String WaterSupply = "water_supply";
	public static final List<String> WaterSupplyLevels = Arrays.asList("well", "water_conductor", "aqueduct");

	public static final String Health = "health";
	public static final List<String> HealthLevels = Arrays.asList("bath_house", "public_baths");

	public static final String MinesCastle = "castle_mines";
	public static final List<String> MinesCastleLevels = Arrays.asList("c_mines", "c_mines+1");

	public static final String BarracksCity = "barracks";
	public static final List<String> BarracksCityLevels = Arrays.asList("town_watch", "town_guard", "city_watch", "militia_drill_square", "militia_barracks", "army_barracks", "royal_armoury");

	public static final String BarracksCastle = "castle_barracks";
	public static final List<String> BarracksCastleLevels = Arrays.asList("mustering_hall", "garrison_quarters", "drill_square", "barracks", "armoury");
	public static final  BuildingTree BarracksCastleTree = new BuildingTree(BarracksCastle, BarracksCastleLevels, SettlType.Castle);

	public static final String MissileCastle = "missiles";
	public static final List<String> MissileCastleLevels = Arrays.asList("bowyer", "practice_range", "archery_range", "marksmans_range");
	public static final BuildingTree MissleCastleTree = new BuildingTree(MissileCastle, MissileCastleLevels, SettlType.Castle);

	public static final String StablesCastleSymbol = "equestrian";
	public static final List<String> StablesCastleLevels = Arrays.asList("stables", "knights_stables", "barons_stables", "earls_stables", "kings_stables");

	public static final BuildingTree StableCastle = new BuildingTree(StablesCastleSymbol, StablesCastleLevels , SettlType.Castle);


	public static final String ItalianTraders = "Italian_Traders";
	public static final List<String> ItalianTradersLevels = Arrays.asList("italian_trader_quarters", "italian_trader_headquarters");


	public static final String HospitallersCity = "st_johns_chapter_house";
	public static final List<String> HospitallersCityLevels = Arrays.asList("st_johns_minor_ch", "st_johns_major_ch");
	public static final String HospitallersCastle = "st_johns_chapter_house_castle";
	public static final List<String> HospitallersCastleLevels = Arrays.asList("st_johns_minor_ch", "st_johns_major_ch");







	public final static tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.WallsCity WallsCitySpec =
			tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.WallsCity;

	public final static tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.WallsCastle WallsCastleSpec =
			tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.WallsCastle;

	public static final List<String> WallsCityLevels = Arrays.asList(WallsCitySpec.L1_WoodenPalisade, WallsCitySpec.L2_WoodenWall, WallsCitySpec.L3_StoneWall, WallsCitySpec.L4_LargeStoneWall, WallsCitySpec.L5_HugeStoneWall);
	public static final List<String> WallsCastleLevels = Arrays.asList(WallsCastleSpec.L1_MotteAndBailey, WallsCastleSpec.L2_WoodenCastle, WallsCastleSpec.L3_Castle, WallsCastleSpec.L4_Fortress, WallsCastleSpec.L5_Citadel);

	public static final BuildingTree WallsCity = new BuildingTree(WallsCitySpec.Name, WallsCityLevels, SettlType.City);
	public static final BuildingTree WallsCastle = new BuildingTree(WallsCastleSpec.Name, WallsCastleLevels, SettlType.Castle);

	public final static String CityType = tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.CityType;
	public final static String CastleType = tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.Buildings.CastleType;
}
