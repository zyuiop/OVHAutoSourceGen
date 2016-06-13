package net.zyuiop.autosrcgen.types;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zyuiop
 */
public class JavaReserved {
	private static Set<String> reserved = new HashSet<>();

	static {
		reserved.add("public");
		reserved.add("private");
		reserved.add("class");
		reserved.add("enum");
		reserved.add("default");
		reserved.add("interface");
		reserved.add("return");
	}

	public static String check(String name) {
		return reserved.contains(name) ? "_" + name : name;
	}
}
