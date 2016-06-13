package net.zyuiop.autosrcgen.types;

import java.util.HashMap;

/**
 * @author zyuiop
 */
public class TypeMap {
	private HashMap<String, TypeIdentifier> map = new HashMap<>();

	{
		register(new TypeIdentifier("string", "String", null));
		register(new TypeIdentifier("ip", "String", null));
		register(new TypeIdentifier("ipv4", "String", null));
		register(new TypeIdentifier("ipBlock", "String", null));
		register(new TypeIdentifier("ipInterface", "String", null));
		register(new TypeIdentifier("text", "String", null));
		register(new TypeIdentifier("coreTypes.AccountId:string", "String", null));
		register(new TypeIdentifier("password", "String", null));
		registerJavaType("boolean");
		registerJavaType("double");
		registerJavaType("long");
		registerJavaType("int");
		register(new TypeIdentifier("datetime", "Date", "java.util.Date"));
		register(new TypeIdentifier("date", "Date", "java.util.Date"));
	}

	public void registerJavaType(String type) {
		register(new TypeIdentifier(type, type, null));
	}

	public void register(TypeIdentifier identifier) {
		if (!map.containsKey(identifier.getRestName())) {
			map.put(identifier.getRestName(), identifier);
		}

		if (!identifier.getRestName().contains("[]"))
			register(identifier.arrayCopy());
	}

	public TypeIdentifier get(String restName) {
		return map.get(restName);
	}
}
