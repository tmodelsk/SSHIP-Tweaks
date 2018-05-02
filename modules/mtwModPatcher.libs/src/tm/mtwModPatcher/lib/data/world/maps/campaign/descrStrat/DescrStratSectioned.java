package tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat;

import lombok.val;
import org.xml.sax.SAXException;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.SectionsFileEntity;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.sections.SectionDocTextLines;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.sections.SectionTextLines;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;
import tm.mtwModPatcher.lib.common.entities.SettlementLevelConverter;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.buildings.BuildingSimple;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomek on 2016-04-18.
 */
public class DescrStratSectioned extends SectionsFileEntity {

	public SectionTextLines Header;
	public ResourcesSection Resources;
	public FactionsSection Factions;
	public SectionDocTextLines Diplomacy;
	public SectionDocTextLines Regions;

	public void setFactionAiLabel(String factionSymbol, String aiLabel) {
		val lines = Factions.content().lines();

		int index = lines.findFirstLineByLinePath(
				Arrays.asList(
						Ctm.format("^\\s*faction\\s+{0}", factionSymbol),
						"^ai_label\\s+\\s+"), 0);
		if (index < 0) throw new PatcherLibBaseEx("ai_label not found for faction " + factionSymbol);

		lines.replaceLine(index, Ctm.format("ai_label\t\t{0}", aiLabel));
	}

	public void setToCastle(String provinceName) {
		Factions.setToCastle(provinceName);
	}
	public void setFactionCreator(String provinceName, String factionSymbol) {
		Factions.setFactionCreator(provinceName, factionSymbol);
	}
	public void insertSettlementBuilding(String provinceName, String name, String level) throws PatcherLibBaseEx {
		Factions.insertSettlementBuilding(provinceName, name, level);
	}
	public void insertSettlementBuilding(String provinceName, BuildingSimple building) throws PatcherLibBaseEx {
		insertSettlementBuilding(provinceName, building.name, building.levelName);
	}

	public void setSettlementBuilding(String provinceName, BuildingSimple building) throws PatcherLibBaseEx {
		removeSettlementBuildingAnyLevel(provinceName, building);
		insertSettlementBuilding(provinceName, building.name, building.levelName);
	}


	public void removeSettlementBuilding(String provinceName, String buildingType, String buildingLevel) {
		Factions.removeSettlementBuilding(provinceName, buildingType, buildingLevel);
	}
	public void removeSettlementBuilding(String provinceName, String buildingType) {
		Factions.removeSettlementBuilding(provinceName, buildingType);
	}
	public void removeSettlementBuilding(String provinceName, BuildingSimple building) {
		removeSettlementBuilding(provinceName, building.name, building.levelName);
	}
	public void removeSettlementBuildingAnyLevel(String provinceName, BuildingSimple building) {
		removeSettlementBuilding(provinceName, building.name);
	}
	public void removeAllBuildings(String provinceName) {
		Factions.removeAllBuildings(provinceName);
	}

	// !! NOT TESTED !!
	protected void replaceInitialUnit(String factionName, String oldUnitName, String newUnitName, int experienceLevel) throws PatcherLibBaseEx {

		LinesProcessor lines = Factions.content().lines();

		int factionStartIndex = lines.findExpFirstRegexLine("^faction " + factionName);

		factionStartIndex = lines.findExpFirstRegexLine("^unit\\s+" + oldUnitName + ".+", factionStartIndex + 1);
		lines.replaceLine(factionStartIndex, "unit\t\t" + newUnitName + "\t\t\t\texp " + experienceLevel + " armour 0 weapon_lvl 0");
	}

	private static Set<String> _SettlementNames = null;

	public Set<String> getSettlementNames() throws PatcherLibBaseEx {

		if (_SettlementNames == null) {
			// Load, lazyloading

			_SettlementNames = new HashSet<>();

			LinesProcessor lines = Factions.content().lines();
			// 	region Angora_Province ;#132

			Pattern p = Pattern.compile("^\\s*region\\s+(\\S+)_Province.*");

			int index = lines.findFirstByRegexLine(p, 0);

			while (index >= 0) {
				String line = lines.getLine(index);

				// process
				Matcher m = p.matcher(line);
				if (m.matches()) {
					String settlementName = m.group(1);
					_SettlementNames.add(settlementName);
				} else throw new PatcherLibBaseEx("Provice not found " + line);

				index = lines.findFirstByRegexLine(p, index + 1);
			}
		}

		return _SettlementNames;
	}

	private static List<SettlementInfo> _SettlementInfoList;

	public List<SettlementInfo> getSettlementInfoList() throws PatcherLibBaseEx {
		if (_SettlementInfoList == null) {
			// lazyloading
			_SettlementInfoList = new ArrayList<>();

			LinesProcessor lines = Factions.content().lines();
			// 	region Angora_Province ;#132

			Pattern p = Pattern.compile("^\\s*region\\s+(\\S+).*");
			//Pattern p = Pattern.compile("^\\s*region\\s+(\\S+)_Province.*");

			int index = lines.findFirstByRegexLine(p, 0);

			while (index >= 0) {
				String line = lines.getLine(index);

				// process
				String provinceName;
				SettlementLevel level;

				Matcher m = p.matcher(line);
				if (m.matches())
					provinceName = m.group(1);
				else throw new PatcherLibBaseEx("Provice not found " + line);


				String levelLine = lines.getLine(index - 1);
				// 	level city
				Pattern levelPattern = Pattern.compile("^\\s*level\\s+(\\S+).*");
				Matcher levelMatcher = levelPattern.matcher(levelLine);
				if (levelMatcher.matches())
					level = SettlementLevelConverter.parse(levelMatcher.group(1));
				else throw new PatcherLibBaseEx("Level not found " + line + ", Level : " + levelLine);

				// 	faction_creator aragon
				String factionCreator;
				Pattern factionCreatorPattern = Pattern.compile("^\\s*faction_creator\\s+(\\S+)");
				String facionCreatorLine = lines.loadLineByFirstRegexLine(factionCreatorPattern, index + 1);
				Matcher factionCreatorMatcher = factionCreatorPattern.matcher(facionCreatorLine);
				if (factionCreatorMatcher.matches())
					factionCreator = factionCreatorMatcher.group(1);
				else throw new PatcherLibBaseEx("Province " + provinceName + " faction creator not found!");

				_SettlementInfoList.add(new SettlementInfo(null, provinceName, level, factionCreator));

				index = lines.findFirstByRegexLine(p, index + 1);
			}
		}
		return _SettlementInfoList;
	}

	public void setKingsPurse(String factionSymbol, int kingsPurse) throws PatcherLibBaseEx {
		Factions.setKingsPurse(factionSymbol, kingsPurse);
	}

	public void addKingsPurse(String factionSymbol, int kingsPurseAdd) throws PatcherLibBaseEx {
		Factions.addKingsPurse(factionSymbol, kingsPurseAdd);
	}

	public void addStartingTreasury(String factionSymbol, int treasuryBonus) throws PatcherLibBaseEx {
		Factions.addStartingTreasury(factionSymbol, treasuryBonus);
	}

	public void addKingsPursesAll(int kingsPurseAdd) throws PatcherLibBaseEx {
		Factions.addKingsPursesAll(kingsPurseAdd);
	}

	@Override
	public void load() throws ParserConfigurationException, IOException, SAXException, PatcherLibBaseEx {

		LinesProcessor lines = new LinesProcessor();
		lines.setLines(loadAsTextLines());

		// ## Find first section "Resources"
		int resourcesIndex = lines.findFirstByRexexLines("^;;;;;;+", "^; >>>> start of resources section <<<<");
		int factionsIndex = lines.findFirstByRexexLines("^;;;;;;+", "; >>>> start of factions section <<<<");
		int diplomacyIndex = lines.findFirstByRexexLines("^;;;;;;+", "; >>>> start of diplomacy section <<<<");
		int regionIndex = lines.findFirstByRexexLines("^;;;;;;+", "; >>>> start of regions section <<<<");

		Header = new SectionTextLines("Header");
		Header.setLines(lines.subsetCopy(0, resourcesIndex - 1));
		sections.add(Header);

		Resources = new ResourcesSection(lines.subsetCopy(resourcesIndex, resourcesIndex + 1) , lines.subsetCopy(resourcesIndex + 2, factionsIndex - 1));
		sections.add(Resources);

		Factions = new FactionsSection(lines.subsetCopy(factionsIndex, factionsIndex + 1) , lines.subsetCopy(factionsIndex + 2, diplomacyIndex - 1));
		sections.add(Factions);

		Diplomacy = new SectionDocTextLines();
		Diplomacy.setHeader(new SectionTextLines("DiplomacyHeader", lines.subsetCopy(diplomacyIndex, diplomacyIndex + 1)));
		Diplomacy.setContent(new SectionTextLines("DiplomacyContent", lines.subsetCopy(diplomacyIndex + 2, regionIndex - 1)));
		sections.add(Diplomacy);

		Regions = new SectionDocTextLines();
		Regions.setHeader(new SectionTextLines("RegionsHeader", lines.subsetCopy(regionIndex, regionIndex + 1)));
		Regions.setContent(new SectionTextLines("RegionsContent", lines.subsetCopy(regionIndex + 2)));
		sections.add(Regions);
	}

	public DescrStratSectioned() {

		super("data\\world\\maps\\campaign\\imperial_campaign\\Descr_strat.txt");
		sections = new ArrayList<>();
	}
}
