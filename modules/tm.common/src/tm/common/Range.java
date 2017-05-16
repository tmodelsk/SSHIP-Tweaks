package tm.common;

import lombok.Getter;
import lombok.Setter;

/** Range / Tuple / Pair on generics */
public class Range<S,E> {

	@Getter @Setter
	private S start;

	@Getter @Setter
	private E end;

	public Range(S start, E end) {
		this.start = start;
		this.end = end;
	}

	public Range() {}
}
