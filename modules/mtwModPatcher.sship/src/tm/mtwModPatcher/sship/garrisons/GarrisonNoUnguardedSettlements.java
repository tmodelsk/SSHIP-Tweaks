package tm.mtwModPatcher.sship.garrisons;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdBoolean;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.commands.AddMoney;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.CampaignScript;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrStratSectioned;
import tm.mtwModPatcher.lib.managers.SettlementManager;
import tm.mtwModPatcher.lib.managers.garrisons.GarrisonManager;
import tm.mtwModPatcher.lib.managers.garrisons.UnitGarrisonInfo;
import tm.mtwModPatcher.lib.managers.garrisons.UnitType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tomek on 2016-11-11.
 */
public class GarrisonNoUnguardedSettlements extends Feature {
	private CampaignScript _CampaignScript;
	private DescrStratSectioned _DescrStrat;
	private ExportDescrUnitTyped _ExportDescrUnit;
	private DescrRegions _DescrRegions;

	@Getter @Setter
	private boolean loggingEnabled = false;

	@Override
	public void executeUpdates() throws Exception {
		_CampaignScript = getFileRegisterForUpdated(CampaignScript.class);
		_DescrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);
		_ExportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		_DescrRegions = getFileRegisterForUpdated(DescrRegions.class);

		// ## Find Insert Index
		int insertIndex = _CampaignScript.getLastInsertLineForMonitors();

		SettlementManager settlementManager = new SettlementManager(_DescrStrat, _DescrRegions);

		List<SettlementInfo> settlementInfos = settlementManager.getAllSettlements();

		List<String> rl = new ArrayList<>();
		rl.add("");
		rl.add(";----- Garrison Script: No unguarded settlements v.1.0 START, TM Patcher Added -----");

		rl.addAll(createSettlementTroopRisingMonitors(settlementInfos));

		rl.add(";----- Garrison Script: No unguarded settlements END, TM Patcher Added -----");
		rl.add("");
		rl.add("");

		// ## Insert all generated monitors ##
		_CampaignScript.getLines().insertAt(insertIndex, rl);
	}

	private List<String> createSettlementTroopRisingMonitors(List<SettlementInfo> settlementInfos) throws Exception {
		List<String> rl = new ArrayList<>();

		for(SettlementInfo settlement : settlementInfos) {
			rl.addAll(createSettlementMonitor(settlement));
		}

		return rl;
	}

	private List<String> createSettlementMonitor(SettlementInfo settlement) throws Exception {
		List<String> rl = new ArrayList<>();

		rl.add("monitor_event SettlementTurnEnd SettlementName "+ settlement.Name +" ");
		rl.add("	and not IsSettlementGarrisoned ");
		//rl.add("");rl.add("");

		for(String factionName : FactionsDefs.allFactionsList()) {
			rl.addAll(createFactionIfBlock(factionName, settlement));
		}

		rl.add("end_monitor");

		return rl;
	}

	private List<String> createFactionIfBlock(String factionName, SettlementInfo settlement) throws Exception {
		List<String> rl = new ArrayList<>();

		int unitCost;

		rl.add("	if I_SettlementOwner " + settlement.Name + " = " + factionName);

		List<UnitGarrisonInfo> units = _GarrisonMnager.getUnits(settlement, factionName);
		if(units == null || units.size() == 0)
			throw new PatcherLibBaseEx("No units for "+settlement.Level + " Faction: "+factionName);

		UnitGarrisonInfo unit = units.get(0);	// take First - 'default' spearmen
		// Check if it's spearmen
		if(unit.Type != UnitType.LevyInfantry && unit.Type != UnitType.Spearmen)
			throw new PatcherLibBaseEx("Spearmen expected as first Unit Type from GarrisonManager !");

		validateUnitInFaction(unit.Name, factionName, settlement);

		rl.add("		create_unit "+ settlement.Name +", "+ unit.Name +", num "+ unit.Quantity +", exp "+unit.Experience+", arm "+unit.Armor+", wep "+unit.Weapon);

		if(factionName.equals("slave"))
			unitCost = 0;
		else
			unitCost = calculateUnitCost(unit.Name);

		if(unitCost > 0) {
			rl.add( "		" + new AddMoney(factionName, -unitCost).getString());
		}

		if(loggingEnabled)
			rl.add("		log always ### Garrison Script: Raising unit in Unguarded Settlement: Settlement: "
								+ settlement.Name +" : " + factionName + " : " + unit.Name +"(" + unitCost+" fl)" );

		rl.add("	end_if");
		return rl;
	}

	private int calculateUnitCost(String unitName) throws PatcherLibBaseEx {
		return  (int)(_ExportDescrUnit.loadUnit(unitName).StatCost.Cost * 0.5);
	}

	private void validateUnitInFaction(String unitName, String factionName, SettlementInfo settlement) throws PatcherLibBaseEx {
		String ownershipCsv = _ExportDescrUnit.loadUnit(unitName).Ownership;

		if( !ownershipCsv.contains("all") && !ownershipCsv.contains(factionName) )
			throw new PatcherLibBaseEx("Unit "+unitName + " don't belongs to "+factionName + " Settl: "+settlement.Name+" .Level="+settlement.Level + " .Creator="+settlement.CreatedByFaction);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();


		parIds.add(new ParamIdBoolean("LoggingEnabled" , "Logging Enabled" ,
				feature -> ((GarrisonNoUnguardedSettlements)feature).isLoggingEnabled(),
				(feature, value) -> ((GarrisonNoUnguardedSettlements)feature).setLoggingEnabled(value)));

		return parIds;
	}

	private GarrisonManager _GarrisonMnager;

	private static String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public GarrisonNoUnguardedSettlements(GarrisonManager garrisonManager) throws PatcherLibBaseEx {
		super("Garrison Script : No Unguarded Settlements");

		addCategory("Campaign");
		setDescriptionShort("Garrison Script : No Unguarded Settlements, adds unit to empty settlement");
		setDescriptionUrl("http://tmsship.wikidot.com/garrison-script");

		_GarrisonMnager = garrisonManager;
	}
}