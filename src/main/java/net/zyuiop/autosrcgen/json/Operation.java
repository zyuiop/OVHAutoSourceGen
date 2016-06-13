package net.zyuiop.autosrcgen.json;

/**
 * @author zyuiop
 */
public class Operation {
	private String httpMethod;
	private Object noAuthentication;
	private String description;
	private String responseType;

	public Operation() {
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public boolean isNoAuthentication() {
		return (noAuthentication instanceof Boolean ? (Boolean) noAuthentication : (((Integer) noAuthentication) == 1));
	}

	// WTF FUCK YOU OVH
	public void setNoAuthentication(int noAuthentication) {
		this.noAuthentication = noAuthentication;
	}

	public void setNoAuthentication(boolean noAuthentication) {
		this.noAuthentication = noAuthentication;
	}

	public void setNoAuthentication(Object noAuthentication) {
		this.noAuthentication = noAuthentication;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
}
