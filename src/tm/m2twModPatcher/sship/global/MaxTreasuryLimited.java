package tm.m2twModPatcher.sship.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.Tuple3;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.ContainerBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.IfBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.AddMoneyLocalConsole;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.various.CompareCounter;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.CampaignScript;

import java.util.List;
import java.util.UUID;

/**
 * Max Treasury Limited - there's higher and higher inflation if faction treasury is closer to limit
 */
public class MaxTreasuryLimited extends Feature {

	@Getter
	@Setter
	private int MaxTreasuryLimit = 150000;

	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);

		LinesProcessor lines = _CampaignScript.getLines();

		int insertIndex = lines.findExpFirstRegexLine("^;============= G5 FACTION ECONOMY SCRIPT");
		insertIndex = lines.findExpFirstRegexLine(";Part 7", insertIndex + 1);
		insertIndex = lines.findExpFirstRegexLine(";Part 7", insertIndex + 1);
		//lines.findFirstByRexexLines("^;============= G5 FACTION ECONOMY SCRIPT" , "monitor_event");
		if (insertIndex < 0) throw new PatcherLibBaseEx("G5 Faction Script / Part 7 - not found !");
		insertIndex++;

		String s = "";

		List<String> factionNames = FactionsDefs.allFactionsList();
		factionNames.remove("slave");

		val levelsManager = new MaxTreasuryLimitLevels();
		val levels = levelsManager.getLevels(MaxTreasuryLimit, 5);

		s += ";============= Faction Maximum Treasury Limit script START =====" + nl;

		// First
		Tuple3<Integer, Integer, Integer> item = levels.get(0);
		s += createInflationMonitor(factionNames, item.getItem1(), item.getItem2(), item.getItem3());

		for (int i = 1; i < levels.size() - 1; i++) {
			item = levels.get(i);
			s += createInflationMonitor(factionNames, item.getItem1(), item.getItem2(), item.getItem3());
		}

		// Last
		item = levels.get(levels.size() - 1);
		s += createInflationMonitor(factionNames, item.getItem1(), null, item.getItem3());

		/* old
		s += createInflationMonitor(factionNames, 30001 , 40000, -1000);
		s += createInflationMonitor(factionNames, 40001 , 50000, -3000);
		s += createInflationMonitor(factionNames, 50001 , 60000, -6000);
		s += createInflationMonitor(factionNames, 60001 , 70000 , -10000);
		s += createInflationMonitor(factionNames, 70001 , null , -40000);*/

		s += ";============= Faction Maximum Treasury Limit script END =====" + nl;
		lines.insertAt(insertIndex, s);
	}

	private String createInflationMonitor(List<String> factions, int greaterThanTreasure, Integer lesserThanTreasure, int inflation) throws PatcherLibBaseEx {
		String s = "";

		s += "monitor_event PreFactionTurnStart and Treasury > " + greaterThanTreasure + nl; //I_CompareCounter ai_ec_id > 0" +nl;	// 0 - local player , 1 - rebels
		//s += "   and Treasury > "+greaterThanTreasure +nl;

		if (lesserThanTreasure != null)
			s += "   and Treasury < " + lesserThanTreasure + nl;

		for (String factionName : factions) {
			s += getFactionIfBlock(factionName, inflation) + nl;
		}

		s += getLocalFactionBlock(factions, greaterThanTreasure, lesserThanTreasure, inflation) + nl;

		s += "end_monitor" + nl + nl;

		return s;
	}

	private String getLocalFactionBlock(List<String> factions, int greaterThanTreasure, Integer lesserThanTreasure, int inflation) {
		val block = new ContainerBlock();

		val ifLocal = new IfBlock(new CompareCounter("ai_ec_id", "=", 0));
		ifLocal.andCondition(new CompareCounter("pl_ec_id", ">", 0));

		if (inflation < -30000) {
			val half = inflation / 2;
			ifLocal.add(new AddMoneyLocalConsole(half));
			ifLocal.add(new AddMoneyLocalConsole(inflation - half));
		} else ifLocal.add(new AddMoneyLocalConsole(inflation));    // Najprosciej

//		for (val factionName : factions) {
//			val factionId = FactionsDefs.getFactionAiEcId(factionName);
//
//			val ifLocalFaction = new IfBlock(new CompareCounter("pl_ec_id" , "=" , factionId));
//			ifLocalFaction.add(new AddMoney(factionName, inflation));
//			ifLocalFaction.add(new AddMoneyLocalConsole(inflation));
//
//			ifLocal.add(ifLocalFaction);
//		}
		block.add(ifLocal);

		String resStr = "";
		for (String line : block.getScriptBlock().getLines()) resStr += line + nl;

		return resStr;
	}

	protected String getFactionIfBlock(String factionName, int moneyBonus) throws PatcherLibBaseEx {
		String s = "";

		s += "    if I_CompareCounter ai_ec_id = " + FactionsDefs.getFactionAiEcId(factionName) + nl;

		if (moneyBonus < -30000) {
			val half = moneyBonus / 2;
			s += "       add_money " + factionName + " " + half + nl;
			s += "       add_money " + factionName + " " + (moneyBonus - half) + nl;
		} else s += "       add_money " + factionName + " " + moneyBonus + nl;

		s += "    end_if";

//		if I_CompareCounter ai_ec_id = 2
//		add_money venice -5000
//		end_if

		return s;
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdInteger("MaxTreasuryLimit", "Max Treasury Limit",
				feature -> ((MaxTreasuryLimited) feature).getMaxTreasuryLimit(),
				(feature, value) -> ((MaxTreasuryLimited) feature).setMaxTreasuryLimit(value)));

		return pars;
	}

	protected CampaignScript _CampaignScript;
	protected String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}

	public static UUID Id = UUID.randomUUID();

	public MaxTreasuryLimited() {
		super("Max Treasury is limited");

		addCategory("Economy");

		setDescriptionUrl("http://tmsship.wikidot.com/max-treasury-limited");
		setDescriptionShort("Max faction treasury is limited, after partiucular amounts of treasury 'inflation' is applied");
	}
}
