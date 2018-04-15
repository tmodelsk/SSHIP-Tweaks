package tm.mtwModPatcher.lib.managers;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import tm.common.collections.ArrayUniqueList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 31.05.2017.
 */
public class FactionsDefsTests {

	@Test
	public void resolve_justFactions_ShouldResolve() {
		val input = new ArrayUniqueList<String>();

		input.add("poland");
		input.add("aragon");

		val res = FactionsDefs.resolveFactions(input);

		assertThat(res).isNotEmpty();
		assertThat(res).containsAll(input);
	}

	@Test
	public void resolve_Group_ShouldResolve() {
		val input = new ArrayUniqueList<String>();

		input.add("northern_european");

		val res = FactionsDefs.resolveFactions(input);

		assertThat(res).isNotEmpty();
		assertThat(res).contains("denmark");
		assertThat(res).doesNotContain("northern_european");
	}

	@Test
	public void getFactionInfos_ShouldReturnAllList() {
		val factions = FactionsDefs.factionInfos();

		Assertions.assertThat(factions).isNotEmpty();
	}

}
