package net.zyuiop.autosrcgen.types;

/**
 * @author zyuiop
 */
public class GenericTypeIdentifier extends TypeIdentifier {
	private final String genericType;

	public GenericTypeIdentifier(TypeIdentifier identifier, String type) {
		super(identifier.getRestName(), identifier.getJavaName(), identifier.getJavaFullName());
		this.genericType = type;
	}

	public String getGenericType() {
		return genericType;
	}

	private boolean isArray(String name) {
		return name.contains("[]");
	}

	private String addType(String name) {
		if (isArray(name))
			return addType(name.replace("[]", "")) + "[]";
		return name + "<" + genericType + ">";
	}

	@Override
	public String getJavaName() {
		return addType(super.getJavaName());
	}

	@Override
	public String getJavaFullName() {
		return addType(super.getJavaFullName());
	}
}
