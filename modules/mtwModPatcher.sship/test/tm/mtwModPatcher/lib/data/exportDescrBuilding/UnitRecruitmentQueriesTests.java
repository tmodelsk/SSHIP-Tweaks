package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.lib.managers.FactionsDefs;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 17.06.2017.
 */
public class UnitRecruitmentQueriesTests extends FeatureBaseTest {

	@Test
	public void findMoorsRecruitments() throws Exception {
		val fileEntityFactory = createFileEntityFactory();
		val edb = fileEntityFactory.getFile(ExportDescrBuilding.class);

		val queriesServ = new UnitRecruitmentQueries(edb);
		val res = queriesServ.getByFactionsHiddenResourcesPositive(Arrays.asList("moors"), Arrays.asList("moors","andalusia"));

		assertThat(res).isNotEmpty();
	}

	@Test
	public void findMuslimRegionsRecruitments() throws Exception {
		val fileEntityFactory = createFileEntityFactory();
		val edb = fileEntityFactory.getFile(ExportDescrBuilding.class);
		val queriesServ = new UnitRecruitmentQueries(edb);

		val factions = FactionsDefs.islamFactionsList();
		val hiddenResources = FactionsDefs.islamFactionsList();
		hiddenResources.add("andalusia");
		hiddenResources.add("berber");

		val res = queriesServ.getByFactionsHiddenResourcesPositive(factions, hiddenResources);

		assertThat(res).isNotEmpty();

		val units = new HashSet<String>();
		res.forEach( u -> units.add(u.Name) );

		assertThat(units).isNotEmpty();
	}

	@Test
	public void findMuslimRecruitmentsNoHiddenResources() throws Exception {
		val fileEntityFactory = createFileEntityFactory();
		val edb = fileEntityFactory.getFile(ExportDescrBuilding.class);
		val queriesServ = new UnitRecruitmentQueries(edb);

		val factions = FactionsDefs.islamFactionsList();

		val res = queriesServ.getByFactionsNoHiddenResources(factions);

		assertThat(res).isNotEmpty();

		val units = new HashSet<String>();
		res.forEach( u -> units.add(u.Name) );

		assertThat(units).isNotEmpty();
	}
}
