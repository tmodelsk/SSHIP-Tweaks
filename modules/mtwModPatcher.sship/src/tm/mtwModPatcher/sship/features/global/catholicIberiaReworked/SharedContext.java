package tm.mtwModPatcher.sship.features.global.catholicIberiaReworked;

import tm.mtwModPatcher.lib.common.entities.FactionInfo;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.unitModels.BattleModels;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.DescrMercenaries;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.FactionsSection;

import java.util.List;

public class SharedContext {

	public List<FactionInfo> iberiaChristianFactions;
	public List<FactionInfo> otherChristianAndSlaveFactions;
	public List<String> allSpainHiddenResList;

	public ExportDescrBuilding edb;
	public ExportDescrUnitTyped edu;
	public DescrMercenaries descrMercenaries;
	public DescrStratSectioned descrStrat;
	public FactionsSection factionsSection;
	public BattleModels battleModels;
	public DescrRegions descrRegions;
	
}
