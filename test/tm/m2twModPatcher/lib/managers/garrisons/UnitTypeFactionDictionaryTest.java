package tm.m2twModPatcher.lib.managers.garrisons;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Tomek on 2016-11-13.
 */
public class UnitTypeFactionDictionaryTest {

	@Test
	public void addFactionList() throws Exception {
		UnitTypeFactionDictionary dict = new UnitTypeFactionDictionary();

		UnitGarrisonInfo unit1 = new UnitGarrisonInfo("Spear Militia");
		UnitGarrisonInfo unit2 = new UnitGarrisonInfo("Spear Militia Spain");

		dict.add(UnitType.LevyInfantry, Arrays.asList("aragon" , "spain"), unit1);
		dict.add(UnitType.LevyInfantry, Arrays.asList("portugal"), unit2);

		UnitGarrisonInfo result1 = dict.loadFirst(UnitType.LevyInfantry, "aragon");
		assertEquals(unit1.Name, result1.Name);

		result1 = dict.loadFirst(UnitType.LevyInfantry, "spain");
		assertEquals(unit1.Name, result1.Name);

		UnitGarrisonInfo result2 = dict.loadFirst(UnitType.LevyInfantry, "portugal");
		assertEquals(unit2.Name, result2.Name);
	}

	@Test
	public void addSingleByKeyValue_ShouldFind() throws Exception {
		UnitTypeFactionDictionary dict = new UnitTypeFactionDictionary();

		UnitTypeFactionKey key = new UnitTypeFactionKey(UnitType.LevyInfantry, "aragon");
		UnitGarrisonInfo unit = new UnitGarrisonInfo("Spear Militia");

		dict.add(key, unit);

		UnitGarrisonInfo result = dict.loadFirst(key);
		assertNotNull(result);

		assertEquals(unit.Name, result.Name);
	}

	@Test
	public void addTwoByKeyValue_ShouldFindProperly() throws Exception {
		UnitTypeFactionDictionary dict = new UnitTypeFactionDictionary();

		UnitTypeFactionKey key1 = new UnitTypeFactionKey(UnitType.LevyInfantry, "aragon");
		UnitGarrisonInfo unit1 = new UnitGarrisonInfo("Spear Militia");

		dict.add(key1, unit1);

		UnitTypeFactionKey key2 = new UnitTypeFactionKey(UnitType.LevyInfantry, "spain");
		UnitGarrisonInfo unit2 = new UnitGarrisonInfo("Spear Militia Spain");

		dict.add(key2, unit2);

		UnitGarrisonInfo result1 = dict.loadFirst(key1);
		assertEquals(unit1.Name, result1.Name);

		UnitGarrisonInfo result2 = dict.loadFirst(key2);
		assertEquals(unit2.Name, result2.Name);
	}

	@Test
	public void addSingleByKeyValue_Duplicated_ShouldFind() throws Exception {
		UnitTypeFactionDictionary dict = new UnitTypeFactionDictionary();

		UnitTypeFactionKey key = new UnitTypeFactionKey(UnitType.LevyInfantry, "aragon");
		UnitGarrisonInfo unit = new UnitGarrisonInfo("Spear Militia");

		dict.add(key, unit);

		dict.add(key, unit);	// should throw
	}

}