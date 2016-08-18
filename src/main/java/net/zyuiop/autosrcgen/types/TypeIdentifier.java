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

	public TypeIdentifier arrayCopy() {
		return new TypeIdentifier(restName + "[]", javaName + "[]", javaFullName);
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
