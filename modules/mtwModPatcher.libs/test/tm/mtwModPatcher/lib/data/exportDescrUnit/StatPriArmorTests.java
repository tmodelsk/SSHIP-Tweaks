package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Created by tomek on 02.11.2017 */
public class StatPriArmorTests {

	@Test
	public void parseSimple() {
		val str = "10, 10, 0, metal ";

		val spa = StatPriArmor.parseStr(str);

		assertThat(spa).isNotNull();
		assertThat(spa.Armour).isEqualTo(10);
		assertThat(spa.DefenceSkill).isEqualTo(10);
		assertThat(spa.Shield).isEqualTo(0);
		assertThat(spa.Sound).isEqualTo("metal");
		assertThat(spa.Comments).isNull();
	}

	@Test
	public void parseSimpleWithComment() {
		val str = "7, 6, 5, metal ; ";

		val spa = StatPriArmor.parseStr(str);

		assertThat(spa).isNotNull();
		assertThat(spa.Armour).isEqualTo(7);
		assertThat(spa.DefenceSkill).isEqualTo(6);
		assertThat(spa.Shield).isEqualTo(5);
		assertThat(spa.Sound).isEqualTo("metal");
		assertThat(spa.Comments).isEqualTo(" ; ");
	}

	@Test
	public void parseSimpleWithCommentAdvanced() {
		val str = "6, 10, 5, metal ; mail ; GUARD";

		val spa = StatPriArmor.parseStr(str);

		assertThat(spa).isNotNull();
		assertThat(spa.Armour).isEqualTo(6);
		assertThat(spa.DefenceSkill).isEqualTo(10);
		assertThat(spa.Shield).isEqualTo(5);
		assertThat(spa.Sound).isEqualTo("metal");
		assertThat(spa.Comments).isEqualTo(" ; mail ; GUARD");

		val spaStr = spa.serialize();
		assertThat(str).isEqualTo(spaStr);
	}
}
