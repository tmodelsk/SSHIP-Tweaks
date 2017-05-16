package tm.mtwModPatcher.lib.managers;

import lombok.val;
import tm.mtwModPatcher.lib.common.entities.FactionsDefs;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrBuilding.UnitRecuitmentInfo;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.UnitDef;
import tm.mtwModPatcher.lib.fileEntities.data.exportDescrUnit.UnitStatPriArmor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper methods for units related processing
 */
public class UnitsManager {

	public List<String> modifyArmorStats(String factionsFilterCsv, List<String> attributesRequired,
										 int minimumArmor, int maximumArmor, int armorModifier,
										 List<Pattern> unitsToExclude, ExportDescrUnitTyped exportDescrUnit) {
		List<String> modifiedNames = new ArrayList<>();

		String csvStr = "", nl = System.lineSeparator();
		String factionCheckExcludedCsv = "";

		// ##### Loop throught all _Units, find ... ####
		for (UnitDef unit : exportDescrUnit.getUnits()) {
			String unitOwnersCsv = "";
			unitOwnersCsv += unit.Ownership;

			// ### Check if needs to be excluded - ommitted ###
			boolean isShouldBeExcluded = false;
			// # exclude check #
			if (unitsToExclude != null && unitsToExclude.size() > 0)
				for (Pattern excludeNameRegex : unitsToExclude) {
					if (excludeNameRegex.matcher(unit.Name).find()) {
						isShouldBeExcluded = true;
						break;
					}
				}
			// # Attributes Check #
			if (attributesRequired != null && attributesRequired.size() > 0)
				for (String attrib : attributesRequired) {
					if (!unit.Attributes.contains(attrib)) {
						isShouldBeExcluded = true;
						break;
					}
				}

			if (isShouldBeExcluded)
				continue;

			// ## Faction Check ##
			if (FactionsDefs.isAllFirstCsvFactorsInSecondCsv(unitOwnersCsv, factionsFilterCsv)) {

				if (unit.StatPriArmour.Armour >= minimumArmor && unit.StatPriArmour.Armour <= maximumArmor) {
					//unit.StatPriArmour.Armour += armorModifier;
					modifiedNames.add(unit.Name);

					UnitStatPriArmor armor = unit.StatPriArmour;
					csvStr += unit.Name + ";" + (armor.Armour + armor.DefenceSkill + armor.Shield) + ";" + armor.Armour + ";" + armor.DefenceSkill + ";" + armor.Shield + ";" + nl;

				}
			} else factionCheckExcludedCsv += unit.Name + ";" + unit.Ownership + ";" + nl;
		}
		return modifiedNames;
	}

	public void multiplyUpkeepRecruitCosts(String factionsFilterCsv, double costMulti, double upkeepMulti,
										   List<Pattern> unitsToExclude, ExportDescrUnitTyped exportDescrUnit)  {
		// ##### Loop throught all _Units, ####
		for (UnitDef unit : exportDescrUnit.getUnits()) {
			String unitOwnersCsv = "";
			unitOwnersCsv += unit.Ownership;

			// ### Check if needs to be excluded - ommitted ###
//			boolean isShouldBeExcluded = false;
//			for (Pattern excludeNameRegex : unitsToExclude) {
//				if (excludeNameRegex.matcher(unit.Name).find()) {
//					isShouldBeExcluded = true;
//					break;
//				}
//			}
//			if (isShouldBeExcluded) continue;

			if(isUnitShouldBeExcluded(unit, unitsToExclude)) continue;

			// ## Faction Check ##
			if (FactionsDefs.isAllFirstCsvFactorsInSecondCsv(unitOwnersCsv, factionsFilterCsv)) {
				exportDescrUnit.updateCostAndUpkeepByMultipliers(unit.Name, costMulti, upkeepMulti);
			}
		}
	}

	public void addToReplenishRates(String unitName, String factionsFilterCsv,
									double relpenishRateMin, double replenishRateAddition, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+\"" + unitName + "\".+");

		Pattern factionRegex = Pattern.compile("factions\\s+\\{(.+)\\}");

		int index = 0;
		while (index >= 0) {
			index = lines.findFirstByRegexLine(regex, index + 1);
			if (index >= 0) {
				String lineOrg = lines.getLine(index);
				UnitRecuitmentInfo unitRecrInfo = exportDescrBuilding.parseUnitRecruitmentInfo(lineOrg);
				if (!unitName.equals(unitRecrInfo.Name))
					throw new PatcherLibBaseEx("Parsed Unit name should be equal to " + unitName);

				// ## Faction Check ##
				Matcher matcher = factionRegex.matcher(unitRecrInfo.RequirementStr);
				if (matcher.find()) {

					String factionsStr = matcher.group(1);
					if (unitRecrInfo.MaxStack >= 1) { // only True entries

						if (FactionsDefs.isAllFirstCsvFactorsInSecondCsv(factionsStr, factionsFilterCsv)) {
							// ### OK - this add satisfies FACTION condition ###

							boolean isUpdated = false;

							int rateOrg = (int) ((1.0 / unitRecrInfo.ReplenishRate));
							if (rateOrg > relpenishRateMin) {
								isUpdated = true;

								double newRate = 1.0 / (Double) (rateOrg + replenishRateAddition);

								double rounded = Math.ceil(newRate * 1000) / 1000;

								newRate = rounded;

								if (newRate > 1.0)
									throw new PatcherLibBaseEx("New relpenish rate > 1.0 , unit = " + unitRecrInfo.Name + " !!");

								unitRecrInfo.ReplenishRate = newRate;
							}

							if (isUpdated) {
								String lineUpdated = unitRecrInfo.toRecruitmentPoolLine();
								lines.replaceLine(index, lineUpdated);
							}
						}
					}
				}
			}
		}
	}

	public List<String> addToAllUnitsReplenishRates(String factionsFilterCsv, double relpenishRateMin, double replenishRateAddition,
											List<Pattern> unitsToExclude, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+.+");
		Pattern factionRegex = Pattern.compile("factions\\s+\\{(.+)\\}");
		val updatedLines = new ArrayList<String>();

		int index = 0;
		while (index >= 0) {
			index = lines.findFirstByRegexLine(regex, index + 1);
			if (index >= 0) {
				String lineOrg = lines.getLine(index);
				UnitRecuitmentInfo unitRecrInfo = exportDescrBuilding.parseUnitRecruitmentInfo(lineOrg);

				// ### Check if needs to be excluded - ommitted ###
				boolean isShouldBeExcluded = false;
				if (unitsToExclude != null)
					for (Pattern excludeNameRegex : unitsToExclude) {
						if (excludeNameRegex.matcher(unitRecrInfo.Name).find()) {
							isShouldBeExcluded = true;
							break;
						}
					}
				if (isShouldBeExcluded) continue;

				// ## Faction Check ##
				Matcher matcher = factionRegex.matcher(unitRecrInfo.RequirementStr);
				if (matcher.find()) {
					String factionsStr = matcher.group(1);

					// # exclude units with no Factions requirements assigned #
					String factionsStrNoWhitespaces = factionsStr.replace(",", "").replace(" ","");
					if(!factionsStrNoWhitespaces.isEmpty()) {
						if (unitRecrInfo.MaxStack >= 1) { // only True entries

							if (FactionsDefs.isAllFirstCsvFactorsInSecondCsv(factionsStr, factionsFilterCsv)) {
								// ### OK - this add satisfies FACTION condition ###
								boolean isUpdated = false;

								if (unitRecrInfo.MaxStack >= 1) {
									isUpdated = true;
									unitRecrInfo.MaxStack++;
								}

								int rateOrg = (int) ((1.0 / unitRecrInfo.ReplenishRate));
								if (rateOrg > relpenishRateMin) {
									isUpdated = true;

									double newRate = 1.0 / (Double) (rateOrg + replenishRateAddition);
									double rounded = Math.ceil(newRate * 1000) / 1000;
									newRate = rounded;

									if (newRate > 1.0) throw new PatcherLibBaseEx("New relpenish rate > 1.0 , unit = " + unitRecrInfo.Name + " !!");

									unitRecrInfo.ReplenishRate = newRate;
								}
								if (isUpdated) {
									String lineUpdated = unitRecrInfo.toRecruitmentPoolLine();
									updatedLines.add(lineUpdated);
									lines.replaceLine(index, lineUpdated);
								}
							}
						}
					}
				}
			}
		}

		return updatedLines;
	}

	public void enableFreeUpkeepAllUnits(List<Pattern> unitsToExclude, ExportDescrUnitTyped exportDescrUnit) {

		// ##### Loop throught all _Units, ####
		for (UnitDef unit : exportDescrUnit.getUnits()) {
			if(isUnitShouldBeExcluded(unit, unitsToExclude)) continue;

			unit.addAttribute(UnitDef.AttribFreeUpkeep);
		}
	}

	private boolean isUnitShouldBeExcluded(UnitDef unit, List<Pattern> unitsToExclude) {
		// ### Check if needs to be excluded - ommitted ###
		boolean isShouldBeExcluded = false;
		if(unitsToExclude != null)
			for (Pattern excludeNameRegex : unitsToExclude) {
				if (excludeNameRegex.matcher(unit.Name).find()) {
					isShouldBeExcluded = true;
					break;
				}
			}

		return isShouldBeExcluded;
	}

}
