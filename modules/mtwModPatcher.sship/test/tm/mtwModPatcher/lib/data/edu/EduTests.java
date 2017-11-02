package tm.mtwModPatcher.lib.data.edu;

import lombok.val;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.lib.data.exportDescrUnit.ExportDescrUnitTyped;
import tm.mtwModPatcher.lib.data.exportDescrUnit.WeaponStat;

import static org.assertj.core.api.Assertions.assertThat;

/** Created by tomek on 02.11.2017  */
public class EduTests extends FeatureBaseTest {

	@Test
	public void getAllUnits() throws Exception {
		val fileEntityFactory = createFileEntityFactory();
		val edu = fileEntityFactory.getFile(ExportDescrUnitTyped.class);

		val units = edu.getUnits();
		assertThat(units).isNotEmpty();
	}

	@Test
	public void statPriFullPatternCheck() throws Exception {
		val fileEntityFactory = createFileEntityFactory();
		val edu = fileEntityFactory.getFile(ExportDescrUnitTyped.class);

		val unitFull = edu.getUnit("Early Reiters");

		assertThat(unitFull).isNotNull();
		WeaponStat statPri = unitFull.StatPri;
		assertThat(statPri).isNotNull();
		assertThat(statPri.SoundFireType).isNotNull();

		val unitShort = edu.getUnit("Hobilars");
		assertThat(unitShort).isNotNull();
		statPri = unitShort.StatPri;
		assertThat(statPri).isNotNull();
		assertThat(statPri.SoundFireType).isNull();
	}

}
