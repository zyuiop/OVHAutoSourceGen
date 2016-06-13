package net.zyuiop.autosrcgen.json;

import net.zyuiop.autosrcgen.AutomaticSourceGen;
import net.zyuiop.autosrcgen.types.TypeIdentifier;

import java.util.Map;

/**
 * @author zyuiop
 */
public class Model {
	private String namespace;
	private String id;
	private String description;

	// Facultatif : enum
	private Object[] enumValues;
	private String enumType;

	// Facultatif : pas enum
	private Map<String, Property> properties;

	public Model() {
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object[] getEnum() {
		return enumValues;
	}

	public void setEnum(Object[] enumValues) {
		this.enumValues = enumValues;
	}

	public String getEnumType() {
		return enumType;
	}

	public void setEnumType(String enumType) {
		this.enumType = enumType;
	}

	public Object[] getEnumValues() {
		return enumValues;
	}

	public void setEnumValues(Object[] enumValues) {
		this.enumValues = enumValues;
	}

	public Map<String, Property> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}

	public boolean canWrite() {
		if (enumType != null) {
			TypeIdentifier identifier = AutomaticSourceGen.currentTypeIdentifier.get(enumType);
			if (identifier == null) {
				System.out.println("x|-> Can't write " + getNamespace() + "." + getId() + " yet : missing " + enumType);
				return false;
			}

			AutomaticSourceGen.currentTypeIdentifier.register(new TypeIdentifier(getNamespace() + "." + getId(), identifier.getJavaName(), identifier.getJavaFullName()));
			return true; // TODO : may not work
		}

		for (Property property : properties.values()) {
			if (AutomaticSourceGen.currentTypeIdentifier.get(property.getFullType()) == null) {
				System.out.println("  x|-> Can't write " + getNamespace() + "." + getId() + " yet : missing " + property.getFullType());
				return false;
			}
		}
		return true;
	}
}
