package tm.mtwModPatcher.lib.common.core.features.params;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 21.04.2017.
 */
public class ParamIdTests {

	@Test
	public void equals_sameInstance_ShouldEqual() {
		val par = new ParamIdString("a", "name", null, null);
		assertThat(par.equals(par)).isTrue();
	}

	@Test
	public void equals_sameTypeSameSymbol_ShouldEqual() {
		val par1 = new ParamIdString("a", "name", null, null);
		val par2 = new ParamIdString("a", "name", null, null);
		assertThat(par1.equals(par2)).isTrue();
	}

	@Test
	public void equals_sameTypeDifferentSymbol_ShouldNotEqual() {
		val par1 = new ParamIdString("a", "name", null, null);
		val par2 = new ParamIdString("b", "name", null, null);
		assertThat(par1.equals(par2)).isFalse();
	}

	@Test
	public void equals_diffTypeSameSymbol_ShouldNotEqual() {
		val par1 = new ParamIdString("a", "name", null, null);
		val par2 = new ParamIdDouble("b", "name", null, null);
		assertThat(par1.equals(par2)).isFalse();
	}

	@Test
	public void hashCode_sameInstance_ShouldEqual() {
		val par = new ParamIdString("a", "name", null, null);
		assertThat(par.hashCode()).isEqualTo(par.hashCode());
	}

	@Test
	public void hashCode_sameTypeDifferentSymbol_ShouldNotEqual() {
		val par1 = new ParamIdString("a", "name", null, null);
		val par2 = new ParamIdString("b", "name", null, null);
		assertThat(par1.hashCode()).isNotEqualTo(par2.hashCode());
	}

	@Test
	public void hashCode_sameTypeSameSymbol_ShouldEqual() {
		val par1 = new ParamIdString("a", "name", null, null);
		val par2 = new ParamIdString("a", "name", null, null);
		assertThat(par1.hashCode()).isEqualTo(par2.hashCode());
	}

	@Test
	public void hashCode_diffTypeSameSymbol_ShouldNotEqual() {
		val par1 = new ParamIdString("a", "name", null, null);
		val par2 = new ParamIdDouble("b", "name", null, null);
		assertThat(par1.hashCode()).isNotEqualTo(par2.hashCode());
	}

}
