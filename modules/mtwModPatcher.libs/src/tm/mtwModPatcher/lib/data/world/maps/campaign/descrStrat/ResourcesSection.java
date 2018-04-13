package tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.sections.SectionDocTextLines;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.sections.SectionTextLines;

public class ResourcesSection extends SectionDocTextLines {

	public void addResource(String provinceSymbol, String resourceSymbol, int x, int y) {
		val descrStratResourcesLines = content().lines();
		val zaragozaProvIndex = descrStratResourcesLines.findExpFirstRegexLine("^;"+provinceSymbol);

		descrStratResourcesLines.insertAt(zaragozaProvIndex+1, "resource\t"+ resourceSymbol +",\t"+x+", "+y);
	}


	public ResourcesSection(LinesProcessor header, LinesProcessor content) {
		super();

		setHeader(new SectionTextLines("ResourcesHeader", header));
		setContent(new SectionTextLines("ResourcesContent", content));
	}
}
