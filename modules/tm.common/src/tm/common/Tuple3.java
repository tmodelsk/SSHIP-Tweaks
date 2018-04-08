package tm.common;

import lombok.Getter;
import lombok.Setter;

/** Tuple (Vector ?) with 3 elements */
public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {

	@Getter @Setter protected T3 Item3;

	@Override
	public String toString() {
		return Ctm.format("{0}, {1}", super.toString(), Item3);
	}

	public Tuple3(T1 item1, T2 item2, T3 item3) {
		Item1 = item1;
		Item2 = item2;
		Item3 = item3;
	}

	public Tuple3() {
	}
}
