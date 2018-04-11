package tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat;

import lombok.val;
import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LineNotFoundEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.sections.SectionDocTextLines;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.sections.SectionTextLines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FactionsSection extends SectionDocTextLines {

	public void insertSettlementBuilding(String provinceName, String name, String level) throws PatcherLibBaseEx {
		val endIndex = loadSettlemenBlockEndIndex(provinceName);

		val rl = new ArrayList<String>();

		rl.add("	building");
		rl.add("	{");
		rl.add("		type " + name + " " + level);
		rl.add("	}");

		val lines = getContent().getLines();
		lines.insertAt(endIndex, rl);
	}

	public boolean removeSettlementBuilding(String provinceName, String buildingType, String buildingLevel) {
		boolean removed = false;

		val index = getSettlementBuildingLine(provinceName, buildingType, buildingLevel);
		if(index > 0) {
			val lines = getContent().getLines();

			lines.removeRange(index-2, index+1);
		}

		return removed;
	}

	public void removeAllBuildings(String provinceName) {
		val lines = getContent().getLines();

		val firstBuildingIndex = loadSettlementFirstBuildingIndex(provinceName);
		val endBuildingIndex = loadSettlemenBlockEndIndex(provinceName);

		val lineFirstBuildingIndex = lines.getLine(firstBuildingIndex);
		val lineEndBuildingIndex = lines.getLine(endBuildingIndex);

		val x = lineFirstBuildingIndex + lineEndBuildingIndex;

		lines.removeRange(firstBuildingIndex, endBuildingIndex-1);

		//descrStrat.

//		building
//		{
//			type core_castle_building wooden_castle
//		}
	}

	public void addBuilding(String provinceName, String buildingType, String buildingLevel) {
		val insertIndex = loadSettlemenBlockEndIndex(provinceName);

		//val lines =
	}
	public void setToCastle(String provinceName) {
		val lines = getContent().getLines();
		val regionIndex = lines.findExpFirstRegexLine("^\\s*region\\s+" + provinceName);

		val settlementIndex = regionIndex - 3;
		lines.replaceLine(settlementIndex, "settlement castle");
	}
	public void setFactionCreator(String provinceName, String factionSymbol) {
		val lines = getContent().getLines();

		int factionCreatorIndex = lines.findExpFirstRegexLine(
				"^\\s*region\\s+" + provinceName,"^\\s*faction_creator");

		lines.replaceLine(factionCreatorIndex, "\tfaction_creator "+factionSymbol);
	}

	public int loadSettlementFirstBuildingIndex(String provinceName) {
		val lines = getContent().getLines();

		int firstBuildingTag = lines.findFirstLineByLinePath(
				Arrays.asList("^\\s*region\\s+" + provinceName,
						"^\\s*building"));
		if (firstBuildingTag < 0)
			throw new LineNotFoundEx("DescrStrat / Factions / Settlement Province " + provinceName + " building not found !");

		return firstBuildingTag;
	}

	public int loadSettlemenBlockEndIndex(String provinceName) throws PatcherLibBaseEx {
		val lines = getContent().getLines();

		val provinceHeaderEndIndex = lines.findFirstLineByLinePath(
				Arrays.asList("^\\s*region\\s+" + provinceName,
						"^\\s*faction_creator\\s+\\w+"));

		int openBracketsCount = 0, endIndex = -1, i = provinceHeaderEndIndex + 1;
		String actLine = "";


		while (endIndex < 0) {
			actLine = lines.getLine(i).trim();

			if (actLine.equals("{")) openBracketsCount++;
			else {
				if (actLine.equals("}")) {
					if (openBracketsCount > 0) openBracketsCount--;
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

	public int getSettlementBuildingLine(String provinceName, String buildingType, String buildingLevel) {
		val start = loadSettlementFirstBuildingIndex(provinceName);
		val end = loadSettlemenBlockEndIndex(provinceName);

		val lines = getContent().getLines();

		val regex = Ctm.format("^\\s*type {0} {1}", buildingType, buildingLevel);
		val index = lines.findFirstRegexLine(regex, start, end);
		return index;
	}

	public void setKingsPurse(String factionSymbol, int kingsPurse) throws PatcherLibBaseEx {

		LinesProcessor lines = getContent().getLines();

		int factionKingPurse = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^;## " + factionSymbol.toUpperCase() + " ##",
						"^denari_kings_purse\\s+\\d+"), 0);
		if (factionKingPurse < 0) throw new PatcherLibBaseEx("Kings Purse not found for faction " + factionSymbol);

		lines.replaceLine(factionKingPurse, "denari_kings_purse\t" + Integer.toString(kingsPurse));
	}

	public void addKingsPurse(String factionSymbol, int kingsPurseAdd) throws PatcherLibBaseEx {

		LinesProcessor lines = getContent().getLines();

		int factionKingPurse = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^;## " + factionSymbol.toUpperCase() + " ##",
						"^denari_kings_purse\\s+\\d+"), 0);
		if (factionKingPurse < 0) throw new PatcherLibBaseEx("Kings Purse not found for faction " + factionSymbol);

		String lineOrg = lines.getLine(factionKingPurse);
		Pattern regex = Pattern.compile("^denari_kings_purse\\s+(\\d+)\\s*");
		Matcher matcher = regex.matcher(lineOrg);

		if (matcher.matches()) {
			int kingPurseOld = Integer.parseInt(matcher.group(1));

			lines.replaceLine(factionKingPurse, "denari_kings_purse\t" + (kingPurseOld + kingsPurseAdd));
		} else
			throw new PatcherLibBaseEx("Unexpected - no match for king purse " + lineOrg);
	}

	public void addStartingTreasury(String factionSymbol, int treasuryBonus) throws PatcherLibBaseEx {

		LinesProcessor lines = getContent().getLines();

		int factionKingPurse = lines.findFirstLineByLinePath(
				Arrays.asList(
						"^;## " + factionSymbol.toUpperCase() + " ##",
						"^\\s*denari\\s+\\d+"), 0);
		if (factionKingPurse < 0) throw new PatcherLibBaseEx("Kings Purse not found for faction " + factionSymbol);

		String lineOrg = lines.getLine(factionKingPurse);
		Pattern regex = Pattern.compile("^\\s*denari\\s+(\\d+)\\s*");
		Matcher matcher = regex.matcher(lineOrg);

		if (matcher.matches()) {
			int treasuryOrg = Integer.parseInt(matcher.group(1));

			lines.replaceLine(factionKingPurse, "denari\t" + (treasuryOrg + treasuryBonus));
		} else
			throw new PatcherLibBaseEx("Unexpected - no match for denari " + lineOrg);
	}

	public void addKingsPursesAll(int kingsPurseAdd) throws PatcherLibBaseEx {

		LinesProcessor lines = getContent().getLines();
		Pattern regex = Pattern.compile("(^denari_kings_purse\\s+)(\\d+)\\s*");

		int factionKingPurse = 0;

		while (factionKingPurse >= 0) {

			factionKingPurse = lines.findFirstByRegexLine(regex, factionKingPurse);
			if (factionKingPurse < 0) break;

			String lineOrg = lines.getLine(factionKingPurse);
			Matcher matcher = regex.matcher(lineOrg);

			if (matcher.matches()) {
				int kingPurseOld = Integer.parseInt(matcher.group(2));

				lines.replaceLine(factionKingPurse, "denari_kings_purse\t" + (kingPurseOld + kingsPurseAdd));
				factionKingPurse++;
			} else
				throw new PatcherLibBaseEx("Unexpected : no match for denari_kongs_purse Line : " + factionKingPurse + " " + lineOrg);
		}
	}



	public FactionsSection(LinesProcessor header, LinesProcessor content) {
		super();

		setHeader(new SectionTextLines("FactionsHeader", header));
		setContent(new SectionTextLines("FactionContent", content));
	}
}
