package tm.mtwModPatcher.sship.features.armyUnits;

import lombok.val;
import tm.mtwModPatcher.lib.managers.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitStatPriArmor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** Wetern Knight +1 Armor etc */
public class CatholicFactionsBoost extends Feature {

	@Override
	public void executeUpdates() throws Exception {
		exportDescrUnit = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		exportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);

		knightsWesternArmorUp();
		religiousConversionTempleBonus();
	}

	private void knightsWesternArmorUp() {
		val islamFactionsCsv = FactionsDefs.islamFactionsCsv();
		val orthodoxCsv = FactionsDefs.ortodoxFactionsCsv();
		val turanianCsv = FactionsDefs.turanianFactionsCsv();

		// ## Find All not muslim & not pagan Knights ##
		List<UnitDef> knights = exportDescrUnit.getUnits().stream()
				.filter(
						u -> u.Attributes.contains("knight")
								&& FactionsDefs.isNotAnyFirstCsvFactorsInSecondCsv(islamFactionsCsv , u.Ownership)
								&& FactionsDefs.isNotAnyFirstCsvFactorsInSecondCsv(orthodoxCsv, u.Ownership)
								&& FactionsDefs.isNotAnyFirstCsvFactorsInSecondCsv(turanianCsv, u.Ownership)
				)
				.collect(Collectors.toList());
		//String orgKnightscsvStr = "", modifiedKnightsCsvStr = "";
		for(UnitDef unit : knights) {
			UnitStatPriArmor armors = unit.StatPriArmour;
			//orgKnightscsvStr += unit.Name+";"+ (armors.Armour + armors.DefenceSkill + armors.Shield)+";"+ armors.Armour + ";"+armors.DefenceSkill+";"+armors.Shield+";"+nl;

			int armor = unit.StatPriArmour.Armour;
			if(armor <= 10) {
				unit.StatPriArmour.Armour += 2;
				unit.StatCost.Cost *= 1.10;	// +10 %
				unit.StatCost.Cost *= 1.10;
			}
			else if(armor > 10 && armor < 20) {
				unit.StatPriArmour.Armour += 1;
				unit.StatCost.Cost *= 1.10;	// +10 %
				unit.StatCost.Cost *= 1.10;
			}

			// ## Boost sword damage +1
			//if(unit.isCategoryInfantry()) unit.StatPri.Damage++;
			//if(unit.isCategoryCavalry()) unit.StatSec.Damage++;

			//armors = unit.StatPriArmour;
			//modifiedKnightsCsvStr += unit.Name+";"+ (armors.Armour + armors.DefenceSkill + armors.Shield)+";"+ armors.Armour + ";"+armors.DefenceSkill+";"+armors.Shield+";"+nl;
		}

		// Debug puproses
		int size = knights.size();
		String knightsNamesStr="";
		for(val kn : knights) {
			knightsNamesStr += kn.Name + nl;
		}
	}

	private void religiousConversionTempleBonus() throws PatcherLibBaseEx {

		// ### Religion Conversion bonus Catholic Chutches  ###

		String attribStr = "        religion_level bonus ";

		// # City # : temple_catholic : levels small_church church abbey cathedral huge_cathedral
		//exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "small_church", "city", attribStr + 0);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "church", "city", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "abbey", "city", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "cathedral", "city", attribStr + 2);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic", "huge_cathedral", "city", attribStr + 2);

		// # Castle # : temple_catholic_castle : levels small_chapel chapel
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic_castle", "small_chapel", "castle", attribStr + 1);
		exportDescrBuilding.insertIntoBuildingCapabilities("temple_catholic_castle", "chapel", "castle", attribStr + 2);
	}

	private ExportDescrUnitTyped exportDescrUnit;
	private ExportDescrBuilding exportDescrBuilding;

	private static String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("");

	public CatholicFactionsBoost() {
		super("Catholic factions boost");

		addCategory("Campaign");

		setDescriptionShort("Various boosts for Catholic Factions");
		setDescriptionUrl("http://tmsship.wikidot.com/catholic-factions-boost");
	}
}
