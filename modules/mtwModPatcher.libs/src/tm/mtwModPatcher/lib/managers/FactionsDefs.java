package tm.mtwModPatcher.lib.managers;

import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.CultureType;
import tm.mtwModPatcher.lib.common.entities.EducationStyle;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.common.entities.Religion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FactionsDefs {

	public static FactionInfo loadFactionInfo(String factionName) throws PatcherLibBaseEx {
		List<FactionInfo> factions = _FactionInfos.stream().filter( fi -> fi.symbol.equals(factionName)).collect(Collectors.toList());

		if(factions.size() > 1) throw new PatcherLibBaseEx("Faction Symbol not unique");

		if(factions.size() == 0) throw new PatcherLibBaseEx("Faction "+ factionName +"not found");

		return factions.get(0);
	}
	public static List<FactionInfo> getFactionInfos() {
		return _FactionInfos;
	}

	public static int getFactionAiEcId(String factionName) throws PatcherLibBaseEx {

		int id = -1;

		switch (factionName) {

			case "slave" :
				id = 1;
				break;

			case "venice" :
				id = 2;
				break;

			case "sicily" :
				id = 3;
				break;

			case "milan" :
				id = 4;
				break;

			case "papal_states" :
				id = 5;
				break;

			case "denmark" :
				id = 6;
				break;

			case "egypt" :
				id = 7;
				break;

			case "scotland" :
				id = 8;
				break;

			case "cumans" :
				id = 9;
				break;

			case "mongols" :
				id = 10;
				break;

			case "turks" :
				id = 11;
				break;

			case "france" :
				id = 12;
				break;

			case "hre" :
				id = 13;
				break;

			case "england" :
				id = 14;
				break;

			case "portugal" :
				id = 15;
				break;

			case "poland" :
				id = 16;
				break;

			case "byzantium" :
				id = 17;
				break;

			case "moors" :
				id = 18;
				break;

			case "russia" :
				id = 19;
				break;

			case "spain" :
				id = 20;
				break;

			case "hungary" :
				id = 21;
				break;

			case "aragon" :
				id = 22;
				break;

			case "lithuania" :
				id = 23;
				break;

			case "kievan_rus" :
				id = 24;
				break;

			case "teutonic_order" :
				id = 25;
				break;

			case "timurids" :
				id = 26;
				break;

			case "norway" :
				id = 27;
				break;

			case "jerusalem" :
				id = 28;
				break;

			case "kwarezm" :
				id = 29;
				break;

			case "pisa" :
				id = 30;
				break;

			case "rum" :
				id = 31;
				break;

			default:
				throw new PatcherLibBaseEx("Faction name '"+factionName+"' not recongized !");
		}

		return id;
	}

	protected static final List<String> _ChristianFactions = Arrays.asList(
			"denmark",
			"jerusalem",
			"norway",
			"hre",
			"scotland",
			"france",
			"england",
			"hungary",
			"poland",
			"venice",
			"papal_states",
			"portugal",
			"spain",
			"aragon",
			"sicily",
			"pisa");	// 16

	public static final FactionInfo NORWAY = new FactionInfo("norway","Norway", CultureType.NORTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "Royal Hirdsmen" );
	public static final FactionInfo SCOTLAND = new FactionInfo("scotland","Scotland", CultureType.NORTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" );
	public static final FactionInfo VENICE = new FactionInfo("venice","Venice", CultureType.SOUTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "SE Bodyguard" );
	public static final FactionInfo ARAGON = new FactionInfo("aragon","Aragon", CultureType.SOUTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "SE Bodyguard" );
	public static final FactionInfo PISA = new FactionInfo("pisa","Pisa", CultureType.SOUTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "SE Bodyguard" );
	public static final FactionInfo NOVOGROD = new FactionInfo("russia","NOVGOROD", CultureType.EASTERN_EUROPEAN, Religion.Orthodox , EducationStyle.Western , "EE Bodyguard" ); // ???? Culture = Western ???
	public static final FactionInfo CUMANS = new FactionInfo("cumans","CUMANS", CultureType.EASTERN_EUROPEAN, Religion.Pagan, EducationStyle.Pagan , "Cuman Bodyguard" );

	private static final List<FactionInfo> _FactionInfos = Arrays.asList(
			new FactionInfo("denmark","Denmark", CultureType.NORTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" ),
			new FactionInfo("jerusalem","Crusader States", CultureType.NORTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" ),
			NORWAY,
			new FactionInfo("hre","Holy Roman Emprire - Germans", CultureType.NORTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" ),
			SCOTLAND,
			new FactionInfo("france","France", CultureType.NORTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" ),
			new FactionInfo("england","England", CultureType.NORTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" ),
			new FactionInfo("hungary","Hungary",CultureType.EASTERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" ),
			new FactionInfo("poland","Poland", CultureType.EASTERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "NE Bodyguard" ),
			VENICE,
			new FactionInfo("papal_states","Papal States - Pontifex Maximus", CultureType.SOUTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "SE Bodyguard" ),
			new FactionInfo("portugal","Portugal", CultureType.SOUTHERN_EUROPEAN, Religion.Catholic, EducationStyle.Western , "SE Bodyguard" ),
			new FactionInfo("spain","Spain", CultureType.SOUTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "SE Bodyguard" ),
			ARAGON,
			new FactionInfo("sicily","Sicily", CultureType.SOUTHERN_EUROPEAN, Religion.Catholic , EducationStyle.Western , "SE Bodyguard" ),
			PISA,

			new FactionInfo("egypt","Egypt", CultureType.MIDDLE_EASTERN, Religion.Islam , EducationStyle.Eastern , "ME Bodyguard" ),
			new FactionInfo("milan","ABBASSIDS", CultureType.MIDDLE_EASTERN, Religion.Islam , EducationStyle.Eastern , "ME Bodyguard" ),
			new FactionInfo("moors","Moors - ALMORAVID EMPIRE", CultureType.MIDDLE_EASTERN, Religion.Islam  , EducationStyle.Eastern, "ME Bodyguard" ),
			new FactionInfo("turks","GREAT SELJUQ EMPIRE - far east", CultureType.MIDDLE_EASTERN, Religion.Islam  , EducationStyle.Eastern, "ME Bodyguard" ),
			new FactionInfo("rum","SULTANATE OF RUM - Seljuq Turks - Asia Minor", CultureType.MIDDLE_EASTERN, Religion.Islam , EducationStyle.Eastern , "ME Bodyguard" ),
			new FactionInfo("kwarezm","ZENGIDS", CultureType.MIDDLE_EASTERN, Religion.Islam , EducationStyle.Eastern , "ME Bodyguard" ),

			NOVOGROD,
			new FactionInfo("kievan_rus","KIEVAN RUS", CultureType.EASTERN_EUROPEAN, Religion.Orthodox , EducationStyle.Western , "EE Bodyguard" ),  // ???? Culture = Western ???
			new FactionInfo("byzantium","BYZANTINE EMPIRE", CultureType.GREEK, Religion.Orthodox , EducationStyle.Greek , "Greek Bodyguard" ),
			new FactionInfo("timurids","GEORGIA", CultureType.GREEK, Religion.Orthodox , EducationStyle.Greek , "bodyguard georgia" ),
			new FactionInfo("teutonic_order","SERBIA", CultureType.GREEK, Religion.Orthodox , EducationStyle.Western , "EE Bodyguard" ),  // ???? Culture = Western ???

			CUMANS,
			new FactionInfo("lithuania","LITHUANIA", CultureType.EASTERN_EUROPEAN, Religion.Pagan, EducationStyle.Pagan , "Lith Bodyguard" ),
			new FactionInfo("mongols","Mongols", CultureType.MIDDLE_EASTERN, Religion.Pagan, EducationStyle.Pagan , "Keshikten Bodyguard" ),
			new FactionInfo("slave", "Rebels", CultureType.SOUTHERN_EUROPEAN, Religion.Pagan , EducationStyle.None , null )
			);

	public static ListUnique<String> csvToSet(String firstCsv) {
		val res = new ArrayUniqueList<String>();

		String[] factionTab = firstCsv.split(",");
		for (String factionSymbol : factionTab) {
			factionSymbol = factionSymbol.trim();
			if(!factionSymbol.isEmpty())
				res.addUnique(factionSymbol);
		}

		return res;
	}

	public static ListUnique<String> resolveFactions(String factionsCsv) {
		val factionsWithGroups = csvToSet(factionsCsv);
		val res = resolveFactions(factionsWithGroups);
		return res;
	}
	public static ListUnique<String> resolveFactions(ListUnique<String> factionsGroups) {
		val res = new ArrayUniqueList<String>();

		for (val symbol : factionsGroups) {

			CultureType cultureType = null;
			try { cultureType = Enum.valueOf(CultureType.class, symbol.toUpperCase()); }
			catch (IllegalArgumentException iaEx) {}

			if(cultureType == null) res.add(symbol);
			else {
				CultureType finalCultureType = cultureType;
				val factions = _FactionInfos.stream().filter(fi -> fi.culture.equals(finalCultureType) ).collect(Collectors.toList());

				factions.forEach( f -> res.add(f.symbol) );
			}
		}

		return res;
	}

	protected static final List<String> _IslamFactions = Arrays.asList(
			"egypt",
			"milan",
			"moors",
			"turks",
			"rum",
			"kwarezm");		// 6

	protected static final List<String> _OrthodoxFactions = Arrays.asList(
			"russia",
			"kievan_rus",
			"byzantium",
			"timurids",
			"teutonic_order");	// 5

	protected static final List<String> _TuranianFactions = Arrays.asList(
			"cumans",
			"lithuania");		// 2

	protected static final List<String> _OtherFactions = Arrays.asList(
			"mongols",
			"slave");			// 2

	public static final String Rebels = "slave";

	public static List<String> allFactionsList() {

		List<String> all = new ArrayList<>();

		all.addAll(_ChristianFactions);
		all.addAll(_IslamFactions);
		all.addAll(_OrthodoxFactions);
		all.addAll(_TuranianFactions);
		all.addAll(_OtherFactions);

		return all;
	}

	public static List<String> allFactionsExceptRebelsList() {
		List<String> all = allFactionsList();

		all.remove("slave");

		return  all;
	}

	public static List<String> catholicFactionsList() {
		List<String> list = new ArrayList<>();

		list.addAll(_ChristianFactions);

		return list;
	}

	public static List<String> orthodoxFactionsList() {

		List<String> list = new ArrayList<>();

		list.addAll(_OrthodoxFactions);

		return list;
	}

	public static List<String> islamFactionsList() {
		List<String> list = new ArrayList<>();

		list.addAll(_IslamFactions);

		return list;
	}

	public static boolean isAllIslam(String factionsStr) {

		String factionsDefinitionCsv = islamFactionsCsv();
		return isAllFirstCsvFactorsInSecondCsv(factionsStr, factionsDefinitionCsv);
//		String[] factionTab = factionsStr.split(",");
//
//		boolean isOk = true;
//		for (String factionStr : factionTab) {
//
//			factionStr = factionStr.trim();
//			if(!factionStr.isEmpty()) {
//				int index = factionsDefinitionCsv.indexOf(factionStr);
//				if (index  < 0) {
//					isOk = false;
//					break;
//				}
//			}
//		}
//
//		return isOk;
	}

	public static boolean isAllFirstCsvFactorsInSecondCsv(String firstCsv, String factionsDefinitionCsv) {
		String[] factionTab = firstCsv.split(",");

		boolean isOk = true;
		for (String factionStr : factionTab) {

			factionStr = factionStr.trim();
			if(!factionStr.isEmpty()) {
				int index = factionsDefinitionCsv.indexOf(factionStr);
				if (index  < 0) {
					isOk = false;
					break;
				}
			}
		}

		return isOk;
	}

	public static boolean isAnyFirstCsvFactorsInSecondCsv(String firstCsv, String factionsDefinitionCsv) {

		String[] factionTab = firstCsv.split(",");

		boolean isOk = false;
		for (String factionStr : factionTab) {

			factionStr = factionStr.trim();
			if(!factionStr.isEmpty()) {
				int index = factionsDefinitionCsv.indexOf(factionStr);
				if (index  >= 0) {
					isOk = true;
					break;
				}
			}
		}

		return isOk;
	}

	public static boolean isNotAnyFirstCsvFactorsInSecondCsv(String firstCsv, String factionsDefinitionCsv) {
		return ! isAnyFirstCsvFactorsInSecondCsv(firstCsv, factionsDefinitionCsv);
	}

	public static String christianFactionsCsv() {
		if(_ChristianFactionsCsv == null) {
			String str="";

			for (String faction : _ChristianFactions)
				str += faction + ", ";

			_ChristianFactionsCsv = str;
			//str = "denmark, jerusalem, norway,  hre, scotland, france, england, hungary, poland, venice, papal_states, portugal, spain, aragon, sicily, pisa,";
		}
		return _ChristianFactionsCsv;
	}

	public static String ortodoxFactionsCsv() {

		String str="";

		str = "russia, kievan_rus, byzantium, timurids, teutonic_order,";

		return str;
	}

	public static String islamFactionsCsv() {
		String str="";

		str = "egypt, milan, moors, turks, rum, kwarezm,";

		return str;
	}
	public static ListUnique<String> islamFactionsSet() {
		return csvToSet(islamFactionsCsv());
	}

	public static String turanianFactionsCsv() {

		// cumans, lithuania,

		String str="";

		str = "cumans, lithuania,";

		return str;
	}

	public static String slaveCsv() {
		return "slave,";
	}

	public static String byzantiumCsv() {
		return "byzantium,";
	}


	public static String factionsListToCsv(List<String> factionsList) {
		String str="";

		for (String faction : factionsList)
			str += faction + ", ";

		return str;
	}

	protected static String _ChristianFactionsCsv = null;

}
