package net.zyuiop.autosrcgen.json;

/**
 * @author zyuiop
 */
public class Parameter {
	private Object required;
	private String dataType;
	private String paramType;
	private String name;
	private String description;

	public Parameter() {
	}

	public Object getRequired() {
		return required;
	}

	public boolean isRequired() {
		return required != null && (required instanceof Boolean ? (Boolean) required : ((Number) required).intValue() == 1);
	}

	public void setRequired(int required) {
		this.required = required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
