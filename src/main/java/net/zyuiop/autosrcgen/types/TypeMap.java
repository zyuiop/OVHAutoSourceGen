package net.zyuiop.autosrcgen.types;

import java.util.HashMap;

/**
 * @author zyuiop
 */
public class TypeMap {
	private HashMap<String, TypeIdentifier> map = new HashMap<>();

	{
		register(new TypeIdentifier("string", "String", "java.lang.String"));
		register(new TypeIdentifier("ip", "String", "java.lang.String"));
		register(new TypeIdentifier("ipv4", "String", "java.lang.String"));
		register(new TypeIdentifier("ipBlock", "String", "java.lang.String"));
		register(new TypeIdentifier("phoneNumber", "String", "java.lang.String"));
		register(new TypeIdentifier("ipInterface", "String", "java.lang.String"));
		register(new TypeIdentifier("text", "String", "java.lang.String"));
		register(new TypeIdentifier("coreTypes.AccountId:string", "String", "java.lang.String"));
		register(new TypeIdentifier("password", "String", "java.lang.String"));
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
