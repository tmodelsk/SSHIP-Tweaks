package tm.m2twModPatcher.sship.Obsolete;

import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.CampaignScript;
import tm.m2twModPatcher.sship.agentsCharacters.FactionsDropZones;
import tm.m2twModPatcher.sship.agentsCharacters.GeneralDropZone;
import tm.m2twModPatcher.sship.armyUnits.ArmySuppliesCosts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Adds more Generals & Governors
 */
public class MultiGeneralsGonvernors extends Feature {
	private CampaignScript _CampaignScript;

	public int SettlementsHostedMaxCount = 30;

	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);

		int insertIndex = _CampaignScript.getLastInsertLineForMonitors();
		List<String> factions = FactionsDefs.allFactionsExceptRebelsList();

		factions.remove("mongols");
		factions.remove("teutonic_order");
		factions.remove("papal_states");


		List<String> rl = new ArrayList<>();

		rl.add("");
		rl.add(_commentPrefix + "Start, TM Patcher Added -----");
		rl.add("");

		// ### Create Section : Declare & Set for Faction_GeneralCounter's ###
		rl.addAll(createDeclareFactionCounters(factions));


		// ### Create section for Factions
		rl.addAll(createGeneralsInserterForFactions(factions));

		// ## Create section for Factions Generals Counting monitors
		rl.addAll(createGeneralsCountingMonitors(factions));


		rl.add("");
		rl.add(_commentPrefix + "END, TM Patcher Added -----");
		rl.add("");

		// ## Insert all generated monitors ##
		_CampaignScript.getLines().insertAt(insertIndex, rl);
	}

	private List<String> createDeclareFactionCounters(List<String> factions) {
		List<String> rl = new ArrayList<>();

		rl.add(_commentPrefix + "Factions variables section");
		for (String factionName : factions) {
			rl.add("declare_counter " + getVariableGeneralsCountName(factionName));
			rl.add("set_counter "+ getVariableGeneralsCountName(factionName) +" 999");
		}

		rl.add("declare_counter "+IsGeneralNeedsCreation);
		rl.add("declare_counter "+IsGeneralOrGovernor);

		return rl;
	}

	private List<String> createGeneralsInserterForFactions(List<String> factions) throws PatcherLibBaseEx {
		List<String> rl = new ArrayList<>();

		rl.add(_commentPrefix + "Factions generals inserting monitors section START");

		for (String factionName : factions) {
			rl.addAll(createGeneralInserterMonitor(factionName));
		}

		rl.add(_commentPrefix + "Factions generals inserting monitors section END");

		return rl;
	}

	private List<String> createGeneralInserterMonitor(String factionName) throws PatcherLibBaseEx {
		List<String> rl = new ArrayList<>();

		rl.add("monitor_event PreFactionTurnStart FactionType "+factionName);
		rl.add("	set_counter "+ IsGeneralNeedsCreation +" 0");

		rl.add("	if I_CompareCounter ai_ec_id = 0");	// Block For Player
		for(int i=1 ; i <= SettlementsHostedMaxCount ; i++)
			rl.addAll(createExpectedGeneralsManagerIfBlock(factionName, true, i , "=" , null));

		// Last If for FactionSettlementCount < Max
		rl.addAll(createExpectedGeneralsManagerIfBlock(factionName, true, SettlementsHostedMaxCount , ">" , getExpectedGeneralsCount(SettlementsHostedMaxCount, true) + 10));

		rl.add("	end_if");

		rl.add("	if I_CompareCounter ai_ec_id > 1");	// Block  For AI
		for(int i=1 ; i <= SettlementsHostedMaxCount ; i++)
			rl.addAll(createExpectedGeneralsManagerIfBlock(factionName, false, i , "=" , null));

		// Last If for FactionSettlementCount < Max
		rl.addAll(createExpectedGeneralsManagerIfBlock(factionName, false, SettlementsHostedMaxCount , ">" , getExpectedGeneralsCount(SettlementsHostedMaxCount, false) + 10));
		rl.add("	end_if");

		rl.add("	if I_CompareCounter "+ IsGeneralNeedsCreation +" = 1");

		rl.addAll(createSpawnArmyBlock(factionName));

		rl.add("	end_if");

		rl.add("	set_counter "+ IsGeneralNeedsCreation +" 0");	// reset creation flag
		rl.add("	set_counter "+ getVariableGeneralsCountName(factionName) +" 0");	// reset generals counter

		rl.add("end_monitor");

		return rl;
	}

	private List<String> createExpectedGeneralsManagerIfBlock(String factionName, boolean isPlayer , int settlementsNumber ,
															  String settlementsOperator, Integer expectedGeneralsOverride) {
		List<String> rl = new ArrayList<>();

		int expectedGenerals;
		if(expectedGeneralsOverride == null)
			expectedGenerals = getExpectedGeneralsCount(settlementsNumber , isPlayer);
		else expectedGenerals = expectedGeneralsOverride;

		rl.add("		if I_NumberOfSettlements "+factionName+"  "+ settlementsOperator +" " + settlementsNumber);
		rl.add("			and I_CompareCounter "+ getVariableGeneralsCountName(factionName)+" < "+expectedGenerals);
		rl.add("				set_counter "+ IsGeneralNeedsCreation +" 1");
		rl.add("		end_if");

		return rl;
	}

	private List<String> createSpawnArmyBlock(String fationName) throws PatcherLibBaseEx {
		List<String> rl = new ArrayList<>();

		GeneralDropZone dz = _FactionDropZone.getDefaultZOne(fationName);
		if(dz == null) throw new PatcherLibBaseEx("Drop Zone for faction " + fationName +" not found!");

		String armySizeStr = "";
		boolean shouldSetTraitArmySize = featuresContainer.isFeatureEnabled(ArmySuppliesCosts.Id);
		if(shouldSetTraitArmySize)
			armySizeStr = ",ArmySize 1";

		FactionInfo fi = FactionsDefs.loadFactionInfo(fationName);

		//GeneralDropZone aragonDz = _FactionDropZone.getDefaultZOne("aragon");
		rl.add("			set_counter "+IsGeneralOrGovernor+" 0");
		rl.add("			if RandomPercent >= 50");
		rl.add("				set_counter "+IsGeneralOrGovernor+" 1");
		rl.add("			end_if");

		rl.add("			if I_CompareCounter "+ IsGeneralOrGovernor +" = 1");	// Army General
		rl.add("				spawn_army");
		rl.add("					faction "+fationName);
		rl.add("					character	random_name, named character, age 22, x "+ dz.X +", y "+ dz.Y);
		rl.add("					traits MilitaryInclination 1,Military_Edu 1,NaturalMilitarySkill 1,GoodCommander 1,Loyal 3,ReligionStarter 1" + armySizeStr);
		rl.add("					unit	"+ fi.BodyguardUnitName +"		exp 0 armour 0 weapon_lvl 0");
		rl.add("				end");
		rl.add("				log always Creating GENERAL for "+ fationName);
		rl.add("			end_if");
		rl.add("			if I_CompareCounter "+ IsGeneralOrGovernor +" = 0");	// governor
		rl.add("				spawn_army");
		rl.add("					faction "+fationName);
		rl.add("					character	random_name, named character, age 22, x "+ dz.X +", y "+ dz.Y);
		rl.add("					traits GovernorInclination 1,GoodAdministrator 1,Loyal 3 ,ReligionStarter 1 ,"+fi.Education+"_Edu 1" + armySizeStr);	//
		rl.add("					unit	"+ fi.BodyguardUnitName +"		exp 0 armour 0 weapon_lvl 0");
		rl.add("				end");
		rl.add("				log always Creating GOVERNOR for "+ fationName);
		rl.add("			end_if");


		 // GovernorInclination

		return rl;
	}

	private void fillDropZones() {
		_FactionDropZone  = new FactionsDropZones();


		_FactionDropZone.add("england", 68 , 215 , "NE Bodyguard");
		_FactionDropZone.add("scotland",44, 248 , "NE Bodyguard");
		_FactionDropZone.add("denmark", 176, 248 , "NE Bodyguard" );
		_FactionDropZone.add("norway", 139, 276 , "Royal Hirdsmen" );
		_FactionDropZone.add("france", 95, 191 ,"NE Bodyguard");
		_FactionDropZone.add("hre", 144, 208 , "NE Bodyguard" );
		_FactionDropZone.add("milan", 405, 62 , "ME Bodyguard" );
		_FactionDropZone.add("pisa", 157, 156 , "ME Bodyguard");
		_FactionDropZone.add("aragon", 59, 155 , "SE Bodyguard");
		_FactionDropZone.add("spain", 46, 152 , "SE Bodyguard" );
		_FactionDropZone.add("portugal", 26, 145,"SE Bodyguard" );
		_FactionDropZone.add("venice", 178, 171 , "SE Bodyguard");
		_FactionDropZone.add("sicily", 183, 112 , "SE Bodyguard");
		_FactionDropZone.add("poland", 216, 219  , "NE Bodyguard");
		_FactionDropZone.add("hungary", 213, 180 , "NE Bodyguard");
		_FactionDropZone.add("kievan_rus", 308, 210 , "EE Bodyguard");
		_FactionDropZone.add("russia", 304, 273 , "EE Bodyguard");
		_FactionDropZone.add("byzantium", 236, 115 , "Greek Bodyguard");
		_FactionDropZone.add("lithuania", 251, 247 , "Lith Bodyguard");
		_FactionDropZone.add("teutonic_order",  220, 159 , "EE Bodyguard");
		_FactionDropZone.add("jerusalem", 339, 78 , "NE Bodyguard");
		_FactionDropZone.add("turks", 460, 92  , "ME Bodyguard");
		_FactionDropZone.add("rum", 317, 115 , "ME Bodyguard");
		_FactionDropZone.add("egypt", 307, 43 , "ME Bodyguard");
		_FactionDropZone.add("moors", 24, 79 , "ME Bodyguard");
		_FactionDropZone.add("kwarezm", 374, 95  , "ME Bodyguard" );
		_FactionDropZone.add("cumans", 399, 183 , "Cuman Bodyguard");
		_FactionDropZone.add("mongols", 502, 160 , "Keshikten Bodyguard");
		_FactionDropZone.add("timurids", 392, 165  , "bodyguard georgia");
	}

	private int getExpectedGeneralsCount(int settlementsCount, boolean isPlayer) {
		int g;

		if(isPlayer)
			g = ((int)(settlementsCount * 1.21)) + 1;
		else
			g = ((int)(settlementsCount * 1.75)) + 1;

		return g;
	}

	private  List<String> createGeneralsCountingMonitors(List<String> factions) {
		List<String> rl = new ArrayList<>();

		rl.add(_commentPrefix + "Factions generals counting monitors section START");
		for (String factionName : factions) {
			rl.add("monitor_event CharacterTurnStart CharFactionType " + factionName);
			rl.add("	and IsGeneral");
			rl.add("		inc_counter "+ getVariableGeneralsCountName(factionName) +" 1");
			rl.add("end_monitor");
		}
		rl.add(_commentPrefix + "Factions generals counting monitors section END");

		return rl;
	}

	private String getVariableGeneralsCountName(String factionName) {
		return "GG_" + factionName +"_Count";
	}
	private static String IsGeneralNeedsCreation = "GG_IsGenNeedsCreation";
	private static String IsGeneralOrGovernor = "GG_IsGenOrGov";

	private static String _commentPrefix = ";---- Generals&Governors Spawn Script: ";

	private FactionsDropZones _FactionDropZone;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public MultiGeneralsGonvernors() {
		super("Multiple generals & governors spawning, mroe thet settlements count");

		fillDropZones();
	}
}
