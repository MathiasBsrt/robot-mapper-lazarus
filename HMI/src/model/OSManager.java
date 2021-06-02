package model;

import java.util.Locale;

public class OSManager {

	public static boolean isOSWindows() {
		String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		return osName.indexOf("win") >= 0;
	}

	public static boolean isOSLinux() {
		String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		return osName.indexOf("nux") >= 0;
	}

	public static boolean isMacOS() {
		String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		return osName.indexOf("mac") >= 0 || osName.indexOf("darwin") >= 0;
	}

}

