package net.zyuiop.autosrcgen.writer;

import net.zyuiop.autosrcgen.json.ApiDescriptorFile;

/**
 * @author zyuiop
 */
public class PathDescriptor {
	private String interfacePackage;
	private String interfaceName;
	private String classPackage;
	private String className;
	private ApiDescriptorFile descriptorFile;

	public PathDescriptor(String interfacePackage, String interfaceName, String classPackage, String className, ApiDescriptorFile descriptorFile) {
		this.interfacePackage = "net.zyuiop.ovhapi." + interfacePackage;
		this.interfaceName = interfaceName;
		this.classPackage = "net.zyuiop.ovhapi." + classPackage;
		this.className = className;
		this.descriptorFile = descriptorFile;
	}

	public ApiDescriptorFile getDescriptorFile() {
		return descriptorFile;
	}

	public String getFullInterfaceName() {
		return getInterfacePackage() + "." + getInterfaceName();
	}

	public String getFullClassName() {
		return getClassPackage() + "." + getClassName();
	}

	public String getInterfacePackage() {
		return interfacePackage;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getClassPackage() {
		return classPackage;
	}

	public String getClassName() {
		return className;
	}
}
