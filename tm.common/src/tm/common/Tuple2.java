package tm.common;

import lombok.Getter;
import lombok.Setter;

/** Tuple with 2 elements  */
public class Tuple2<T1, T2> {
	@Getter @Setter protected T1 Item1;
	@Getter @Setter protected T2 Item2;

	@Override
	public String toString() {
		return Ctm.msgFormat("{0}, {1}", Item1, Item2);
	}

	public Tuple2(T1 item1, T2 item2) {
		Item1 = item1;
		Item2 = item2;
	}

	public Tuple2() {
	}
}
