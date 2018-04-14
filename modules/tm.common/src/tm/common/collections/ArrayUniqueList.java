package tm.common.collections;

import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Array & Unique List, extends ArrayList, with internal HashSet to track uniquicity.
 */
public class ArrayUniqueList<T> extends ArrayList<T> implements ListUnique<T> {

	@Override
	public boolean addUnique(T o) {
		try {
			add(o);
			return true;
		} catch (NotUniqueEx notUniqueEx) {
			return  false;
		}
	}

	@Override
	public boolean add(T t) {
		if(set.contains(t))
			throw new NotUniqueEx("Duplicate: "+t);

		set.add(t);
		return super.add(t);
	}

	@Override
	public void add(int index, T element) {
		if(set.contains(element))
			throw new NotUniqueEx();

		set.add(element);
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (val element : c)
			if(set.contains(element)) throw new NotUniqueEx();

		set.addAll(c);
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return super.addAll(index, c);
	}

	@Override
	public T remove(int index) {
		T value = get(index);
		set.remove(value);

		return super.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		set.remove(o);

		//this.remo

		return super.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for(val o : c)
			set.remove(o);

		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		val toRemove = new ArrayList<T>();

		T tmp;
		for (int i = 0; i < size(); i++) {
			tmp = get(i);
			if(!c.contains(tmp))
				//toRemove.add(tmp);
				set.remove(tmp);
		}

		return super.retainAll(c);
	}

	@Override
	public void clear() {
		set.clear();
		super.clear();
	}

	private Set<T> set;

	public ArrayUniqueList(int initialCapacity) {
		super(initialCapacity);

		set=new HashSet<T>(initialCapacity);
	}

	public ArrayUniqueList() {
		set = new HashSet<T>();
	}

	public ArrayUniqueList(Collection<? extends T> c) {
		super(c);

		set=new HashSet<T>(c);

		if (size() != set.size()) {

			clear();
			set.clear();

			throw new NotUniqueEx("Not Unique input collection, not added !");
		}
	}
}
