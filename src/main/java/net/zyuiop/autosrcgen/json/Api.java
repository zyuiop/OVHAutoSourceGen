package net.zyuiop.autosrcgen.json;

/**
 * @author zyuiop
 */
public class Api {
	private String path;
	private String description;
	private Operation[] operations;

	public Api(String path, String description, Operation[] operations) {
		this.path = path;
		this.description = description;
		this.operations = operations;
	}

	public Api() {

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Operation[] getOperations() {
		return operations;
	}

	public void setOperations(Operation[] operations) {
		this.operations = operations;
	}
}
