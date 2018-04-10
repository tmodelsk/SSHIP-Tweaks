package tm.mtwModPatcher.sship.features.global;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.common.collections.ArrayUniqueList;
import tm.common.collections.ListUnique;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.params.ParamId;
import tm.mtwModPatcher.lib.common.core.features.params.ParamIdInteger;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.StatPriArmor;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.managers.FactionsDefs;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static tm.mtwModPatcher.sship.lib.Buildings.*;

/** Wetern Knight +1 Armor etc */
public class CatholicFactionsBoost extends Feature {

	@Override
	public void setParamsCustomValues() {
		templeConversionRateBonus = 0;
	}

	@Override
	public void executeUpdates() throws Exception {
		edu = getFileRegisterForUpdated(ExportDescrUnitTyped.class);
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);

		knightsWesternArmorUp();

		if(templeConversionRateBonus > 0)
			religiousConversionTempleBonus();
	}

	private void knightsWesternArmorUp() {
		val islamFactionsCsv = FactionsDefs.islamFactionsCsv();
		val orthodoxCsv = FactionsDefs.ortodoxFactionsCsv();
		val turanianCsv = FactionsDefs.turanianFactionsCsv();

		// ## Find All not muslim & not pagan Knights ##
		List<UnitDef> knights = edu.getUnits().stream()
				.filter(
						u -> u.Attributes.contains("knight")
								&& FactionsDefs.isNotAnyFirstCsvFactorsInSecondCsv(islamFactionsCsv , u.Ownership)
								&& FactionsDefs.isNotAnyFirstCsvFactorsInSecondCsv(orthodoxCsv, u.Ownership)
								&& FactionsDefs.isNotAnyFirstCsvFactorsInSecondCsv(turanianCsv, u.Ownership)
				)
				.collect(Collectors.toList());
		//String orgKnightscsvStr = "", modifiedKnightsCsvStr = "";
		for(UnitDef unit : knights) {
			StatPriArmor armors = unit.StatPriArmour;
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
		//edb.insertIntoBuildingCapabilities("temple_catholic", "small_church", "city", attribStr + 0);
		edb.insertIntoBuildingCapabilities(TempleCatholicCity, "church", CityType, attribStr + templeConversionRateBonus);
		edb.insertIntoBuildingCapabilities(TempleCatholicCity, "abbey", CityType, attribStr + templeConversionRateBonus);
		edb.insertIntoBuildingCapabilities(TempleCatholicCity, "cathedral", CityType, attribStr + templeConversionRateBonus +1);
		edb.insertIntoBuildingCapabilities(TempleCatholicCity, "huge_cathedral", CityType, attribStr + templeConversionRateBonus +1);

		// # Castle # : temple_catholic_castle : levels small_chapel chapel
		edb.insertIntoBuildingCapabilities(TempleCatholicCastle, "small_chapel", CastleType, attribStr + templeConversionRateBonus);
		edb.insertIntoBuildingCapabilities(TempleCatholicCastle, "chapel", CastleType, attribStr + templeConversionRateBonus +1);
	}

	@Override
	public ListUnique<ParamId> defineParamsIds() {
		val pars = new ArrayUniqueList<ParamId>();

		pars.add(new ParamIdInteger("TempleConversionRateBonus", "Temple Conversion Rate Bonus",
				feature -> ((CatholicFactionsBoost) feature).getTempleConversionRateBonus(),
				(feature, value) -> ((CatholicFactionsBoost) feature).setTempleConversionRateBonus(value)));

		return pars;
	}

	private ExportDescrUnitTyped edu;
	private ExportDescrBuilding edb;

	@Getter @Setter private int templeConversionRateBonus;

	private static String nl = System.lineSeparator();

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("621c8ac2-3772-40b0-8ed0-b4a341220431");

	public CatholicFactionsBoost() {
		super("Catholic factions boost");

		addCategory("Campaign");

		setDescriptionShort("Various boosts for Catholic Factions");
		setDescriptionUrl("http://tmsship.wikidot.com/catholic-factions-boost");
	}
}
