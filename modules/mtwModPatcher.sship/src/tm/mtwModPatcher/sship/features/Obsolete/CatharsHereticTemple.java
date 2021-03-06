package tm.mtwModPatcher.sship.features.Obsolete;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.Feature;
import tm.mtwModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.engines.ConfigurationSettings;

import java.util.UUID;

/**
 * Created by Tomek on 2016-12-02.
 */
public class CatharsHereticTemple extends Feature {

	@Override
	public void setParamsCustomValues() {

	}

	@Override
	public void executeUpdates() throws Exception {
		edb = getFileRegisterForUpdated(ExportDescrBuilding.class);
		_DescrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);

		val templeFilePath = ConfigurationSettings.VariousDataPath() + "\\HereticTemple-EDB.txt";
		val templeLines = LinesProcessor.load(templeFilePath);

		edb.getLines().insertAtEnd(templeLines.getLines());


		//_DescrStrat.insertSettlementBuilding("Toulouse_Province", "temple_heretic", "abbey");
		_DescrStrat.Factions.insertSettlementBuilding("Toulouse_Province", "temple_heretic", "small_church");

		//_DescrStrat.insertSettlementBuilding("Bordeaux_Province", "temple_heretic", "small_church");
		//_DescrStrat.insertSettlementBuilding("Clermont_Province", "temple_heretic", "small_church");
		//_DescrStrat.insertSettlementBuilding("Poitiers_Province", "temple_heretic", "small_church");
	}

	public CatharsHereticTemple() {
		super("Cathars in Toulouse got Heretic Temple");
	}

	private ExportDescrBuilding edb;
	private DescrStratSectioned _DescrStrat;

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.fromString("c76b6fda-aa49-4fdd-9d8b-1017bfedfbf9");
}
