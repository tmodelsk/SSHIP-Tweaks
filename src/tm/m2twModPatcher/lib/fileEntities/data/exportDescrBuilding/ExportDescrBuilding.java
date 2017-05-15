package tm.m2twModPatcher.lib.fileEntities.data.exportDescrBuilding;

import lombok.val;
import tm.common.Range;
import tm.common.Tuple2;
import tm.common.Tuple3;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;
import tm.m2twModPatcher.lib.common.entities.SettlementLevel;
import tm.m2twModPatcher.lib.common.entities.SettlementLevelConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-04-16.
 */
public class ExportDescrBuilding extends LinesProcessorFileEntity {

	public List<Tuple2<String, List<Integer>>> addFlatCityCastleIncome(String requires, double factor) throws PatcherLibBaseEx {
		String attributeStr = "       income_bonus bonus ";
		// 250 , 375 , 562 , 843 , 1264

		val result = new ArrayList<Tuple2<String, List<Integer>>>();
		int base = 250;
		double multi = 1.5;

		base = (int) (base * factor);
		int baseTmp = base;
		val cityBonuses = new ArrayList<Integer>();

		insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", attributeStr+ baseTmp + requires);
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);

		result.add(new Tuple2<>("City", cityBonuses));

		// # Castles #
		baseTmp = base;
		val castleBonuses = new ArrayList<Integer>();
		insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr+ baseTmp + requires );
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr+ baseTmp  + requires);
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", attributeStr+ baseTmp + requires );
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", attributeStr+ baseTmp + requires );
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", attributeStr+ baseTmp + requires );
		castleBonuses.add(baseTmp);

		result.add(new Tuple2<>("Castle", castleBonuses));

		return result;
	}

	public void removeBuildingCapabilitiesByRegex(String buildingName, String levelName, String castleOrCity, String regexStr) throws PatcherLibBaseEx {
		int capabilityIndex = findBuildingCapabilityIndex(buildingName, levelName, castleOrCity);
		if(capabilityIndex < 0) throw new PatcherLibBaseEx("Building not found: "+buildingName+" "+levelName+" "+castleOrCity);

		// Find end of this block
		LinesProcessor lines = _Lines;

		int endCapabilityIndex = lines.findExpFirstRegexLine("^\\s*\\{", capabilityIndex+2);

		lines.removeAllRegexLinesInRange(regexStr, capabilityIndex, endCapabilityIndex);
	}
	public void removeUnitRecruitment(String unitName) {
		 //        recruit_pool  "EE Peasants"  2   0.077   2  0  requires factions { russia, kievan_rus, hungary, lithuania, }
		val patt = Pattern.compile("^\\s*recruit_pool\\s+\"" +unitName+ "\"\\s+");
		_Lines.removeAllRegexLines(patt);
	}

	public void addRecruitemntSlotBonus(String buildingName, String levelName, String castleOrCity, int recruitmentSlotBonus) throws PatcherLibBaseEx {

		String recruitmentSlotBonusStr = "\t\t\trecruitment_slots bonus "+recruitmentSlotBonus+" requires not event_counter freeze_recr_pool 1 ; Patcher Added";

		insertIntoBuildingCapabilities(buildingName, levelName, castleOrCity, recruitmentSlotBonusStr);
	}
	public void addPopulationGrowthBonus(String buildingName, String levelName, String castleOrCity, int populationGrowthBonus) throws PatcherLibBaseEx {

		String populationGrowthBonusStr = "\t\t\tpopulation_growth_bonus bonus "+Integer.toString(populationGrowthBonus)+" ; Patcher Added";

		insertIntoBuildingCapabilities(buildingName, levelName, castleOrCity, populationGrowthBonusStr);
	}
	// requirementsStr - without requirenemts label
	public void addPopulationGrowthBonus(String buildingName, String levelName, String castleOrCity, int populationGrowthBonus, String requirementsStr) throws PatcherLibBaseEx {

		String populationGrowthBonusStr = "\t\t\tpopulation_growth_bonus bonus "+Integer.toString(populationGrowthBonus);

		populationGrowthBonusStr += " requires " + requirementsStr;
		populationGrowthBonusStr += " ; Patcher Added";

		insertIntoBuildingCapabilities(buildingName, levelName, castleOrCity, populationGrowthBonusStr);
	}
	public void addLawBonus(String buildingName, String levelName, String castleOrCity, int lawBonus) throws PatcherLibBaseEx {

		String bonusStr = "\t\t\tlaw_bonus bonus "+Integer.toString(lawBonus)+" ; Patcher Added";

		insertIntoBuildingCapabilities(buildingName, levelName, castleOrCity, bonusStr);
	}


	public void insertRecruitmentBuildingCapabilities(String buildingName, String levelName, String castleOrCity,
													  String unitName, int starting, double replenish, int max, int bonus,
													  String requirements) throws PatcherLibBaseEx {

		if(requirements.contains("requires")) throw new PatcherLibBaseEx("Requirements contains 'requires' keyword!");

		String line = "\t\t\trecruit_pool    \"" + unitName +"\"  "+ starting +"   "+ replenish +"   "+ max +"  "+ bonus +"  requires "+ requirements +" ; TM Patcher Added";

		insertIntoBuildingCapabilities(buildingName, levelName, castleOrCity, line);
	}

	public void insertIntoBuildingCapabilities(String buildingName, String levelName, String castleOrCity, String newLine) throws PatcherLibBaseEx {

		LinesProcessor lines = _Lines;

		String levelRegex = "^\\s*"+levelName;
		if(castleOrCity != null && !castleOrCity.isEmpty())
			levelRegex += "\\s+"+castleOrCity;
		levelRegex += "\\s+requires\\s+factions\\s+";

		int capabilityIndex = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^building\\s+"+buildingName+"\\s*$",
						levelRegex,
						"^\\s*capability"), 0);
		if(capabilityIndex < 0) throw new PatcherLibBaseEx("Building not found: "+buildingName+" "+levelName+" "+castleOrCity);

		int capabilityBracekStart = capabilityIndex+1;

		int capabilityBracketEnd = lines.findExpFirstRegexLine("^\\s+}",capabilityBracekStart);

		int insertIndex = capabilityBracketEnd - 1;
		lines.insertAt(insertIndex, newLine);
	}

	public void addToWallsRecruitmentSlots(int addNmber) {
		val cityWallsList = Arrays.asList(
				new Tuple3<String, String, String>("core_building", "wooden_pallisade" , "city"),
				new Tuple3<String, String, String>("core_building", "wooden_wall" , "city"),
				new Tuple3<String, String, String>("core_building", "stone_wall" , "city"),
				new Tuple3<String, String, String>("core_building", "large_stone_wall" , "city"),
				new Tuple3<String, String, String>("core_building", "huge_stone_wall" , "city"),

				new Tuple3<String, String, String>("core_castle_building", "motte_and_bailey" , "castle"),
				new Tuple3<String, String, String>("core_castle_building", "wooden_castle" , "castle"),
				new Tuple3<String, String, String>("core_castle_building", "castle" , "castle"),
				new Tuple3<String, String, String>("core_castle_building", "fortress" , "castle"),
				new Tuple3<String, String, String>("core_castle_building", "citadel" , "castle"));

		// recruitment_slots 1 requires not event_counter freeze_recr_pool 1
		val ptr = Pattern.compile("(^\\s*recruitment_slots\\s+)(\\d+)(\\s+.*)");

		for(val cityWallData : cityWallsList) {
			val range = getBuildingCapabilitiesStartEnd(cityWallData.getItem1(), cityWallData.getItem2(), cityWallData.getItem3());

			for (int index = range.getStart(); index < range.getEnd(); index++) {
				String line = _Lines.getLines().get(index);

				val m = ptr.matcher(line);
				if(m.find()) {
					val prefix = m.group(1);
					val number = Integer.parseInt(m.group(2));
					val suffix = m.group(3);

					line = prefix + (number + addNmber) + suffix;
					_Lines.replaceLine(index, line);
				}
			}
		}
	}

	public Range<Integer, Integer> getBuildingCapabilitiesStartEnd(String buildingName, String levelName, String castleOrCity) {
		LinesProcessor lines = _Lines;

		String levelRegex = "^\\s*"+levelName;
		if(castleOrCity != null && !castleOrCity.isEmpty())
			levelRegex += "\\s+"+castleOrCity;
		levelRegex += "\\s+requires\\s+factions\\s+";

		int capabilityIndex = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^building\\s+"+buildingName+"\\s*$",
						levelRegex,
						"^\\s*capability"), 0);
		if(capabilityIndex < 0) throw new PatcherLibBaseEx("Building not found: "+buildingName+" "+levelName+" "+castleOrCity);

		int capabilityBracekStart = capabilityIndex+1;

		int capabilityBracketEnd = lines.findExpFirstRegexLine("^\\s+}",capabilityBracekStart);

		return new Range<Integer, Integer>(capabilityBracekStart, capabilityBracketEnd);
	}
	
	public int findBuildingCapabilityIndex(String buildingName, String levelName, String castleOrCity) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		String levelRegex = "^\\s*"+levelName;
		if(castleOrCity != null && !castleOrCity.isEmpty())
			levelRegex += "\\s+"+castleOrCity;
		levelRegex += "\\s+requires\\s+factions\\s+";

		int capabilityIndex = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^building\\s+"+buildingName+"\\s*$",
						levelRegex,
						"^\\s*capability"), 0);

		return  capabilityIndex;
	}


	public int findBuidlingSettlementRequirementLine(String buildingName, String levelName, String castleOrCity) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		String levelRegex = "^\\s*"+levelName;
		if(castleOrCity != null && !castleOrCity.isEmpty())
			levelRegex += "\\s+"+castleOrCity;
		levelRegex += "\\s+requires\\s+factions\\s+";

		int settlementRequirement = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^building\\s+"+buildingName+"\\s*$",
						levelRegex,
						"^\\s*settlement_min\\s+\\w+"
						), 0);

		return settlementRequirement;
	}

	public void setBuildingSettlementRequirement(String buildingName, String levelName, String castleOrCity, SettlementLevel level) throws PatcherLibBaseEx {
		setBuildingSettlementRequirement(buildingName, levelName, castleOrCity, getSettlementLevelStr(level));
	}
	public void setBuildingSettlementRequirement(String buildingName, String levelName, String castleOrCity, String settlementStr) throws PatcherLibBaseEx {

		int index = findBuidlingSettlementRequirementLine(buildingName, levelName, castleOrCity);
		if(index < 0) throw new PatcherLibBaseEx("Building not found: "+buildingName+" "+levelName+" "+castleOrCity);

		_Lines.replaceLine(index, "      settlement_min "+settlementStr);
	}

	public String getSettlementLevelStr(SettlementLevel level) throws PatcherLibBaseEx {

		return SettlementLevelConverter.toSettlementLevelStr(level);

//		String result;
//
//		switch (level) {
//
//			case L1_Village:
//				result="village";
//				break;
//			case L2_Town:
//				result="town";
//				break;
//			case L3_LargeTown:
//				result="large_town";
//				break;
//			case L4_City:
//				result="city";
//				break;
//			case L5_LargeCity:
//				result="large_city";
//				break;
//			case L6_HugeCity:
//				result="huge_city";
//				break;
//			default:
//				throw new PatcherLibBaseEx("Not implemented exception !");
//		}
//
//		return result;
	}

	public void insertIntoCityCastleWallsCapabilities(String capabilityLine) throws PatcherLibBaseEx {
		insertIntoCityWallsAllCapabilities(capabilityLine);
		insertIntoCastleWallsAllCapabilities(capabilityLine);
	}

	public void insertIntoCityWallsAllCapabilities(String capability) throws PatcherLibBaseEx {

		// ### CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wall

		insertIntoBuildingCapabilities("core_building", "wooden_pallisade" , "city", capability );
		insertIntoBuildingCapabilities("core_building", "wooden_wall" , "city", capability );
		insertIntoBuildingCapabilities("core_building", "stone_wall" , "city", capability );
		insertIntoBuildingCapabilities("core_building", "large_stone_wall" , "city", capability );
		insertIntoBuildingCapabilities("core_building", "huge_stone_wall" , "city", capability );
	}

	public void insertIntoCastleWallsAllCapabilities(String capability) throws PatcherLibBaseEx {
		// ### CASTLES ###
		// # Walls # - building core_castle_building
		// levels motte_and_bailey wooden_castle castle fortress citadel
		insertIntoBuildingCapabilities("core_castle_building", "motte_and_bailey" , "castle", capability );
		insertIntoBuildingCapabilities("core_castle_building", "wooden_castle" , "castle", capability );
		insertIntoBuildingCapabilities("core_castle_building", "castle" , "castle", capability );
		insertIntoBuildingCapabilities("core_castle_building", "fortress" , "castle", capability );
		insertIntoBuildingCapabilities("core_castle_building", "citadel" , "castle", capability );
	}

	public UnitRecuitmentInfo parseUnitRecruitmentInfo(String recruitPoolStringLine) throws PatcherLibBaseEx {

		//         recruit_pool  "Urban Spear Militia"  1   0.25   2  0  requires factions { pisa, venice, papal_states, sicily, }

		// ^\s*recruit_pool\s+"([\w\s]+)"\s+([\d\.]+)\s+([\d\.]+)\s+(\d+)\s+(\d+)\s+requires(.+)

		Matcher matcher = unitRecruitmentLinePatters.matcher(recruitPoolStringLine);

		UnitRecuitmentInfo result = new UnitRecuitmentInfo();

		if (matcher.find()) {

			result.Name = matcher.group(1);
			result.InitialReplenishCounter = Double.parseDouble(matcher.group(2));
			result.ReplenishRate = Double.parseDouble(matcher.group(3));
			result.MaxStack = Double.parseDouble(matcher.group(4));
			result.ExperienceBonus = Integer.parseInt(matcher.group(5));
			result.RequirementStr = matcher.group(6);
		}
		else throw new PatcherLibBaseEx("Unable to parse Building - Capabilities - Unit Recruitment Line - no match for regex !");

		return result;
	}

	public void updateUnitReplenishRates(String unitName , double relpenishRateMin , double replenishRateAddition) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		// ###### Loop throught all "recruit_pool ... " lines of building capabilities ######
		// recruit_pool  "Bondir"  1   0.13   1  0  requires factions { norway, denmark, } and hidden_resource denmark or hidden_resource norway
		Pattern regex = Pattern.compile("^\\s*recruit_pool\\s+\"" +  unitName + "\".+");

		int index = 0;
		while (index >= 0) {
			index = lines.findFirstByRegexLine(regex, index+1);
			if(index >= 0) {

				String lineOrg = lines.getLine(index);

				UnitRecuitmentInfo unitRecrInfo = parseUnitRecruitmentInfo(lineOrg);
				if(unitRecrInfo.MaxStack >= 1) { // only True entries
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

	protected static Pattern unitRecruitmentLinePatters = Pattern.compile("^\\s*recruit_pool\\s+\"([\\w\\s']+)\"\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+(\\d+)\\s+requires(.+)");

	public ExportDescrBuilding() {
		super("data\\export_descr_buildings.txt");
	}
}
