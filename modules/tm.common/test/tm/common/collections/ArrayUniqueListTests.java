package tm.common.collections;

import lombok.val;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ArrayUniqueListTests {

	@Test
	public void create_ShouldDo() {
		val l = new ArrayUniqueList<String>();
		assertThat(l).isNotNull();
	}

	@Test
	public void create_FromUniqueValues_ShouldDo() {
		val integers = Arrays.asList(1,2,3,4,5);

		val l = new ArrayUniqueList<Integer>(integers);
		assertThat(l).isNotNull();
	}

	@Test
	public void create_FromNotUniqueValues_ShouldThrow() {
		val integers = Arrays.asList(1,2,3,4,5,1);

		assertThatThrownBy(() -> new ArrayUniqueList<>(integers)).isInstanceOf(NotUniqueEx.class);
	}

	@Test
	public void add_Unique_Should() {
		val l = new ArrayUniqueList<Integer>();
		l.add(1);

		l.add(2);
	}

	@Test
	public void add_NotUnique_ShouldThrowNotAdd() {
		val l = new ArrayUniqueList<Integer>();
		l.add(1);
		l.add(2);
		val orgSize = l.size();

		assertThatThrownBy(() -> l.add(1)).isInstanceOf(NotUniqueEx.class);
		assertThat(l.size()).isEqualTo(orgSize);
	}

	@Test
	public void addAll_Unique_ShouldAdd() {
		val l = new ArrayUniqueList<Integer>();
		l.add(1);
		l.add(2);

		l.addAll(Arrays.asList(3,4,5));

		assertThat(l).size().isEqualTo(5);
	}

	@Test
	public void addAll_NotUnique_ShouldThrowNotAdd() {
		val l = new ArrayUniqueList<Integer>();
		l.add(1);
		l.add(2);

		assertThatThrownBy( () -> l.addAll(Arrays.asList(3,4,5,1) )).isInstanceOf(NotUniqueEx.class);

		assertThat(l).size().isEqualTo(2);
	}

	@Test
	public void addRemoveObjAdd_ShouldWork() {
		val l = new ArrayUniqueList<Integer>();
		l.add(1);
		l.add(2);

		l.remove(new Integer(2));

		l.add(2);	// should add
	}

	@Test
	public void addRemoveByIndexAdd_ShouldWork() {
		val l = new ArrayUniqueList<String>();
		l.add("1");
		l.add("2");

		l.remove(1);

		l.add("2");	// should add
	}

	@Test
	public void addRemoveAllAdd_ShouldWork() {
		val l = new ArrayUniqueList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.add("5");

		val listToRemove = Arrays.asList("3","4");

		l.removeAll(listToRemove);

		l.add("3");	// should add
		l.add("4");	// should add
	}

	@Test
	public void addRetainAllAddUnique_ShouldWork() {
		val l = new ArrayUniqueList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.add("5");

		val listToRetain = Arrays.asList("3","4");
		l.retainAll(listToRetain);

		l.add("1");	// should add
		l.add("2");	// should add
	}

	@Test
	public void addRetainAllAddNotUnique_ShouldWork() {
		val l = new ArrayUniqueList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.add("5");

		val listToRetain = Arrays.asList("3","4");
		l.retainAll(listToRetain);

		l.add("1");	// should add
		l.add("2");	// should add
		assertThatThrownBy(() -> l.add("3")).isInstanceOf(NotUniqueEx.class);
	}

	@Test
	public void addClearAdd_ShouldWork() {
		val l = new ArrayUniqueList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		l.add("4");
		l.add("5");

		l.clear();

		l.add("3");	// should add
		l.add("4");	// should add
	}

}
