package tm.mtwModPatcher.lib.managers.garrisons;

import lombok.val;
import tm.mtwModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.mtwModPatcher.lib.common.entities.SettlementInfo;
import tm.mtwModPatcher.lib.common.entities.SettlementLevel;
import tm.mtwModPatcher.lib.engines.FileEntityFactory;
import tm.mtwModPatcher.sship.lib.GarrisonManager;
import tm.mtwModPatcher.sship.lib.GarrisonMetaManager;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Tomek on 2016-11-13.
 */
public class GarrisonManagerTest {

	@org.junit.Test
	public void Village_Aragon_ShouldReturnSpearMilitia() throws Exception {

		GarrisonManager manager = createGarrisonManager();

		SettlementInfo settlement = new SettlementInfo("Pamplona", SettlementLevel.L1_Village);
		String faction = "aragon";

		List<UnitGarrisonInfo> units = manager.getUnits(settlement, faction);

		assertNotNull(units);
		assertEquals(units.size(), 1);

	}

	@org.junit.Test
	public void getMetaList_Village() throws PatcherLibBaseEx {
		GarrisonManager manager = createGarrisonManager();

		List<UnitMetaDef> metaList = manager.getMetaListBySettlementLevel(SettlementLevel.L1_Village);

		assertNotNull(metaList);
		assertEquals(1 , metaList.size());

		UnitMetaDef unitMeta = metaList.get(0);
		assertEquals(UnitType.LevyInfantry, unitMeta.Type);
		assertEquals(1, unitMeta.Quantity);
		assertEquals(0, unitMeta.Experience);
		assertEquals(0, unitMeta.Armor);
		assertEquals(0, unitMeta.Weapon);
	}

	private GarrisonManager createGarrisonManager() throws PatcherLibBaseEx {
		val metaMan = createGarrisonMetaManager();
		val manager = new GarrisonManager(metaMan);

		return manager;
	}

	private GarrisonMetaManager createGarrisonMetaManager() throws PatcherLibBaseEx {

		val fileEntityFactory = new FileEntityFactory();

		val garrisonMetaManager = new GarrisonMetaManager(fileEntityFactory);

		return garrisonMetaManager;
	}
}