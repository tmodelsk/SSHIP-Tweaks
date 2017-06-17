package tm.mtwModPatcher.lib.data.exportDescrBuilding;

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 17.06.2017.
 */
public class UnitRecuitmentInfoTests {

	@Test
	public void getUnitRequireSimple_FactionsAndConditions() {
		val unitRecr = new UnitRecuitmentInfo();
		unitRecr.RequirementStr="        recruit_pool  \"iqta'dar\"  0.75   0.07   1  0  requires factions { milan, } and not event_counter first_mamluks_abbasid 1";

		val unitReq = unitRecr.getUnitRequireSimple();
		assertThat(unitReq).isNotNull();
		assertThat(unitReq.Factions).containsExactly("milan");
		assertThat(unitReq.RestConditions).isEqualTo(" and not event_counter first_mamluks_abbasid 1");
	}

	@Test
	public void getUnitRequireSimple_FactionsNoConditions() {
		val unitRecr = new UnitRecuitmentInfo();
		unitRecr.RequirementStr="        recruit_pool  \"Dismounted Fari Archers\"  0.5   0.08   1  0  requires factions { turks, rum, kwarezm, }";

		val unitReq = unitRecr.getUnitRequireSimple();
		assertThat(unitReq).isNotNull();
		assertThat(unitReq.Factions).contains("turks" , "rum" , "kwarezm");
		assertThat(unitReq.RestConditions).isNull();
	}
}
