package tm.common.collections;

import lombok.val;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by tomek on 17.06.2017.
 */
public class CollectionUtilsTests {

	@Test
	public void areEqualWithoutOrder_EqualReversedLists_ShouldBeTrue() {
		List<Integer> list1 = Arrays.asList(1,2,3,4,5,6);
		List<Integer> list2= Arrays.asList(6,5,4,3,2,1);

		val res = CollectionUtils.areEqualWithoutOrder(list1, list2);

		assertThat(res).isTrue();
	}

	@Test
	public void areEqualWithoutOrder_SecondBiggerReversedLists_ShoudBeFalse() {
		List<Integer> list1 = Arrays.asList(1,2,3,4,5,6);
		List<Integer> list2= Arrays.asList(10,9,8,7,6,5,4,3,2,1);

		val res = CollectionUtils.areEqualWithoutOrder(list1, list2);

		assertThat(res).isFalse();
	}

	@Test
	public void isAllFirstElementsAreInSecond_EqualReversedLists() {
		List<Integer> list1 = Arrays.asList(1,2,3,4,5,6);
		List<Integer> list2= Arrays.asList(6,5,4,3,2,1);

		val res = CollectionUtils.isAllFirstElementsAreInSecond(list1, list2);

		assertThat(res).isTrue();
	}

	@Test
	public void isAllFirstElementsAreInSecond_SecondBiggerReversedLists_ShoudBeTrue() {
		List<Integer> list1 = Arrays.asList(1,2,3,4,5,6);
		List<Integer> list2= Arrays.asList(10,9,8,7,6,5,4,3,2,1);

		val res = CollectionUtils.isAllFirstElementsAreInSecond(list1, list2);

		assertThat(res).isTrue();
	}

	@Test
	public void isAllFirstElementsAreInSecond_SecondBiggerReversedOneMissing_ShoudBeTrue() {
		List<Integer> list1 = Arrays.asList(1,2,3,4,5,6);
		List<Integer> list2= Arrays.asList(10,9,8,7,6,5,3,2,1);

		val res = CollectionUtils.isAllFirstElementsAreInSecond(list1, list2);

		assertThat(res).isFalse();
	}
}
