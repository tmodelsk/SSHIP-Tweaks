package tm.mtwModPatcher.sship.lib;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.lib.data.exportDescrBuilding.ExportDescrBuilding;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 17.06.2017.
 */
public class UnitRecruitmentSshipQueriesTests extends FeatureBaseTest {

	@Test
	public void findMuslimRecruitments() throws Exception {
		val fileEntityFactory = createFileEntityFactory();
		val edb = fileEntityFactory.getFile(ExportDescrBuilding.class);
		val queriesServ = new UnitRecruitmentSshipQueries(edb);

		val recruitments = queriesServ.findMuslim();
		assertThat(recruitments).isNotEmpty();

		val units = UnitRecruitmentSshipQueries.toUnitNames(recruitments).stream().sorted().collect(Collectors.toList());
		assertThat(units).isNotEmpty();
	}
}
