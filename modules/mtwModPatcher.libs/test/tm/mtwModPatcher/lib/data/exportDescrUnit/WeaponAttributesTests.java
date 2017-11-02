package tm.mtwModPatcher.lib.data.exportDescrUnit;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 02.11.2017.
 */
public class WeaponAttributesTests {

	@Test
	public void parseSingleAttribute() {
		val str="ap";

		val weaponAttr = WeaponAttributes.parseStr(str);

		assertThat(weaponAttr).isNotNull();
		assertThat(weaponAttr.values().size()).isEqualTo(1);
	}

	@Test
	public void parseMultiAttributes() {
		val str="ap, area";

		val weaponAttr = WeaponAttributes.parseStr(str);

		assertThat(weaponAttr).isNotNull();
		assertThat(weaponAttr.values().size()).isEqualTo(2);
	}

}
