package tm.m2twModPatcher.sship.Obsolete;

import lombok.val;
import tm.m2twModPatcher.lib.common.core.features.Feature;
import tm.m2twModPatcher.lib.common.core.features.fileEntities.LinesProcessor;
import tm.m2twModPatcher.lib.engines.ConfigurationSettings;
import tm.m2twModPatcher.lib.fileEntities.data.exportDescrBuilding.ExportDescrBuilding;
import tm.m2twModPatcher.lib.fileEntities.data.world.maps.campaign.DescrStratSectioned;

import java.util.UUID;

/**
 * Created by Tomek on 2016-12-02.
 */
public class CatharsHereticTemple extends Feature {

	private ExportDescrBuilding _ExportDescrBuilding;
	private DescrStratSectioned _DescrStrat;

	@Override
	public void executeUpdates() throws Exception {
		_ExportDescrBuilding = getFileRegisterForUpdated(ExportDescrBuilding.class);
		_DescrStrat = getFileRegisterForUpdated(DescrStratSectioned.class);

		val templeFilePath = ConfigurationSettings.VariousDataPath() + "\\HereticTemple-EDB.txt";
		val templeLines = LinesProcessor.load(templeFilePath);

		_ExportDescrBuilding.getLines().insertAtEnd(templeLines.getLines());


		//_DescrStrat.insertSettlementBuilding("Toulouse_Province", "temple_heretic", "abbey");
		_DescrStrat.insertSettlementBuilding("Toulouse_Province", "temple_heretic", "small_church");

		//_DescrStrat.insertSettlementBuilding("Bordeaux_Province", "temple_heretic", "small_church");
		//_DescrStrat.insertSettlementBuilding("Clermont_Province", "temple_heretic", "small_church");
		//_DescrStrat.insertSettlementBuilding("Poitiers_Province", "temple_heretic", "small_church");
	}

	public CatharsHereticTemple() {
		super("Cathars in Toulouse got Heretic Temple");
	}

	@Override
	public UUID getId() {
		return Id;
	}
	public static UUID Id = UUID.randomUUID();
}
