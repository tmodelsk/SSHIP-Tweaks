package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;
import tm.common.Ctm;
import tm.common.Range;
import tm.common.Tuple2;
import tm.common.Tuple3;
import tm.common.collections.CollectionUtils;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;
import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;
import tm.mtwModPatcher.lib.common.entities.SettlementLevelConverter;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingLevel;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.SettlType;
import tm.mtwModPatcher.lib.data.exportDescrUnit.UnitDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExportDescrBuilding extends LinesProcessorFileEntity {

	public void addHiddenResourceDef(String hiddenResourceName) {
		val lines = getLines();
		val index = lines.findExpFirstRegexLine("^\\s*hidden_resources\\s+");
		val orgLineStr = lines.getLine(index);
		val newLine = orgLineStr+ " " + hiddenResourceName;

		val splitted = newLine.split(" ");
		val hiddenResCount = splitted.length-1;	// hidden_resources tag is first
		if(hiddenResCount > 64)	throw new PatcherLibBaseEx(Ctm.format("Too many ({0}) hidden resources!", hiddenResCount));

		lines.replaceLine(index, newLine );
	}

	public List<Tuple2<String, List<Integer>>> addFlatCityCastleIncome(String requires, double factor) throws PatcherLibBaseEx {
		String attributeStr = "       income_bonus bonus ";
		// 250 , 375 , 562 , 843 , 1264

		val result = new ArrayList<Tuple2<String, List<Integer>>>();
		int base = 250;
		double multi = 1.5;

		base = (int) (base * factor);
		int baseTmp = base;
		val cityBonuses = new ArrayList<Integer>();

		addCapabilities("core_building", "wooden_pallisade" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_building", "wooden_wall" , "city", attributeStr+ baseTmp + requires);
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_building", "stone_wall" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_building", "large_stone_wall" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_building", "huge_stone_wall" , "city", attributeStr+ baseTmp + requires );
		cityBonuses.add(baseTmp);

		result.add(new Tuple2<>("City", cityBonuses));

		// # Castles #
		baseTmp = base;
		val castleBonuses = new ArrayList<Integer>();
		addCapabilities("core_castle_building", "motte_and_bailey" , "castle", attributeStr+ baseTmp + requires );
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_castle_building", "wooden_castle" , "castle", attributeStr+ baseTmp  + requires);
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_castle_building", "castle" , "castle", attributeStr+ baseTmp + requires );
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_castle_building", "fortress" , "castle", attributeStr+ baseTmp + requires );
		castleBonuses.add(baseTmp);
		baseTmp *= multi;

		addCapabilities("core_castle_building", "citadel" , "castle", attributeStr+ baseTmp + requires );
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
	public void removeUnitRecruitment(UnitDef unit) {
		removeUnitRecruitment(unit.Name);
	}
	public void removeUnitRecruitment(String unitName) {
		 //        recruit_pool  "EE Peasants"  2   0.077   2  0  requires factions { russia, kievan_rus, hungary, lithuania, }
		val patt = Pattern.compile("^\\s*recruit_pool\\s+\"" +unitName+ "\"\\s+");
		_Lines.removeAllRegexLines(patt);
	}
	public void removeUnitRecruitment(String unitName, FactionInfo faction) {
		removeUnitRecruitment(unitName, faction.symbol);
	}
	public void removeUnitRecruitment(String unitName, List<FactionInfo> factions) {
		factions.forEach( f  -> removeUnitRecruitment(unitName, f));
	}
	public void removeUnitRecruitment(String unitName, String faction) {
		val patt = Pattern.compile("^\\s*recruit_pool\\s+\"" +unitName+ "\"\\s+");

		val unitReqruitments = findRecruitmentsByRegex(patt);
		for (val unitRecr : unitReqruitments) {
			val require = unitRecr.getUnitRequireSimple();

			if(require != null && require.Factions.contains(faction)) {

				val unitRecrNew = unitRecr.clone();
				val requireNew = unitRecrNew.getUnitRequireSimple();
				requireNew.Factions.remove(faction);
				unitRecrNew.setRequirementStr(requireNew);

				getLines().replaceLine(unitRecrNew.lineNumber , unitRecrNew.toRecruitmentPoolLine());
			}
		}
	}

	public void addToUnitRecruitment(String unitName, FactionInfo faction, List<FactionInfo> requiredFactions) {
		val patt = Pattern.compile("^\\s*recruit_pool\\s+\"" +unitName+ "\"\\s+");
		val requiredFactionsStr = requiredFactions.stream().map(fi -> fi.symbol).collect(Collectors.toList());

		val unitReqruitments = findRecruitmentsByRegex(patt);
		for (val unitRecr : unitReqruitments) {
			val require = unitRecr.getUnitRequireSimple();

			if(require != null && !require.Factions.contains(faction)
					&& CollectionUtils.isAllFirstElementsAreInSecond(requiredFactionsStr, require.Factions)) {

				val unitRecrNew = unitRecr.clone();
				val requireNew = unitRecrNew.getUnitRequireSimple();
				requireNew.Factions.add(faction.symbol);
				unitRecrNew.setRequirementStr(requireNew);

				getLines().replaceLine(unitRecrNew.lineNumber , unitRecrNew.toRecruitmentPoolLine());
			}
		}
	}
	public void addToUnitRecruitment(String unitName, FactionInfo faction, FactionInfo requiredFaction) {
		addToUnitRecruitment(unitName, faction, nullStr, requiredFaction);
	}
	public void addToUnitRecruitment(String unitName, FactionInfo faction, String buildingName, FactionInfo requiredFaction) {

		addToUnitRecruitment(unitName, faction, buildingName, nullStr, requiredFaction);
	}
	public void addToUnitRecruitment(String unitName, FactionInfo faction, String buildingName, String additionalHiddenResource, FactionInfo requiredFaction) {

		List addHiddenResources = additionalHiddenResource != null ? Arrays.asList(additionalHiddenResource) : null;

		addToUnitRecruitment(unitName, faction, buildingName, addHiddenResources, Arrays.asList(requiredFaction));
	}
	public void addToUnitRecruitment(String unitName, FactionInfo faction, List<String> additionalHiddenResources, FactionInfo requiredFaction) {
		addToUnitRecruitment(unitName, faction, null, additionalHiddenResources, requiredFaction);
	}
	public void addToUnitRecruitment(String unitName, FactionInfo faction, String buildingName, List<String> additionalHiddenResources, FactionInfo requiredFaction) {
		addToUnitRecruitment(unitName, faction, buildingName, additionalHiddenResources, Arrays.asList(requiredFaction));
	}
	public void addToUnitRecruitment(String unitName, FactionInfo faction, String buildingName, List<String> additionalHiddenResources, List<FactionInfo> requiredFactions) {
		val patt = Pattern.compile("^\\s*recruit_pool\\s+\"" +unitName+ "\"\\s+");
		val requiredFactionsStr = requiredFactions.stream().map(fi -> fi.symbol).collect(Collectors.toList());

		val unitReqruitments = buildingName != null ? findRecruitmentsByRegex(patt, buildingName) : findRecruitmentsByRegex(patt);
		Pattern notHiddenResourceRegex = Pattern.compile("not\\s+hidden_resource");
		for (val unitRecr : unitReqruitments) {
			val require = unitRecr.getUnitRequireSimple();

			if(require != null && !require.Factions.contains(faction.symbol)
					&& CollectionUtils.isAllFirstElementsAreInSecond(requiredFactionsStr, require.Factions)) {

				val unitRecrNew = unitRecr.clone();
				val requireNew = unitRecrNew.getUnitRequireSimple();

				requireNew.Factions.add(faction.symbol);

				if(additionalHiddenResources != null) {
					String hrPrefix, globalPrefix="", secondHrPrefix = "";
					val restConditions = requireNew.RestConditions != null ? requireNew.RestConditions : "";

					if( notHiddenResourceRegex.matcher(restConditions).find())
						hrPrefix = " and not hidden_resource ";
					else if( restConditions.contains("hidden_resource") )
						hrPrefix = " or hidden_resource ";
					else {
						globalPrefix = " and ";
						secondHrPrefix = " or ";
						hrPrefix = " hidden_resource ";
					}

					String newRestCond = restConditions;
					newRestCond += globalPrefix;
					boolean isSecond = false;
					for(String hiddenRes : additionalHiddenResources) {
						if(isSecond) newRestCond += secondHrPrefix;
						newRestCond += hrPrefix + hiddenRes;
						isSecond = true;
					}

					requireNew.RestConditions = newRestCond;
				}

				unitRecrNew.setRequirementStr(requireNew);

				getLines().replaceLine(unitRecrNew.lineNumber , unitRecrNew.toRecruitmentPoolLine());
			}
		}
	}

	public void addRecruitemntSlotBonus(String buildingName, String levelName, String castleOrCity, int recruitmentSlotBonus) throws PatcherLibBaseEx {

		String recruitmentSlotBonusStr = "\t\t\trecruitment_slots bonus "+recruitmentSlotBonus+" requires not event_counter freeze_recr_pool 1 ; Patcher Added";

		addCapabilities(buildingName, levelName, castleOrCity, recruitmentSlotBonusStr);
	}
	public void addPopulationGrowthBonus(String buildingName, String levelName, String castleOrCity, int populationGrowthBonus) throws PatcherLibBaseEx {

		String populationGrowthBonusStr = "\t\t\tpopulation_growth_bonus bonus "+Integer.toString(populationGrowthBonus)+" ; Patcher Added";

		addCapabilities(buildingName, levelName, castleOrCity, populationGrowthBonusStr);
	}
	/**  requirementsStr - without requirements label */
	public void addPopulationGrowthBonus(String buildingName, String levelName, String castleOrCity, int populationGrowthBonus, String requirementsStr) throws PatcherLibBaseEx {

		String populationGrowthBonusStr = "\t\t\tpopulation_growth_bonus bonus "+Integer.toString(populationGrowthBonus);

		populationGrowthBonusStr += " requires " + requirementsStr;
		populationGrowthBonusStr += " ; Patcher Added";

		addCapabilities(buildingName, levelName, castleOrCity, populationGrowthBonusStr);
	}
	public void addLawBonus(String buildingName, String levelName, String castleOrCity, int lawBonus) throws PatcherLibBaseEx {

		String bonusStr = "\t\t\tlaw_bonus bonus "+Integer.toString(lawBonus)+" ; Patcher Added";

		addCapabilities(buildingName, levelName, castleOrCity, bonusStr);
	}


	public void addRecuitment(BuildingLevel buidlingLevel, String unitName, int starting, double replenish, int max, int bonus,
							  String requirements) throws PatcherLibBaseEx {
		addRecuitment(buidlingLevel.Name, buidlingLevel.LevelName, buidlingLevel.SettlType.toLabelString(),
				unitName, starting, replenish, max, bonus, requirements);
	}

	public void addRecuitment(String buildingName, String levelName, String castleOrCity,
							  String unitName, int starting, double replenish, int max, int bonus,
							  String requirements) throws PatcherLibBaseEx {

		if(requirements.contains("requires")) throw new PatcherLibBaseEx("Requirements contains 'requires' keyword!");

		String line = "\t\t\trecruit_pool    \"" + unitName +"\"  "+ starting +"   "+ replenish +"   "+ max +"  "+ bonus +"  requires "+ requirements +" ; TM Patcher Added";

		addCapabilities(buildingName, levelName, castleOrCity, line);
	}

	public void addCapabilitiesAllLevels(BuildingLevel buidlingLevel, String newLine) {
		buidlingLevel.levels().forEach( bl -> addCapabilities(bl, newLine));
	}
	public void addCapabilities(BuildingLevel buidlingLevel, String newLine) {
		addCapabilities(buidlingLevel.Name, buidlingLevel.LevelName, buidlingLevel.SettlType, newLine);
	}
	public void addCapabilities(String buildingName, String levelName, SettlType settlType, String newLine) {
		addCapabilities(buildingName, levelName, settlType.toLabelString(), newLine);
	}

	public void addCapabilities(String buildingName, String levelName, String newLine) {
		String castleOrCity = null;
		addCapabilities(buildingName, levelName, castleOrCity, newLine);
	}

	public void addCapabilities(String buildingName, String levelName, String castleOrCity, String newLine) throws PatcherLibBaseEx {

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

		int insertIndex = capabilityBracketEnd;


		String lineCorrected = "        " + newLine.trim();

		lines.insertAt(insertIndex, lineCorrected);
	}


	public void addCapabilities(String buildingName, List<String> levels, SettlType settlType, String newLine) {
		addCapabilities(buildingName, levels, settlType.toLabelString(), newLine);
	}
	public void addCapabilities(String buildingName, List<String> levels, String castleOrCity, String newLine) {
		for (val level: levels)
			addCapabilities(buildingName, level, castleOrCity, newLine);
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
			val range = getCapabilitiesStartEnd(cityWallData.getItem1(), cityWallData.getItem2(), cityWallData.getItem3());

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

	public Range<Integer, Integer> getCapabilitiesStartEnd(BuildingLevel bl) {
		String cityOrCastle = bl.SettlType == null ? null : bl.SettlType.toLabelString();
		return getCapabilitiesStartEnd(bl.Name, bl.LevelName, cityOrCastle);
	}
	public Range<Integer, Integer> getCapabilitiesStartEnd(String buildingName, String levelName, String castleOrCity) {
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

	public int findBuidlingRequiresLine(String buildingName, String levelName, String castleOrCity) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		String levelRegex = "^\\s*"+levelName;
		if(castleOrCity != null && !castleOrCity.isEmpty())
			levelRegex += "\\s+"+castleOrCity;
		levelRegex += "\\s+requires\\s+factions\\s+";

		int settlementRequirement = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^building\\s+"+buildingName+"\\s*$",
						levelRegex
				), 0);

		return settlementRequirement;
	}
	public int findExpBuidlingStart(String buildingName) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		int settlementRequirement = lines.findExpFirstRegexLine("^building\\s+"+buildingName+"\\s*$");

		return settlementRequirement;
	}
	public Range<Integer, Integer> findExpBuidlingRange(String buildingName) throws PatcherLibBaseEx {
		LinesProcessor lines = _Lines;

		int settlStartIndex = lines.findExpFirstRegexLine("^building\\s+"+buildingName+"\\s*$");
		int endIndex = lines.findCurrentBlockEndBracket(settlStartIndex+2);

		return new Range<>(settlStartIndex, endIndex);
	}

	public void addBuildingRequirement(String buildingName, String levelName, String castleOrCity, String additionalRequirement) {
		val index = findBuidlingRequiresLine(buildingName, levelName, castleOrCity);
		if(index <=0 ) throw new PatcherLibBaseEx(Ctm.format("Builidng ({0},{1},{2}) not found", buildingName, levelName, castleOrCity));

		val orgLine = getLines().getLine(index);

		getLines().replaceLine(index, orgLine + " " + additionalRequirement);
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

	public void setBuildingSettlementRequirement(String buildingName, String levelName, SettlType settlType, SettlementLevel level) {
		setBuildingSettlementRequirement(buildingName, levelName, settlType.toLabelString(), level);
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
	}

	public void addToCityCastleWallsCapabilities(String capabilityLine) throws PatcherLibBaseEx {
		addToCityWallsAllCapabilities(capabilityLine);
		addToCastleWallsAllCapabilities(capabilityLine);
	}

	public void addToCityWallsAllCapabilities(String capability) throws PatcherLibBaseEx {

		// ### CITIES ### : wooden_pallisade wooden_wall stone_wall large_stone_wall huge_stone_wall

		addCapabilities("core_building", "wooden_pallisade" , "city", capability );
		addCapabilities("core_building", "wooden_wall" , "city", capability );
		addCapabilities("core_building", "stone_wall" , "city", capability );
		addCapabilities("core_building", "large_stone_wall" , "city", capability );
		addCapabilities("core_building", "huge_stone_wall" , "city", capability );
	}

	public void addToCastleWallsAllCapabilities(String capability) throws PatcherLibBaseEx {
		// ### CASTLES ###
		// # Walls # - building core_castle_building
		// levels motte_and_bailey wooden_castle castle fortress citadel
		addCapabilities("core_castle_building", "motte_and_bailey" , "castle", capability );
		addCapabilities("core_castle_building", "wooden_castle" , "castle", capability );
		addCapabilities("core_castle_building", "castle" , "castle", capability );
		addCapabilities("core_castle_building", "fortress" , "castle", capability );
		addCapabilities("core_castle_building", "citadel" , "castle", capability );
	}

	public UnitRecuitmentInfo parseUnitRecruitmentInfo(String recruitPoolStringLine) throws PatcherLibBaseEx {
		return UnitRecuitmentInfo.parseUnitRecruitmentInfo(recruitPoolStringLine);
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

	public List<UnitRecuitmentLineInfo> findRecruitmentsByRegex(Pattern regexPattern) {
		List<UnitRecuitmentLineInfo> res = new ArrayList<>();

		val lines = getLines();
		int index = 0;
		do {
			index = lines.findFirstByRegexLine(regexPattern, index+1);
			if(index < 0) break;

			val lineStr = lines.getLine(index);
			val unitRecr = parseUnitRecruitmentInfo(lineStr);
			val unitRecrLine = new UnitRecuitmentLineInfo(unitRecr, index);
			res.add(unitRecrLine);
		}
		while(index > 0);

		return res;
	}
	public List<UnitRecuitmentLineInfo> findRecruitmentsByRegex(Pattern regexPattern, String buildingName) {
		List<UnitRecuitmentLineInfo> res = new ArrayList<>();

		val lines = getLines();
		val buildingRange = findExpBuidlingRange(buildingName);
		val endIndex = buildingRange.getEnd();
		int index = buildingRange.getStart();
		do {
			index = lines.findFirstRegexLine(regexPattern, index+1, endIndex);
			if(index < 0) break;

			val lineStr = lines.getLine(index);
			val unitRecr = parseUnitRecruitmentInfo(lineStr);
			val unitRecrLine = new UnitRecuitmentLineInfo(unitRecr, index);
			res.add(unitRecrLine);
		}
		while(index > 0);

		return res;
	}

	public int countBuildings() {
		val pattern = Pattern.compile("^\\s*levels\\s+(.*)");
		int index = 0;
		int counter = 0;
		
		return counter;
	}

	public static List<UnitRecuitmentInfo> toUnitRecuitmentInfo(List<UnitRecuitmentLineInfo> recruitments) {
		return recruitments.stream().map( urli -> (UnitRecuitmentInfo)urli).collect(Collectors.toList());
	}

	public void removeAgentRecruitment(String agentType) {
		val lines = getLines();
		//         agent_limit merchant 1
		lines.removeAllRegexLines(Ctm.format("^\\s*agent_limit\\s+{0}\\s+", agentType));

		// ## Remove all merchants production capabilities: agent merchant  0  requires ....
		lines.removeAllRegexLines(Ctm.format("^\\s*agent\\s+{0}\\s", agentType));
	}

	public ExportDescrBuilding() {
		super("data\\export_descr_buildings.txt");
	}

	public final static String TradeBonus = "trade_base_income_bonus bonus ";
	public final static String IncomeBonus = "income_bonus bonus ";
	public final static String LawBonus = "law_bonus bonus ";
	public final static String ReligionBonus = "religion_level bonus ";

	public static final String AGENT_PRIEST = "priest";

	public final static String HiddenResource = "hidden_resource ";
	private final static String nullStr = null;

}
