package net.zyuiop.autosrcgen.types;

/**
 * @author zyuiop
 */
public class TypeIdentifier {
	private String restName;
	private String javaName;
	private String javaFullName;

	public TypeIdentifier(String restName, String javaName, String javaFullName) {
		this.restName = restName;
		this.javaName = javaName;
		this.javaFullName = javaFullName;
	}

	public String getRestName() {
		return restName;
	}

	public String getJavaName() {
		return javaName;
	}

	public String getJavaFullName() {
		return javaFullName;
	}

	private String arrayRestName(String restName) {
		if (restName.contains(":")) {
			String[] parts = restName.split(":");
			return arrayRestName(parts[0]) + ":" + arrayRestName(parts[1]);
		}
		return restName + "[]";
	}

	public TypeIdentifier arrayCopy() {
		return new TypeIdentifier(arrayRestName(restName), javaName + "[]", javaFullName);
	}

	@Override
	public String toString() {
		return "TypeIdentifier{" +
				"restName='" + restName + '\'' +
				", javaName='" + javaName + '\'' +
				", javaFullName='" + javaFullName + '\'' +
				'}';
	}
}
