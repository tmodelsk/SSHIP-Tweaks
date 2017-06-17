package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;
import org.junit.Test;
import tm.common.Ctm;
import tm.mtwModPatcher.FeatureBaseTest;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Resource based tests from ExportDescrBuilding
 */
public class ExportDescrBuidingTests extends FeatureBaseTest {

	@Test
	public void findRecruitmentsByRegex_AllRegex() throws Exception {
		val fileEntityFactory = createFileEntityFactory();

		val edb = fileEntityFactory.getFile(ExportDescrBuilding.class);

		val unitRecrList = edb.findRecruitmentsByRegex(ExportDescrBuilding.unitRecruitmentLinePatters);

		assertThat(unitRecrList).isNotEmpty();
	}

	@Test
	public void findRecruitmentsByRegex_HiddenResource() throws Exception {
		val fileEntityFactory = createFileEntityFactory();

		val edb = fileEntityFactory.getFile(ExportDescrBuilding.class);

		String regex = "^\\s*recruit_pool\\s+\"([\\w\\s']+)\"\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+([\\d\\.]+)\\s+(\\d+)\\s+";
		regex += "requires\\s+";
		regex += "factions\\s+\\{.*"+ "moors"  +"\\s*,.*\\}";
		regex += ".*(and|or)\\s+hidden_resource\\s+(moors|andalusia)"; // |hidden_resource andalusia

		val pattern = Pattern.compile(regex);

		val unitRecrList = edb.findRecruitmentsByRegex(pattern);

		assertThat(unitRecrList).isNotEmpty();

		val alforats = unitRecrList.stream().filter(u -> u.Name.equals("Alforrats")).collect(Collectors.toList());
		val x = alforats;
	}
}
