package tm.mtwModPatcher.lib.common.core.features;

import lombok.val;
import org.junit.Test;
import tm.m2twModPatcher.sship.agentsCharacters.NobilityManyGeneralsGovernors;
import tm.m2twModPatcher.sship.armyUnits.ArmySuppliesCosts;
import tm.m2twModPatcher.sship.map.LandBridgeGibraltar;
import tm.m2twModPatcher.sship.map.LandBridgeGibraltarLaManche;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 04.05.2017.
 */
public class FeatureListTests {

	@Test
	public void getConflictingFeatures_TwoNonConflicting_ShouldReturnNullOrEmpty() {
		val ft1 = new ArmySuppliesCosts();
		val ft2 = new NobilityManyGeneralsGovernors();

		val featureList = new FeatureList();
		featureList.add(ft1);
		featureList.add(ft2);

		ft1.initialize(null, null);
		ft2.initialize(null, null);

		ft1.setEnabled(true);
		ft2.setEnabled(true);

		val conflicts = featureList.getConflictingFeatures();

		assertThat(conflicts).isNullOrEmpty();
	}

	@Test
	public void getConflictingFeatures_TwoConflicting_ShouldReturnConflict() {
		val ft1 = new LandBridgeGibraltar();
		val ft2 = new LandBridgeGibraltarLaManche();

		val featureList = new FeatureList();
		featureList.add(ft1);
		featureList.add(ft2);

		ft1.initialize(null, null);
		ft2.initialize(null, null);

		ft1.setEnabled(true);
		ft2.setEnabled(true);

		val conflicts = featureList.getConflictingFeatures();

		assertThat(conflicts).isNotEmpty();
	}

}
