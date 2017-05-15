package tm.m2twModPatcher.sship.Obsolete;

import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.fileEntities.data.world.maps.campaign.DescrStratSectioned;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Tomek on 2016-04-18.
 */
public class PamplonaToCastle extends Feature {

	protected DescrStratSectioned _DescrStrat;

	@Override
	public void executeUpdates() throws Exception {

		_DescrStrat = fileEntityFactory.getFile(DescrStratSectioned.class);

		LinesProcessor lines = _DescrStrat.Factions.getContent().getLines();

		int pamplonaSettlementIndex = lines.findFirstLineByLinePath(
				Arrays.asList(	//"^; >>>> start of resources section <<<<",
						"^;## ARAGON ##",
						"^settlement castle")
				, 0);
		if(pamplonaSettlementIndex < 0) throw new PatcherLibBaseEx("DescrStrat / Factions / Pamplona not found !");

		int pamplonaLevelIndex = lines.findFirstLineByLinePath(
				Arrays.asList("^\\s*level\\s"), pamplonaSettlementIndex);
		lines.replaceInLine(pamplonaLevelIndex, "town" , "large_town");

		int pamlponaFirstBuildingTag = lines.findFirstLineByLinePath(
				Arrays.asList(	"^\\s*region\\s+Pamplona_Province",
								"^\\s*building")
				, pamplonaSettlementIndex);
		if(pamlponaFirstBuildingTag < 0) throw new PatcherLibBaseEx("DescrStrat / Factions / Pamplona not found !");

		int castleIndex = lines.findFirstLineByLinePath(
				Arrays.asList("^\\s*type core_castle_building"), pamlponaFirstBuildingTag+1);	 // "^\\s*{",
		if(castleIndex < 0) throw new PatcherLibBaseEx("DescrStrat / Factions / Pamplona castle not found !");

		lines.replaceInLine(castleIndex, "wooden_castle" , "castle");
		//lines.replaceLine(castleIndex, "\t\ttype core_castle_building castle");


		registerUpdatedFile(_DescrStrat);

	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();

	public PamplonaToCastle() {
		super("Pamplona upgrade to Stone Castle");
	}
}
