package tm.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Simple point
 */
public class Point<T> {

	@Getter @Setter
	private T x;
	@Getter @Setter
	private T y;


	@Override
	public String toString() {
		return x.toString() + "," + y.toString();
	}

	public Point(T x, T y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
	}
}
