package tm.mtwModPatcher.sship.armyUnits;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdDouble;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.fileEntities.data.world.maps.campaign.DescrMercenaries;

import java.util.*;

/** Mercenaries costs : low initial recruitment cost, high upkeep cost */
public class MercenariesCosts extends Feature {

	private DescrMercenaries _DescrMercenaries;
	private ExportDescrUnitTyped _ExportDescrUnit;
	private ExportDescrBuilding _ExportDescrBuilding;

	@Getter @Setter
	private Double recruitCostMulti = 0.33;
	@Getter @Setter
	private Double upkeepCostsMulti = 1.8;

	@Override
	public void executeUpdates() throws Exception {
		_DescrMercenaries = fileEntityFactory.getFile(DescrMercenaries.class);
		registerUpdatedFile(_DescrMercenaries);
		_ExportDescrUnit = fileEntityFactory.getFile(ExportDescrUnitTyped.class);
		registerUpdatedFile(_ExportDescrUnit);
		_ExportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		LinesProcessor lines = _ExportDescrBuilding.getLines();

		List<String> unitNamesExcluded = new ArrayList<>();

		// ### Exclude Recuitable units ###
		Set<String> mercUnitNames = _DescrMercenaries.getAllUnitNames();

		// ## Check if unit if recruitable EXCEPT particular buildings : byzantine_mercenary_barracks tree
		// building byzantine_mercenary_barracks

		int mercBarracksStart = lines.findExpFirstRegexLine("^building\\s+byzantine_mercenary_barracks");
		int mercBarracksEnd = lines.findExpFirstRegexLine("^building\\s+" , mercBarracksStart+1);	// search for ANY merc building tag

		List<Pair<Integer,Integer>> exeptRanges = new ArrayList<>();
		exeptRanges.add(new Pair<>(mercBarracksStart , mercBarracksEnd));

		for (String mercUnitName : mercUnitNames) {
			String recruitPoolRegex = "^\\s*recruit_pool\\s+\""+ mercUnitName +"\"";
			int mercIndex =  _ExportDescrBuilding.getLines().findFirstRegexLine(recruitPoolRegex , exeptRanges);
			if(mercIndex > 0) {
				unitNamesExcluded.add(mercUnitName);
			}
		}

		// #### Add another Excludes ####
		unitNamesExcluded.add("Hunters");
		List<String> crusadersMercs = Arrays.asList("Crusader Knights" , "Crusader Sergeants" , "Dismounted Crusader Knights");
		unitNamesExcluded.addAll(crusadersMercs);

		mercUnitNames = _DescrMercenaries.updateAllCostsByMultiplier(recruitCostMulti , unitNamesExcluded);
		for (String mercUnitName : mercUnitNames) {
			_ExportDescrUnit.updateUpkeepByMultiplier(mercUnitName, upkeepCostsMulti);
		}

		// ## Crusaders Mercenaries ##
		_DescrMercenaries.updateUnitCostsByMultiplier("Crusader Sergeants" , 0.25);	// 0.5 - 40%
		_ExportDescrUnit.updateUpkeepByMultiplier("Crusader Sergeants", 1.7);	// 1.7

		_DescrMercenaries.updateUnitCostsByMultiplier("Crusader Knights" , 0.28); // 0.5 - 40%
		_ExportDescrUnit.updateUpkeepByMultiplier("Crusader Knights", 1.128);	// bylo 1.25 ale -10%

		_DescrMercenaries.updateUnitCostsByMultiplier("Dismounted Crusader Knights" , 0.3);
		_ExportDescrUnit.updateUpkeepByMultiplier("Dismounted Crusader Knights", 1.128);


		// Moors Christian Guard Recuit -20%, Upkeep + 50%
		double chgRecruitMulti = 0.8, chgUpkeepMulti = 1.5;
		UnitDef christianGuard = _ExportDescrUnit.loadUnit("Christian Guard");
		christianGuard.StatCost.Cost *= chgRecruitMulti;
		christianGuard.StatCost.Upkeep *= chgUpkeepMulti;

		christianGuard = _ExportDescrUnit.loadUnit("Dismounted Christian Guard");
		christianGuard.StatCost.Cost *= chgRecruitMulti;
		christianGuard.StatCost.Upkeep *= chgUpkeepMulti;
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val parIds = new ArrayUniqueList<ParamId>();

		ParamIdDouble parDbl;
		parDbl= new ParamIdDouble("RecruitCostMulti" , "Recruit Cost Multiplier",
				feature -> ((MercenariesCosts)feature).getRecruitCostMulti(),
				(feature, value) -> ((MercenariesCosts)feature).setRecruitCostMulti(value));
		parIds.add(parDbl);

		parDbl= new ParamIdDouble("UpkeepCostMulti" , "Upkeep Cost Multiplier",
				feature -> ((MercenariesCosts)feature).getUpkeepCostsMulti(),
				(feature, value) -> ((MercenariesCosts)feature).setUpkeepCostsMulti(value));
		parIds.add(parDbl);

		return parIds;
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public MercenariesCosts() {
		super("Mercenaries costs");

		addCategory("Campaign");
		addCategory("Units");

		setDescriptionShort("Mercenaries costs tweaks : lower recruitment costs and higher upkeep costs");

		setDescriptionUrl("http://tmsship.wikidot.com/mercenaries-costs");
	}
}
