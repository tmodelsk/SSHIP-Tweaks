package tm.common.collections;

import java.util.List;

/**
 * List with uniqe values
 */
public interface ListUnique<T> extends List<T> {


	/** Add only uniqu value, returns added or not */
	public boolean addUnique(T o);
}
