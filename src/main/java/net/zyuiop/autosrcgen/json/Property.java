package net.zyuiop.autosrcgen.json;

/**
 * @author zyuiop
 */
public class Property {
	private String fullType;
	private int canBeNull;
	private String type;
	private String description;
	private int readOnly;

	public Property() {
	}

	public Property(int canBeNull, String type, String description) {
		this.canBeNull = canBeNull;
		this.type = type;
		this.description = description;
	}

	public String getFullType() {
		return fullType == null ? type : fullType;
	}

	public void setFullType(String fullType) {
		this.fullType = fullType;
	}

	public boolean isCanBeNull() {
		return canBeNull == 1;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isReadOnly() {
		return readOnly == 1;
	}

	public int getCanBeNull() {
		return canBeNull;
	}

	public void setCanBeNull(int canBeNull) {
		this.canBeNull = canBeNull;
	}

	public int getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(int readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public String toString() {
		return "Property{" +
				"fullType='" + fullType + '\'' +
				", canBeNull=" + canBeNull +
				", type='" + type + '\'' +
				", description='" + description + '\'' +
				", readOnly=" + readOnly +
				'}';
	}
}
