package tm.mtwModPatcher.sship.features.global;

import lombok.val;
import tm.common.Tuple3;

import java.util.ArrayList;
import java.util.List;

/** Creates Treasury inflation levels */
@SuppressWarnings("WeakerAccess")
public class MaxTreasuryLimitLevels {

	@SuppressWarnings("SameParameterValue")
	public List<Tuple3<Integer, Integer, Integer>> getLevels(int maxLimit, int iterations) {
		val res = new ArrayList<Tuple3<Integer, Integer, Integer>>();

		//maxLimit *= 1.5 ;

		int min = 0, median = (int) ((min + maxLimit) / 3);
		iterations++;

		int stepRange = (maxLimit - median) / (iterations - 1);
		int minAct, maxAct, inflationStep;

		minAct = median;
		maxAct = minAct + stepRange;
		inflationStep = (int) (median / (Math.pow(2, iterations))) * 2;

		for (int i = 1; i <= iterations; i++) {
			val item = new Tuple3<Integer, Integer, Integer>(minAct, maxAct, (int) (-inflationStep * 1.1));
			res.add(item);

			minAct = maxAct + 1;
			maxAct = maxAct + stepRange;
			inflationStep *= 2;
		}

		// Add 'guard' level,
		maxAct += (stepRange*3);
		val item = new Tuple3<Integer, Integer, Integer>(minAct, maxAct, -((int) (maxAct * 0.9)));
		res.add(item);

		return res;
	}
}
