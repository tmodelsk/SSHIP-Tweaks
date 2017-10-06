package tm.mtwModPatcher.sship.features.global.rimlandHeartland;

import tm.common.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomek on 24.06.2017.
 */
public class RimlandProvinceList {

	public void l(String province) {
		add(province, 1);
	}

	public void adH(String province) {
		add(province, 2);
	}

	public void add(String provinceName) {
		add(provinceName, 1);
	}
	public void add1(String provinceName) {
		add(provinceName, 1);
	}
	public void add2(String provinceName) {
		add(provinceName, 2);
	}
	public void add3(String provinceName) {
		add(provinceName, 3);
	}

	public void add(String province, int level) {
		provinces.add(new Tuple2<>(province, level));
	}


	private List<Tuple2<String, Integer>> provinces = new ArrayList<>();
	public List<Tuple2<String, Integer>> getList() {
		return provinces;
	}

}
