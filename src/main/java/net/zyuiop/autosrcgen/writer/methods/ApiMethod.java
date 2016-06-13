package net.zyuiop.autosrcgen.writer.methods;

import net.zyuiop.autosrcgen.AutomaticSourceGen;
import net.zyuiop.autosrcgen.json.Parameter;
import net.zyuiop.autosrcgen.types.JavaReserved;
import net.zyuiop.autosrcgen.types.TypeIdentifier;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zyuiop
 */
public class ApiMethod {
	private boolean         hasAuth;
	private String          mainDomain;
	private String          parentApi;
	private String          httpCode;
	private String          path;
	private String          description;
	private String          returnType; // To be improved
	private List<Parameter> arguments;

	public ApiMethod(boolean hasAuth, String mainDomain, String parentApi, String httpCode, String path, String description, String returnType, List<Parameter> arguments) {
		this.hasAuth = hasAuth;
		this.mainDomain = mainDomain;
		this.parentApi = parentApi;
		this.httpCode = httpCode;
		this.path = path;
		this.description = description;
		this.returnType = returnType;
		this.arguments = arguments;
	}

	public String getMethodSkell(boolean withFacultative) throws Exception {
		String methodName = httpCode.toLowerCase();

		String[] parts = path.replace(parentApi, "").split("/");
		int i = 0; // temp
		while (i < parts.length) {
			String part = parts[i];
			part = part.replace("$", "");
			methodName += StringUtils.capitalize(part);
			i++;
		}

		if (methodName.equalsIgnoreCase("get") && arguments.size() == 0) {
			methodName = "listAvailableServices";
		}

		String returnType = "void";
		if (!this.returnType.equalsIgnoreCase("void")) {
			TypeIdentifier identifier = AutomaticSourceGen.currentTypeIdentifier.get(this.returnType);
			if (identifier == null) {
				System.out.println("*** Missing identifier for " + this.returnType + " while generating " + httpCode + " " + path);
				throw new Exception("Missing identifier.");
			} else {
				returnType = identifier.getJavaFullName() == null ? identifier.getJavaName() : identifier.getJavaFullName();

				if (methodName.equalsIgnoreCase("get")) {
					String ret = identifier.getJavaName().replace("[]", "Array");
					methodName += StringUtils.capitalize(ret);
				}
			}
		}

		methodName = returnType + " " + methodName + "(" + StringUtils.join(buildArguments(withFacultative), ", ") + ")";

		return methodName;
	}

	private List<Parameter> getParams(boolean withFacultative) {
		List<Parameter> imperative = this.arguments.stream().filter(Parameter::isRequired).collect(Collectors.toList());
		List<Parameter> facultative = this.arguments.stream().filter(p -> !p.isRequired()).collect(Collectors.toList());

		if (withFacultative)
			imperative.addAll(facultative); // toujours à la fin
		return imperative;
	}

	private boolean hasFacultative() {
		for (Parameter parameter : arguments)
			if (!parameter.isRequired())
				return true;
		return false;
	}

	public void write(FileWriter writer, boolean isInterface) throws IOException {
		if (hasFacultative()) {
			write(writer, isInterface, true);
			writer.write("\n");
		}
		write(writer, isInterface, false);
	}

	private void write(FileWriter writer, boolean isInterface, boolean withFacultative) throws IOException {
		if (isInterface) {
			String method = "";
			method += "\t/**\n";
			method += "\t * " + description + "\n";
			method += "\t * Facultative parameters ? " + withFacultative + "\n";

			for (Parameter parameter : this.getParams(withFacultative)) {
				method += "	 * @param " + JavaReserved.check(parameter.getName()) + " " + parameter.getDescription() + "\n";
			}
			method += "\t*/\n";
			try {
				method += "\t" + getMethodSkell(withFacultative) + " throws java.io.IOException;\n";
			} catch (Exception e) {
				System.out.println("*** Method " + httpCode + " " + path + " was not written. " + e.getMessage());
				return; // on écrit pas la méthode.
			}
			writer.write(method);
		} else {
			String method = "";
			try {
				method += "\tpublic " + getMethodSkell(withFacultative) + " throws java.io.IOException {\n";
				String[] parts = path.replace(parentApi, "").split("/");
				String callUrl = "\"" + mainDomain;
				for (String part : parts) {
					if (part.startsWith("$")) {
						String paramName = JavaReserved.check(part.substring(1));
						callUrl += "/\" + " + paramName + " + \"";
					} else {
						callUrl += "/" + part;
					}
				}

				method += "\t\tString callUrl = " + callUrl + "\";\n";

				if (!this.httpCode.equalsIgnoreCase("get")) {
					method += "\t\tMap<Object, Object> dataMap = new HashMap<>();\n";
					for (Parameter parameter : this.getParams(withFacultative)) {
						if (parameter.getParamType().equalsIgnoreCase("body")) {
							method += "\t\tdataMap.put(\"" + parameter.getName() + "\", " + JavaReserved.check(parameter.getName()) + ");\n";
						}
					}
					method += "\t\tString data = new Gson().toJson(dataMap);\n";
				} else {
					method += "\t\tString data = \"?\";\n";
					for (Parameter parameter : this.getParams(withFacultative)) {
						if (parameter.getParamType().equalsIgnoreCase("body")) {
							method += "\t\tdata += \"" + parameter.getName() + "=\" + " + JavaReserved.check(parameter.getName()) + ";\n";
						}
					}
				}

				method += "\t\tOVHApiMethod method = OVHApiMethod." + httpCode + ";\n";
				method += "\t\tURL url = new URL(callUrl);\n";
				method += "\t\t";
				if (!this.returnType.equalsIgnoreCase("void")) method += "return new Gson().fromJson(";
				method += "this.client.callRaw(url, method, data, " + Boolean.toString(hasAuth) + ")";
				if (!this.returnType.equalsIgnoreCase("void")) {
					TypeIdentifier identifier = AutomaticSourceGen.currentTypeIdentifier.get(this.returnType);
					if (identifier == null) {
						System.out.println("*** Missing identifier for " + this.returnType + " while generating " + httpCode + " " + path);
						throw new Exception("Missing identifier.");
					}
					String returnType = identifier.getJavaFullName() == null ? identifier.getJavaName() : identifier.getJavaFullName();
					method += ", " + returnType + ".class);\n";
				} else {
					method += ";\n";
				}

				method += "\t}\n";
			} catch (Exception e) {
				System.out.println("*** Method " + httpCode + " " + path + " was not written. " + e.getMessage());
				return; // on écrit pas la méthode.
			}
			writer.write(method);
			// On va voir heeein
		}
	}

	private List<String> buildArguments(boolean withFacultative) throws Exception {
		List<String> arguments = new ArrayList<>();
		int i = 0;
		for (Parameter parameter : getParams(withFacultative)) {
			TypeIdentifier identifier = AutomaticSourceGen.currentTypeIdentifier.get(parameter.getDataType());
			if (identifier == null) {
				System.out.println("*** Missing identifier for " + parameter.getDataType());
				throw new Exception("Missing identifier.");
			}
			String name = parameter.getName();
			if (name == null)
				name = "param" + (i++);
			if (name.contains(".")) {
				String[] parts = name.split("\\.");
				name = parts[0];
				for (String part : Arrays.copyOfRange(parts, 1, parts.length))
					name += StringUtils.capitalize(part);
			}
			arguments.add((identifier.getJavaFullName() == null ? identifier.getJavaName() : identifier.getJavaFullName()) + " " + JavaReserved.check(name));
		}
		return arguments;
	}

	@Override
	public String toString() {
		return "ApiMethod{" +
				"httpCode='" + httpCode + '\'' +
				", path='" + path + '\'' +
				", returnType='" + returnType + '\'' +
				", arguments=" + arguments +
				'}';
	}
}