package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 22.06.2017.
 */
public class SoldierTests {

	@Test
	public void parseEduEntry_Simple() {
		val str = "Swiss_Pikemen, 60, 0, 1";

		val s = Soldier.parseEduEntry(str);

		assertThat(s).isNotNull();
		assertThat(s.Model).isEqualTo("Swiss_Pikemen");
		assertThat(s.NumberOfMen).isEqualTo(60);
		assertThat(s.SpecialNumber).isEqualTo(0);
		assertThat(s.Mass).isEqualTo(1.0);
	}

	@Test
	public void parseEduEntry_Advanced() {
		val str = "soldier NE_Trebuchet_Crew, 16, 2, 1";

		val s = Soldier.parseEduEntry(str);

		assertThat(s).isNotNull();
		assertThat(s.Model).isEqualTo("NE_Trebuchet_Crew");
		assertThat(s.NumberOfMen).isEqualTo(16);
		assertThat(s.SpecialNumber).isEqualTo(2);
		assertThat(s.Mass).isEqualTo(1.0);
	}

	@Test
	public void parseEduEntry_Advanced2() {
		val str = "hirdmenn, 40, 0, 1.2";

		val s = Soldier.parseEduEntry(str);

		assertThat(s).isNotNull();
		assertThat(s.Model).isEqualTo("hirdmenn");
		assertThat(s.NumberOfMen).isEqualTo(40);
		assertThat(s.SpecialNumber).isEqualTo(0);
		assertThat(s.Mass).isEqualTo(1.2);
	}

	@Test
	public void toEduString_Simple() {
		val str = "Swiss_Pikemen, 60, 0, 1";
		val s = Soldier.parseEduEntry(str);
		val resultStr = s.toEduString();

		assertThat(resultStr).isEqualTo(str);
	}

	@Test
	public void toEduString_Advanced() {
		val str = "NE_Trebuchet_Crew, 16, 2, 1";
		val s = Soldier.parseEduEntry(str);
		val resultStr = s.toEduString();

		assertThat(resultStr).isEqualTo(str);

	}

	@Test
	public void toEduString_Advanced2() {
		val str = "hirdmenn, 40, 0, 1.2";
		val s = Soldier.parseEduEntry(str);
		val resultStr = s.toEduString();

		assertThat(resultStr).isEqualTo(str);
	}
}
