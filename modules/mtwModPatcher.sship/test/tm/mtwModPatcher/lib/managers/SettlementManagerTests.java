package tm.mtwModPatcher.lib.managers;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import tm.mtwModPatcher.FeatureBaseTest;
import tm.mtwModPatcher.lib.data.world.maps.base.DescrRegions;
import tm.mtwModPatcher.lib.data.world.maps.campaign.descrStrat.DescrStratSectioned;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SettlementManagerTests extends FeatureBaseTest {

	@Test
	public void getAllSettlements_ShouldReturn() throws Exception {
		val fileEntityFactory = createFileEntityFactory();

		val settlementManager = new SettlementManager(
				fileEntityFactory.getFile(DescrStratSectioned.class),
				fileEntityFactory.getFile(DescrRegions.class));

		val settlements = settlementManager.getAllSettlements();

		Assertions.assertThat(settlements).isNotEmpty();

		for(val settl : settlements) {
			try {
				Assertions.assertThat(settl.Position).isNotNull();
			}
			catch (AssertionError e) {
				throw new Exception(e.getMessage() + " in Settlement.Name " + settl.Name);
			}
		}

		val byResource = settlementManager.groupByHiddenResources();
		byResource.size();

		// Print:
		String res = "";
		for(val hrName : byResource.keySet().stream().sorted(String::compareTo).collect(Collectors.toList())) {
			res += hrName + " : ";
			val provinces = byResource.get(hrName);
			for(val province : provinces)
				res += province.Name + ", ";

			res += nl+nl;
		}
		System.out.println("Hidden Resources + Provinces list: "+nl);
		System.out.println(res + nl);
	}

	@Test
	public void sumHiddenResources() throws Exception {
		val fileEntityFactory = createFileEntityFactory();

		val settlementManager = new SettlementManager(
				fileEntityFactory.getFile(DescrStratSectioned.class),
				fileEntityFactory.getFile(DescrRegions.class));

		val settlements = settlementManager.getAllSettlements();

		assertThat(settlements).isNotEmpty();

		val hiddenResources = new HashSet<String>();

		settlements.forEach( si -> hiddenResources.addAll(si.Resources) );

		System.out.println("Hidden Resourcec Total = "+hiddenResources.size());

		hiddenResources.forEach( hr -> System.out.print(hr + ","));
	}

	private static final String nl = System.lineSeparator();
}
