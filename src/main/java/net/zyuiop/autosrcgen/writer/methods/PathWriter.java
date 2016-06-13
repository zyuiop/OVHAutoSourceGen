package net.zyuiop.autosrcgen.writer.methods;

import net.zyuiop.autosrcgen.json.ApiDescriptorFile;
import net.zyuiop.autosrcgen.writer.ClassWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zyuiop
 */
public class PathWriter extends ClassWriter {
	private boolean           isInterface;
	private ApiDescriptorFile api;
	private Set<ApiMethod> methodList = new HashSet<>();

	public PathWriter(String targetPackage, String targetClassName, boolean isInterface, ApiDescriptorFile api) {
		super(targetPackage, targetClassName);
		this.isInterface = isInterface;
		this.api = api;
	}

	public void addMethod(ApiMethod method) {
		methodList.add(method);
	}

	public Set<ApiMethod> getMethodList() {
		return methodList;
	}

	public void setMethodList(Set<ApiMethod> methodList) {
		this.methodList = methodList;
	}

	@Override
	public void write() throws IOException {
		if (isInterface)
			writeInterface();
		else
			writeClass();
	}

	private void writeInterface() throws IOException {
		FileWriter writer = newFileWriter();

		writer.write("/**\n");
		writer.write(" * A class to interact with OVH's " + api.getResourcePath() + " APIs\n");
		writer.write(" */\n\n");
		writer.write("public interface " + targetClassName + " { \n\n");
		for (ApiMethod method : methodList) {
			method.write(writer, true);
			writer.write("\n");
		}
		writer.write("}\n");
		writer.flush();
	}

	private void writeClass() throws IOException {
		FileWriter writer = newFileWriter();
		String interfacePackage = "net.zyuiop.ovhapi." + this.targetPackage.replace("impl.methods", "api.methods");
		writer.write("import " + interfacePackage + "." + (targetClassName.substring(0, targetClassName.length() - 4)) + ";\n");
		writer.write("import com.google.gson.Gson;\n" +
				"import net.zyuiop.ovhapi.api.OVHApiMethod;\n" +
				"import java.util.HashMap;\n" +
				"import java.util.Map;\n" +
				"import net.zyuiop.ovhapi.impl.OVHRawCalls;\n" +
				"import java.net.URL;\n");

		writer.write("public class " + targetClassName + " implements " + (targetClassName.substring(0, targetClassName.length() - 4)) + " { \n\n");
		writer.write("\tprivate final OVHRawCalls client;\n\n");
		writer.write("\tpublic " + targetClassName + "(OVHRawCalls client) { \n\t\tthis.client = client;\n\t}\n\n");
		for (ApiMethod method : methodList) {
			method.write(writer, false);
			writer.write("\n");
		}
		writer.write("}\n");
		writer.flush();
	}
}
