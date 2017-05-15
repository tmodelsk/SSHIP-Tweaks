package tm.m2twModPatcher.lib.managers.garrisons;

import lombok.val;
import org.junit.Test;
import tm.m2twModPatcher.lib.common.entities.FactionsDefs;
import tm.m2twModPatcher.lib.common.core.features.PatcherLibBaseEx;
import tm.m2twModPatcher.lib.common.entities.SettlementInfo;
import tm.m2twModPatcher.lib.common.entities.SettlementLevel;
import tm.m2twModPatcher.lib.engines.FileEntityFactory;

import static org.junit.Assert.*;

public class GarrisonMetaManagerTest {
	@Test
	public void levySpearmen_Basic_Aragon_ShouldReturn_SpearMilitia() throws Exception {

		GarrisonMetaManager manager = createGarrisonMetaManager();

		UnitGarrisonInfo unit = manager.getFactionStrictImplementation(new UnitMetaDef(UnitType.LevyInfantry), "aragon",
				new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, "aragon"));

		assertNotNull(unit);
		assertEquals("Spear Militia", unit.Name);
	}

	@Test
	public void levySpearmen_Basic_Papacy_ShouldReturn_UrbanSpearMilitia() throws Exception {

		GarrisonMetaManager manager = createGarrisonMetaManager();

		UnitGarrisonInfo unit = manager.getFactionStrictImplementation(new UnitMetaDef(UnitType.LevyInfantry), "papal_states",
				new SettlementInfo("Rome", SettlementLevel.L3_LargeTown, "papal_states"));

		assertNotNull(unit);
		assertEquals("Urban Spear Militia", unit.Name);
	}

	@Test
	public void levySpearmen_Basic_Moors_ShouldReturn_AldathMilitia() throws Exception {

		GarrisonMetaManager manager = createGarrisonMetaManager();

		UnitGarrisonInfo unit = manager.getFactionStrictImplementation(new UnitMetaDef(UnitType.LevyInfantry), "moors",
				new SettlementInfo("Cordoba", SettlementLevel.L3_LargeTown, "moors"));

		assertNotNull(unit);
		assertEquals("Ahdath Militia", unit.Name);
	}

	@Test
	public void levySpearmen_Basic_AllFactions_ShouldReturn_NotNull() throws Exception {
		GarrisonMetaManager manager = createGarrisonMetaManager();
		UnitGarrisonInfo unit;

		for(String factionName : FactionsDefs.allFactionsList()) {
			unit = manager.getFactionStrictImplementation(new UnitMetaDef(UnitType.LevyInfantry), factionName,
					new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, "france"));

			assertNotNull(unit);
			assertNotNull(unit.Name);
		}
	}

	@Test
	public void spearmen_Basic_AllFactions_ShouldReturn_NotNull() throws Exception {
		GarrisonMetaManager manager = createGarrisonMetaManager();
		UnitGarrisonInfo unit;

		for(String factionName : FactionsDefs.allFactionsList()) {
			unit = manager.getFactionImplementation(new UnitMetaDef(UnitType.Spearmen), factionName,
					new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, "hre"));

			assertNotNull(unit);
			assertNotNull(unit.Name);
		}
	}

	@Test
	public void spearmen_Basic_Denmark_ShouldReturn_SpearMilitia2() throws Exception {
		GarrisonMetaManager manager = createGarrisonMetaManager();
		UnitGarrisonInfo unit;

		unit = manager.getFactionImplementation(new UnitMetaDef(UnitType.Spearmen), "denmark",
				new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, "denmark"));

		assertNotNull(unit);
		assertNotNull(unit.Name);
		assertEquals("Spear Militia" , unit.Name);
		assertEquals(2 , unit.Experience);
	}

	@Test
	public void levyMissle_Basic_AllFactions_ShouldReturn_NotNull() throws Exception {
		GarrisonMetaManager manager = createGarrisonMetaManager();
		UnitGarrisonInfo unit;

		for(String factionName : FactionsDefs.allFactionsList()) {
			unit = manager.getFactionImplementation(new UnitMetaDef(UnitType.LevyMissle), factionName,
					new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, "lithuania"));

			assertNotNull(unit);
			assertNotNull(unit.Name);
		}
	}

	@Test
	public void missle_Basic_AllFactions_ShouldReturn_NotNull() throws Exception {
		GarrisonMetaManager manager = createGarrisonMetaManager();
		UnitGarrisonInfo unit;

		for(String factionName : FactionsDefs.allFactionsList()) {
			unit = manager.getFactionImplementation(new UnitMetaDef(UnitType.Missle), factionName,
					new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, "egypt"));

			assertNotNull(unit);
			assertNotNull(unit.Name);
		}
	}

	@Test
	public void heavyInf_Basic_AllFactions_ShouldReturn_NotNull() throws Exception {
		GarrisonMetaManager manager = createGarrisonMetaManager();
		UnitGarrisonInfo unit;

		for(String factionName : FactionsDefs.allFactionsList()) {
			unit = manager.getFactionImplementation(new UnitMetaDef(UnitType.HeavyInfantry), factionName,
					new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, "aragon"));

			assertNotNull(unit);
			assertNotNull(unit.Name);
		}
	}

	@Test
	public void heavyInf_Basic_AllFactions_AllSlaveReplecaments_ShouldReturn_NotNull() throws Exception {
		GarrisonMetaManager manager = createGarrisonMetaManager();
		UnitGarrisonInfo unit;

		for(String factionName : FactionsDefs.allFactionsList()) {

			for (String factionSlaveReplacementName : FactionsDefs.allFactionsList()) {
				//try
				{
					unit = manager.getFactionStrictImplementation(new UnitMetaDef(UnitType.HeavyInfantry), factionName,
							new SettlementInfo("Zaragoza", SettlementLevel.L3_LargeTown, factionSlaveReplacementName));

					assertNotNull(unit);
					assertNotNull(unit.Name);

				}
//				catch (Exception ex) {
//					throw new Exception("Failed for slave replacement: "+factionSlaveReplacementName +" : " + ex.getMessage(), ex);
//				}
			}
		}
	}

	private GarrisonMetaManager createGarrisonMetaManager() throws PatcherLibBaseEx {

		val fileEntityFactory = new FileEntityFactory();
		fileEntityFactory.rootPath = "c:\\Gry\\Steam\\steamapps\\common\\Medieval II Total War\\mods\\SSHIP-TM";

		val garrisonMetaManager = new GarrisonMetaManager(fileEntityFactory);

		return garrisonMetaManager;
	}

	public GarrisonMetaManagerTest() {

	}
}