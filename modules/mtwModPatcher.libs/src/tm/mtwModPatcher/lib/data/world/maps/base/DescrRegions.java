package tm.mtwModPatcher.lib.data.world.maps.base;

import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessorFileEntity;

import java.util.ArrayList;
import java.util.List;

/**  */
public class DescrRegions extends LinesProcessorFileEntity {

	public static final String Jihad = "jihad";
	public static final String Crusade = "crusade";
	public static final String KnightsOfSantiago = "knights_of_santiago_chapter_house";
	public static final String TemplarKnights = "templars_chapter_house";
	public static final String StJohnKnights = "st_johns_chapter_house";

	public List<String> getResources(String provinceName) throws PatcherLibBaseEx {
		LinesProcessor lines = getLines();

		int provinceStart = lines.findExpFirstRegexLine("^"+ provinceName);
		int resourceLineIndex = provinceStart+5;

		String resourcesLine = lines.getLine(resourceLineIndex);

		List<String> resources = new ArrayList<>();

		String[] resourcesTab = resourcesLine.split(",");
		for (String res : resourcesTab) {
			resources.add(res.trim());
		}

		return resources;
	}

	public String getSettlementName(String provinceName) throws PatcherLibBaseEx {
		LinesProcessor lines = getLines();

		int provinceStart = lines.findExpFirstRegexLine("^"+ provinceName);
		int settlementLineIndex = provinceStart+1;

		String settlementLine = lines.getLine(settlementLineIndex);

		settlementLine = settlementLine.trim();

		return settlementLine;
	}

	public void addResources(String provinceName, List<String> resourceNames) throws PatcherLibBaseEx {
		LinesProcessor lines = getLines();

		int provinceStart = lines.findExpFirstRegexLine("^"+ provinceName);
		int resourceLineIndex = provinceStart+5;

		String resourcesLine = lines.getLine(resourceLineIndex);

		boolean isChanged = false;
		for (String resourceName : resourceNames) {
			if(!resourcesLine.contains(resourceName)) {
				resourcesLine += ", " + resourceName;
				isChanged = true;
			}
		}

		if(isChanged)
			lines.replaceLine(resourceLineIndex, resourcesLine);
	}

	public void addResource(String provinceName, String resourceName) throws PatcherLibBaseEx {
		List<String> list = new ArrayList<>();
		list.add(resourceName);

		addResources(provinceName, list);
	}


//	Zaragoza_Province
//		Zaragoza
//		aragon
//		Aragonese_Capital_Rebels
//		13 196 198
//		aragon, river, capital, templars_chapter_house, knights_of_santiago_chapter_house, st_johns_chapter_house, jihad, crusade
//		5
//		7
//		religions { catholic 55 orthodox 0 islam 35 pagan 0 heretic 10 }

	public DescrRegions() {
		super("data\\world\\maps\\base\\descr_regions.txt");
	}
}
