package net.zyuiop.autosrcgen.json;

/**
 * @author zyuiop
 */
public class Parameter {
	private int required;
	private String dataType;
	private String paramType;
	private String name;
	private String description;

	public Parameter() {
	}

	public boolean isRequired() {
		return required == 1;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
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
