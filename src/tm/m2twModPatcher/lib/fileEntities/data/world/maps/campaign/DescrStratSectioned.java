package tm.m2twModPatcher.lib.fileEntities.data.world.maps.campaign;

import lombok.val;
import org.xml.sax.SAXException;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.SectionsFileEntity;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.sections.SectionDocTextLines;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.sections.SectionTextLines;
import tm.m2twModPatcher.lib.common.entities.SettlementInfo;
import tm.m2twModPatcher.lib.common.entities.SettlementLevel;
import tm.m2twModPatcher.lib.common.entities.SettlementLevelConverter;

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
	public SectionDocTextLines Resources;
	public SectionDocTextLines Factions;
	public SectionDocTextLines Diplomacy;
	public SectionDocTextLines Regions;

	// !! NOT TESTED !!
	protected void replaceInitialUnit(String factionName, String oldUnitName , String newUnitName, int experienceLevel) throws PatcherLibBaseEx {

		LinesProcessor lines = Factions.getContent().getLines();

		int factionStartIndex = lines.findExpFirstRegexLine("^faction "+factionName);

		factionStartIndex = lines.findExpFirstRegexLine("^unit\\s+"+ oldUnitName + ".+" , factionStartIndex+1);
		lines.replaceLine(factionStartIndex , "unit\t\t" + newUnitName + "\t\t\t\texp " + experienceLevel + " armour 0 weapon_lvl 0");
	}

	public void insertSettlementBuilding(String provinceName, String name, String level) throws PatcherLibBaseEx {
		val endIndex = loadSettlemenBlockEndIndex(provinceName);

		val rl = new ArrayList<String>();

		rl.add("	building");
		rl.add("	{");
		rl.add("		type "+ name +" "+level);
		rl.add("	}");

		val lines = Factions.getContent().getLines();
		lines.insertAt(endIndex, rl);
	}
	public int loadSettlementBuildingIndex(String provinceName) throws PatcherLibBaseEx {
		val lines = Factions.getContent().getLines();

		int pamlponaFirstBuildingTag = lines.findFirstLineByLinePath(
				Arrays.asList(	"^\\s*region\\s+" + provinceName,
						"^\\s*building"));
		if(pamlponaFirstBuildingTag < 0) throw new PatcherLibBaseEx("DescrStrat / Factions / Settlement Province "+ provinceName +" building not found !");

		return pamlponaFirstBuildingTag;
	}
	public int loadSettlemenBlockEndIndex(String provinceName) throws PatcherLibBaseEx {
		val buildingIndex = loadSettlementBuildingIndex(provinceName);

		int openBracketsCount = 0, endIndex = -1, i=buildingIndex+1;
		String actLine="";
		val lines = Factions.getContent().getLines();

		while (endIndex < 0) {
			actLine = lines.getLine(i).trim();

			if(actLine.equals("{")) openBracketsCount++;
			else {
				if(actLine.equals("}")) {
					if(openBracketsCount > 0) openBracketsCount--;
					else {
						// ## FOUND END BRACKER !! ##
						endIndex = i;
					}
				}
			}
			i++;
		}

		return endIndex;
	}

	private static Set<String> _SettlementNames = null;
	public Set<String> getSettlementNames() throws PatcherLibBaseEx {

		if(_SettlementNames == null) {
			// Load, lazyloading

			_SettlementNames = new HashSet<>();

			LinesProcessor lines = Factions.getContent().getLines();
			// 	region Angora_Province ;#132

			Pattern p = Pattern.compile("^\\s*region\\s+(\\S+)_Province.*");

			int index = lines.findFirstByRegexLine(p, 0);

			while (index >= 0) {
				String line = lines.getLine(index);

				// process
				Matcher m = p.matcher(line);
				if(m.matches())
				{
					String settlementName = m.group(1);
					_SettlementNames.add(settlementName);
				}
				else throw new PatcherLibBaseEx("Provice not found "+line);

				index = lines.findFirstByRegexLine(p, index+1);
			}
		}

		return  _SettlementNames;
	}

	private static List<SettlementInfo> _SettlementInfoList;
	public List<SettlementInfo> getSettlementInfoList() throws PatcherLibBaseEx {
		if(_SettlementInfoList == null) {
			// lazyloading
			_SettlementInfoList = new ArrayList<>();

			LinesProcessor lines = Factions.getContent().getLines();
			// 	region Angora_Province ;#132

			Pattern p = Pattern.compile("^\\s*region\\s+(\\S+).*");
			//Pattern p = Pattern.compile("^\\s*region\\s+(\\S+)_Province.*");

			int index = lines.findFirstByRegexLine(p, 0);

			while (index >= 0) {
				String line = lines.getLine(index);

				if(line.contains("Reggio"))
				{
					String xx = "break";
				}

				// process
				String provinceName;
				SettlementLevel level;

				Matcher m = p.matcher(line);
				if(m.matches())
					provinceName = m.group(1);
				else throw new PatcherLibBaseEx("Provice not found "+line);



				String levelLine = lines.getLine(index-1);
				// 	level city
				Pattern levelPattern = Pattern.compile("^\\s*level\\s+(\\S+).*");
				Matcher levelMatcher = levelPattern.matcher(levelLine);
				if(levelMatcher.matches())
					level = SettlementLevelConverter.parse(levelMatcher.group(1));
				else throw new PatcherLibBaseEx("Level not found "+line + ", Level : " + levelLine);

				// 	faction_creator aragon
				String factionCreator;
				Pattern factionCreatorPattern = Pattern.compile("^\\s*faction_creator\\s+(\\S+)");
				String facionCreatorLine  = lines.loadLineByFirstRegexLine(factionCreatorPattern, index+1);
				Matcher factionCreatorMatcher = factionCreatorPattern.matcher(facionCreatorLine);
				if(factionCreatorMatcher.matches())
					factionCreator = factionCreatorMatcher.group(1);
				else throw new PatcherLibBaseEx("Province "+provinceName+" faction creator not found!");

				_SettlementInfoList.add(new SettlementInfo(null, provinceName, level, factionCreator));

				index = lines.findFirstByRegexLine(p, index+1);
			}
		}
		return _SettlementInfoList;
	}

	public void setKingsPurse(String factionSymbol, int kingsPurse) throws PatcherLibBaseEx {

		LinesProcessor lines = Factions.getContent().getLines();

		int factionKingPurse = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^;## "+factionSymbol.toUpperCase()+" ##",
						"^denari_kings_purse\\s+\\d+"), 0);
		if(factionKingPurse < 0) throw new PatcherLibBaseEx("Kings Purse not found for faction "+factionSymbol);

		lines.replaceLine(factionKingPurse, "denari_kings_purse\t"+Integer.toString(kingsPurse));
	}

	public void addKingsPurse(String factionSymbol, int kingsPurseAdd) throws PatcherLibBaseEx {

		LinesProcessor lines = Factions.getContent().getLines();

		int factionKingPurse = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^;## "+factionSymbol.toUpperCase()+" ##",
						"^denari_kings_purse\\s+\\d+"), 0);
		if(factionKingPurse < 0) throw new PatcherLibBaseEx("Kings Purse not found for faction "+factionSymbol);

		String lineOrg = lines.getLine(factionKingPurse);
		Pattern regex = Pattern.compile("^denari_kings_purse\\s+(\\d+)\\s*");
		Matcher matcher = regex.matcher(lineOrg);

		if(matcher.matches()) {
			int kingPurseOld = Integer.parseInt(matcher.group(1));

			lines.replaceLine(factionKingPurse, "denari_kings_purse\t"+(kingPurseOld + kingsPurseAdd));
		}
		else
			throw new PatcherLibBaseEx("Unexpected - no match for king purse "+lineOrg);
	}

	public void addKingsPursesAll(int kingsPurseAdd) throws PatcherLibBaseEx {

		LinesProcessor lines = Factions.getContent().getLines();
		Pattern regex = Pattern.compile("(^denari_kings_purse\\s+)(\\d+)\\s*");

		int factionKingPurse = 0;

		while(factionKingPurse >= 0) {

			factionKingPurse = lines.findFirstByRegexLine(regex, factionKingPurse);
			if(factionKingPurse < 0) break;

			String lineOrg = lines.getLine(factionKingPurse);
			Matcher matcher = regex.matcher(lineOrg);

			if(matcher.matches()) {
				int kingPurseOld = Integer.parseInt(matcher.group(2));

				lines.replaceLine(factionKingPurse, "denari_kings_purse\t"+(kingPurseOld + kingsPurseAdd));
				factionKingPurse++;
			}
			else
				throw new PatcherLibBaseEx("Unexpected : no match for denari_kongs_purse Line : "+factionKingPurse+" "+lineOrg);
		}
	}

	@Override
	public void load() throws ParserConfigurationException, IOException, SAXException, PatcherLibBaseEx {

		LinesProcessor lines = new LinesProcessor();
		lines.setLines(	loadAsTextLines() );

		// ## Find first section "Resources"
		int resourcesIndex = lines.findFirstByRexexLines("^;;;;;;+", "^; >>>> start of resources section <<<<");
		int factionsIndex = lines.findFirstByRexexLines("^;;;;;;+", "; >>>> start of factions section <<<<");
		int diplomacyIndex = lines.findFirstByRexexLines("^;;;;;;+", "; >>>> start of diplomacy section <<<<");
		int regionIndex = lines.findFirstByRexexLines("^;;;;;;+", "; >>>> start of regions section <<<<");

		Header = new SectionTextLines("Header");
		Header.setLines( lines.subsetCopy(0, resourcesIndex-1) );
		_Sections.add(Header);

		Resources = new SectionDocTextLines();
		Resources.setHeader( new SectionTextLines("ResourcesHeader",lines.subsetCopy(resourcesIndex, resourcesIndex+1)));
		Resources.setContent(new SectionTextLines("ResourcesContent", lines.subsetCopy(resourcesIndex+2, factionsIndex-1)));
		_Sections.add(Resources);

		Factions = new SectionDocTextLines();
		Factions.setHeader(new SectionTextLines("FactionsHeader", lines.subsetCopy(factionsIndex, factionsIndex+1)));
		Factions.setContent(new SectionTextLines("FactionContent", lines.subsetCopy(factionsIndex+2, diplomacyIndex-1)));
		_Sections.add(Factions);

		Diplomacy = new SectionDocTextLines();
		Diplomacy.setHeader(new SectionTextLines("DiplomacyHeader", lines.subsetCopy(diplomacyIndex, diplomacyIndex+1)));
		Diplomacy.setContent(new SectionTextLines("DiplomacyContent", lines.subsetCopy(diplomacyIndex+2, regionIndex-1)));
		_Sections.add(Diplomacy);

		Regions = new SectionDocTextLines();
		Regions.setHeader(new SectionTextLines("RegionsHeader", lines.subsetCopy(regionIndex, regionIndex+1)));
		Regions.setContent(new SectionTextLines("RegionsContent", lines.subsetCopy(regionIndex+2)));
		_Sections.add(Regions);
	}

	public DescrStratSectioned()  {

		super("data\\world\\maps\\campaign\\imperial_campaign\\Descr_strat.txt");
		_Sections = new ArrayList<>();
	}
}
