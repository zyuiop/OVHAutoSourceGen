package net.zyuiop.autosrcgen.types;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zyuiop
 */
public class TypeMap {
	private static Pattern generic = Pattern.compile("<([a-zA-Z.]+)>");
	private HashMap<String, TypeIdentifier> map = new HashMap<>();

	{
		register(new TypeIdentifier("time", "String", "java.lang.String"));
		register(new TypeIdentifier("string", "String", "java.lang.String"));
		register(new TypeIdentifier("ip", "String", "java.lang.String"));
		register(new TypeIdentifier("ipv4", "String", "java.lang.String"));
		register(new TypeIdentifier("ipv6", "String", "java.lang.String"));
		register(new TypeIdentifier("ipBlock", "String", "java.lang.String"));
		register(new TypeIdentifier("ipv4Block", "String", "java.lang.String"));
		register(new TypeIdentifier("phoneNumber", "String", "java.lang.String"));
		register(new TypeIdentifier("ipInterface", "String", "java.lang.String"));
		register(new TypeIdentifier("text", "String", "java.lang.String"));
		register(new TypeIdentifier("duration:string", "String", "java.lang.String"));
		register(new TypeIdentifier("coretypes.AccountId:string", "String", "java.lang.String"));
		register(new TypeIdentifier("coretypes.CountryEnum", "String", "java.lang.String"));
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
			System.out.println("Register " + identifier.getRestName());
			map.put(identifier.getRestName(), identifier);
		}

		if (!identifier.getRestName().contains("[]"))
			register(identifier.arrayCopy());
	}

	private String correctRestName(String restName) {
		if (restName.contains(".")) {
			// Namespace is lowercase !
			String[] parts = restName.split("\\.");
			String nameSpace = StringUtils.join(Arrays.copyOfRange(parts, 0, parts.length - 1), ".");
			nameSpace = nameSpace.toLowerCase();
			restName = nameSpace + "." + parts[parts.length - 1];
		}
		return restName;
	}

	public TypeIdentifier get(String restName) {
		Matcher matcher = TypeMap.generic.matcher(restName);
		if (matcher.find()) {
			try {
				// String generic = matcher.group(1);
				restName = correctRestName(matcher.replaceAll(""));
				TypeIdentifier typeIdentifier = map.get(restName);

				// Doesn't work as expected, needs quick rework
				// return typeIdentifier == null ? null : new GenericTypeIdentifier(typeIdentifier, generic);
				return typeIdentifier;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return null;
			}
		}
		return map.get(correctRestName(restName));
	}
}
