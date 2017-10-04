package tm.common.collections;

import lombok.val;

import java.util.Collection;

/**
 * Created by tomek on 17.06.2017.
 */
public class CollectionUtils {

	public static <T> boolean isAnyFirstElementInSecond(Collection<T> first, Collection<T> second) {
		for (val f : first)
			if(second.contains(f)) return true;

		return false;
	}


	public static <T> boolean areEqualWithoutOrder(Collection<T> first, Collection<T> second) {
		return isAllFirstElementsAreInSecond(first, second) && isAllFirstElementsAreInSecond(second, first);
	}

	public static <T> boolean isAllFirstElementsAreInSecond(Collection<T> first, Collection<T> second) {

		for(T f : first)
			if(!second.contains(f)) return false;

		return true;
	}
}
