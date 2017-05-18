package tm.mtwModPatcher.lib.common.scripting.campaignScript;

import org.junit.Test;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.blocks.MonitorEventBlock;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.EventType;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Tomek on 2016-11-17.
 */
public class MonitorEventBlockTest {

	@Test
	public void createEmptyMonitor_getLines_ShourlReturnProper() {
		MonitorEventBlock event = new MonitorEventBlock(EventType.CharacterTurnStart, "SomeCondition");

		List<String> lines = event.getScriptBlock().getLines();

		assertNotNull(lines);
		assertEquals(2, lines.size());

		String line = lines.get(0);
		assertTrue( line.contains("monitor_event") );
		assertTrue( line.contains(EventType.CharacterTurnStart.toString()) );
		assertTrue( line.contains("SomeCondition") );

		line = lines.get(1);
		assertTrue( line.contains("end_monitor") );
	}

	@Test
	public void addCommands_getLines_ShourlReturnProper() {
		MonitorEventBlock event = new MonitorEventBlock(EventType.CharacterTurnStart, "SomeCondition");

		event.add("Some Command");

		List<String> lines = event.getScriptBlock().getLines();

		assertNotNull(lines);
		assertEquals(3, lines.size());

		String line = lines.get(1);
		assertTrue( line.contains("Some Command") );
	}

}