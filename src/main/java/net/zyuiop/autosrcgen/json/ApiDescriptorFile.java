package net.zyuiop.autosrcgen.json;

import java.util.Map;

/**
 * @author zyuiop
 */
public class ApiDescriptorFile {
	private String             basePath;
	private String             resourcePath;
	private String             apiVersion;
	private Map<String, Model> models;
	private Api[]			   apis;

	public ApiDescriptorFile(String basePath, String resourcePath, String apiVersion, Map<String, Model> models, Api[] apis) {
		this.basePath = basePath;
		this.resourcePath = resourcePath;
		this.apiVersion = apiVersion;
		this.models = models;
		this.apis = apis;
	}

	public ApiDescriptorFile() {
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public Map<String, Model> getModels() {
		return models;
	}

	public void setModels(Map<String, Model> models) {
		this.models = models;
	}

	public Api[] getApis() {
		return apis;
	}

	public void setApis(Api[] apis) {
		this.apis = apis;
	}
}
