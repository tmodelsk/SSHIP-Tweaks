package tm.common;

import java.net.UnknownHostException;

/**
 * Created by tomek on 20.04.2017.
 */
public class EnvitonmentTm {

	public static String getUserNameLogged() {
		return System.getProperty("user.name");
	}

	public static String getHostName() throws UnknownHostException {
		java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
		return localMachine.getHostName();
	}

}
