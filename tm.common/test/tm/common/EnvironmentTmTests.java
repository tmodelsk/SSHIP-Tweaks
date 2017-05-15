package tm.common;

import lombok.val;
import org.junit.Test;
import tm.common.EnvitonmentTm;

import java.net.UnknownHostException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by tomek on 20.04.2017.
 */
public class EnvironmentTmTests {

	@Test
	public void getHostName_ShouldReturn() throws UnknownHostException {
		val hostNameStr = EnvitonmentTm.getHostName();

		assertNotNull(hostNameStr);
		assertTrue("IsNotEmpty", hostNameStr.length() > 0);
	}
}
