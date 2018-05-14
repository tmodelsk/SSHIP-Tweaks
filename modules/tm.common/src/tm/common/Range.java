package tm.common;

/** Range / Tuple / Pair on generics */
public class Range<S,E> {


	private S start;
	public S start() {
		return start;
	}
	public void start(S start) {
		this.start = start;
	}


	private E end;

	public E end() {
		return end;
	}
	public void end(E end) {
		this.end = end;
	}

	public Range(S start, E end) {
		this.start = start;
		this.end = end;
	}

	public Range() {}


	@Override
	public String toString() {
		return "Range{" + start + "," + end +'}';
	}
}
