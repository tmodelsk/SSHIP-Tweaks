package tm.mtwModPatcher.lib.managers;

import lombok.val;
import tm.common.Tuple2;
import tm.common.collections.CollectionUtils;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitStatPriArmor;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.UnitRecuitmentInfo;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Helper methods for units related processing
 */
public class UnitsManager {

	private Pattern factionRegex = Pattern.compile("factions\\s+\\{(.+)\\}");;

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

	/** Update unit entry replenish rates ONLY if all faction from factionsFilterCsv are in entry */
	@Deprecated
	public void updateReplenishRates(String unitName, String factionsFilterCsv,
									 double relpenishRateMin, double replenishRateAddition, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+\"" + unitName + "\".+");

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

	/** Updates all units entries replenish rates when ALL factions from factionsFilterCsv are in entry */
	public List<String> updateAllUnitsReplenishRatesByTurnNumber(String factionsFilterCsv, double relpenishRateMin, double replenishTurnsAddition,
																 List<Pattern> unitsToExclude, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+.+");
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

									double newRate = 1.0 / (Double) (rateOrg + replenishTurnsAddition);
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

	/** Adds replenish bonus by updating replensh (if all factions applies)
	 * 		or by coping unit entry but with single faction { factionSymbol}
	 * 		replenishMult - 1.0 no change	*/
	public void updateAllReplenish(List<String> unitsFilter, double replenishMult,
									   List<Pattern> unitsToExclude, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+.+");

		int index = 0;
		while (index >= 0) {
			index = lines.findFirstByRegexLine(regex, index + 1);
			if (index >= 0) {
				String lineOrg = lines.getLine(index);
				val unitRecrInfo = exportDescrBuilding.parseUnitRecruitmentInfo(lineOrg);

				// ### Check if needs to be excluded - ommitted ###
				if(unitsFilter != null && !unitsFilter.contains(unitRecrInfo.Name)) continue;
				if (isShouldBeExcluded(unitRecrInfo, unitsToExclude)) continue;
				if(unitRecrInfo.ReplenishRate <= 0.001) continue;

				// ## recruitment line is ok for Unit criteria
				unitRecrInfo.ReplenishRate *= replenishMult;
				lines.replaceLine(index, unitRecrInfo.toRecruitmentPoolLine());
			}
		}
	}


	/** Adds replenish bonus by updating replensh (if all factions applies)
	 * 		or by coping unit entry but with single faction { factionSymbol}
	 * 		replenishMult - 1.0 no change	*/
	public void updateOrAddReplenishBonusEntry(List<String> factions, List<String> unitsFilter, double replenishMult,
											   List<Pattern> unitsToExclude, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+.+");

		int index = 0;
		while (index >= 0) {
			index = lines.findFirstByRegexLine(regex, index + 1);
			if (index >= 0) {
				String lineOrg = lines.getLine(index);
				val unitRecrInfo = exportDescrBuilding.parseUnitRecruitmentInfo(lineOrg);

				// ### Check if needs to be excluded - ommitted ###
				if(unitsFilter != null && !unitsFilter.contains(unitRecrInfo.Name)) continue;
				if (isShouldBeExcluded(unitRecrInfo, unitsToExclude)) continue;
				if(unitRecrInfo.ReplenishRate <= 0.001) continue;

				// ## recruitment line is ok for Unit criteria, ### try to work on bulk mode (all factions specified) ###
				val requirments = unitRecrInfo.getUnitRequireSimple();
				if(requirments != null && CollectionUtils.isAllFirstElementsAreInSecond(requirments.Factions, factions)) {
					unitRecrInfo.ReplenishRate *= replenishMult;
					lines.replaceLine(index, unitRecrInfo.toRecruitmentPoolLine());
				}
				else {	// ## Loop throught factions, add new entries per findividual faction
					assertThat(replenishMult).isGreaterThan(1.0);
					val newEntryReplenighMult = replenishMult - 1.0;
					for(val factionSymbol : factions) {
						val unitInfoNew = newUnitEntryReplenishBonusForFaction(unitRecrInfo, newEntryReplenighMult, factionSymbol);
						if(unitInfoNew != null) {
							lines.insertAt(index, unitInfoNew.toRecruitmentPoolLine());
							index++;	// we want to ommit freshly added unit above
						}
					}
				}
			}
		}
	}


	/** Adds replenish bonus by coping unit entry but with single factions { factionSymbol} */
	public void addReplenishBonusEntry(String factionSymbol, List<String> unitsFilter,  double replenishMult,
									   List<Pattern> unitsToExclude, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+.+");

		int index = 0;
		while (index >= 0) {
			index = lines.findFirstByRegexLine(regex, index + 1);
			if (index >= 0) {
				String lineOrg = lines.getLine(index);
				val unitRecrInfo = exportDescrBuilding.parseUnitRecruitmentInfo(lineOrg);

				// ### Check if needs to be excluded - ommitted ###
				if(unitsFilter != null && !unitsFilter.contains(unitRecrInfo.Name)) continue;
				if (isShouldBeExcluded(unitRecrInfo, unitsToExclude)) continue;
				if(unitRecrInfo.ReplenishRate <= 0.001) continue;

				// ## recruitmen line is ok basing on Unit criteria
				assertThat(replenishMult).isGreaterThan(0.0);
				val unitInfoNew = newUnitEntryReplenishBonusForFaction(unitRecrInfo, replenishMult, factionSymbol);
				if(unitInfoNew != null) {
					lines.insertAt(index, unitInfoNew.toRecruitmentPoolLine());
					index++;	// we want to ommit freshly added unit above
				}
			}
		}
	}

	private UnitRecuitmentInfo newUnitEntryReplenishBonusForFaction(UnitRecuitmentInfo unitRecrInfo, double replenishMult, String factionSymbol) {
		UnitRecuitmentInfo unitInfoNew = null;

		val unitRequire = unitRecrInfo.getUnitRequireSimple();
		if(unitRequire != null && unitRequire.Factions.contains(factionSymbol)) {
			// ### Found, we need to add replenish line ####
			unitInfoNew = new UnitRecuitmentInfo();
			unitInfoNew.Name = unitRecrInfo.Name;
			unitInfoNew.MaxStack = 0;
			unitInfoNew.InitialReplenishCounter = 0;

			unitRequire.Factions.removeIf(fs -> !fs.equals(factionSymbol));
			unitInfoNew.setRequirementStr(unitRequire);

			unitInfoNew.ReplenishRate = unitRecrInfo.ReplenishRate * replenishMult;
		}

		return unitInfoNew;
	}

	/**
	 * Adds replenish bonus by coping unit entry + adds additional condition.
	 * orgReplenishMult - orginal entry replenish multiplier will by multiplied by this.
	 */
	public void addReplenishBonusEntryWithCondition(double orgReplenishMult, String additionalCondition,
													List<Pattern> unitsToExclude, ExportDescrBuilding exportDescrBuilding) {

		val list = new ArrayList<Tuple2<Double, String>>();
		list.add(new Tuple2<>(orgReplenishMult, additionalCondition));

		addReplenishBonusEntryWithCondition(list, unitsToExclude, exportDescrBuilding);
	}
	/**
	 * Adds replenish bonus by coping unit entry + adds additional condition.
	 * Works for list of [orgReplenishMult , condition]
	 * orgReplenishMult - orginal entry replenish multiplier will by multiplied by this.
	 */
	public void addReplenishBonusEntryWithCondition(List<Tuple2<Double, String>> bonusConditioList,
													List<Pattern> unitsToExclude, ExportDescrBuilding exportDescrBuilding) {
		LinesProcessor lines = exportDescrBuilding.getLines();

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		Pattern regex = Pattern.compile("^^\\s*recruit_pool\\s+.+");

		int index = 0;
		while (index >= 0) {
			index = lines.findFirstByRegexLine(regex, index + 1);
			if (index >= 0) {
				String lineOrg = lines.getLine(index);
				val unitRecrInfo = exportDescrBuilding.parseUnitRecruitmentInfo(lineOrg);

				// ### Check if needs to be excluded - ommitted ###
				if (isShouldBeExcluded(unitRecrInfo, unitsToExclude)) continue;
				if(unitRecrInfo.ReplenishRate <= 0.001) continue;

				for (val bonusCondition : bonusConditioList) {
					double orgReplenishMult = bonusCondition.getItem1();
					String additionalCondition = bonusCondition.getItem2();

					assertThat(orgReplenishMult).isGreaterThan(0.0);

					val unitInfoNew = newUnitEntryReplenishBonusWithCondition(unitRecrInfo, orgReplenishMult, additionalCondition);
					if(unitInfoNew != null) {
						lines.insertAt(index, unitInfoNew.toRecruitmentPoolLine());
						index++;	// we want to ommit freshly added unit above
					}
				}
			}
		}
	}

	private UnitRecuitmentInfo newUnitEntryReplenishBonusWithCondition(UnitRecuitmentInfo unitRecrInfo, double orgReplenishMult, String additionalCondition) {
		UnitRecuitmentInfo unitInfoNew = null;

		val unitRequire = unitRecrInfo.getUnitRequireSimple();
		if( unitRequire != null ) {
			// ### Found, we need to add replenish line ####
			unitInfoNew = new UnitRecuitmentInfo();
			unitInfoNew.Name = unitRecrInfo.Name;
			unitInfoNew.MaxStack = 0;
			unitInfoNew.InitialReplenishCounter = 0;

			String newCondition = "and " + additionalCondition;
			if(unitRequire.RestConditions != null) newCondition += " " + unitRequire.RestConditions;

			unitRequire.RestConditions = newCondition;
			unitInfoNew.setRequirementStr(unitRequire);

			unitInfoNew.ReplenishRate = unitRecrInfo.ReplenishRate * orgReplenishMult;
		}

		return unitInfoNew;
	}

	private boolean isShouldBeExcluded(UnitRecuitmentInfo unitRecrInfo, List<Pattern> unitsToExclude) {
		boolean isShouldBeExcluded = false;
		if (unitsToExclude != null)
			for (Pattern excludeNameRegex : unitsToExclude) {
				if (excludeNameRegex.matcher(unitRecrInfo.Name).find()) {
					isShouldBeExcluded = true;
					break;
				}
			}

			return isShouldBeExcluded;
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
