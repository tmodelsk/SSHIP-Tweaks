package tm.mtwModPatcher.sship.features.Obsolete;

import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;

import java.util.UUID;

/** Created by Tomek on 2016-04-16. Created forts securing from Moors incvasions  */
public class SaragossaFortifiedFtr extends Feature {
//	region Zaragoza_Province
//	fort 69, 138 stone_fort_a culture southern_european
//	fort 65, 144 stone_fort_a culture southern_european
//	fort 73, 141 stone_fort_b culture southern_european
//	fort 73, 143 stone_fort_b culture southern_european
//	watchtower 81, 138

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {

		_DescrStrat = fileEntityFactory.getFile(DescrStratSectioned.class);

		// ; >>>> start of regions section <<<<
//		int lineNumber = _DescrStrat.FindFirstExactLine("; >>>> start of regions section <<<<");
//		if(lineNumber < 0) throw new PatcherLibBaseEx("Start of regions sections not found !!");

		LinesProcessor regionLines = _DescrStrat.Regions.content().lines();

		int lineNumber = regionLines.findFirstExactLine("region Zaragoza_Province");
		if(lineNumber < 0) throw new PatcherLibBaseEx("no zaragoza region, not implemented");

		regionLines.insertAt(lineNumber+1, "fort 69, 138 stone_fort_a culture southern_european");
		regionLines.insertAt(lineNumber+1, "fort 65, 144 stone_fort_a culture southern_european");
		//regionLines.insertAt(lineNumber+1, "fort 73, 141 stone_fort_b culture southern_european");
		//regionLines.insertAt(lineNumber+1, "fort 73, 143 stone_fort_b culture southern_european");


		registerForUpdate(_DescrStrat);

	}

	protected DescrStratSectioned _DescrStrat;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("7ba1a89b-3260-4b42-adbf-dc181a56946f");

	public SaragossaFortifiedFtr() {

		super("Saragossa province fortified with forts");

	}
}
