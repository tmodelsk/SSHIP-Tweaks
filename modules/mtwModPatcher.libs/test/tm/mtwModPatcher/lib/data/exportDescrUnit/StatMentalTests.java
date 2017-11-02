package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 02.11.2017.
 */
public class StatMentalTests {

	@Test
	public void parseShort() {
		val sm = StatMental.parseStr("15, normal, trained");

		assertThat(sm).isNotNull();
		assertThat(sm.Morale).isEqualTo(15);
		assertThat(sm.Discipline).isEqualTo("normal");
		assertThat(sm.Training).isEqualTo("trained");
		assertThat(sm.IsLockMorale).isFalse();
		assertThat(sm.Comments).isNull();
	}

	@Test
	public void parseShortWithComment() {
		val sm = StatMental.parseStr("15, normal, trained    ;some comment");

		assertThat(sm).isNotNull();
		assertThat(sm.Morale).isEqualTo(15);
		assertThat(sm.Discipline).isEqualTo("normal");
		assertThat(sm.Training).isEqualTo("trained");
		assertThat(sm.IsLockMorale).isFalse();
		assertThat(sm.Comments).isEqualTo("    ;some comment");
	}

	@Test
	public void parseFull() {
		val sm = StatMental.parseStr("19, impetuous, highly_trained, lock_morale ; FANATIC");

		assertThat(sm).isNotNull();
		assertThat(sm.Morale).isEqualTo(19);
		assertThat(sm.Discipline).isEqualTo("impetuous");
		assertThat(sm.Training).isEqualTo("highly_trained");
		//assertThat(sm.IsLockMorale).isTrue();
		assertThat(sm.Comments).isEqualTo(" ; FANATIC");
	}
}
