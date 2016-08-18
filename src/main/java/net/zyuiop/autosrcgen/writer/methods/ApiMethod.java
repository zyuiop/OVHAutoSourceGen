package net.zyuiop.autosrcgen.writer.methods;

import com.google.gson.Gson;
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
				System.out.println("*** Missing identifier for " + this.returnType + " while generating method skel for " + httpCode + " " + path);
				throw new Exception("Missing identifier.");
			} else {
				returnType = identifier.getJavaFullName() == null ? identifier.getJavaName() : identifier.getJavaFullName();

				if (this.returnType.contains("[]") && !returnType.contains("[]"))
					returnType = returnType + "[]";

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
				String callUrl = "\"" + mainDomain + parentApi;
				while (callUrl.endsWith("/"))
					callUrl = callUrl.substring(0, callUrl.length() - 1);

				for (String part : parts) {
					if (part.startsWith("$")) {
						String paramName = JavaReserved.check(part.substring(1));
						callUrl += "/\" + " + paramName + " + \"";
					} else if (part.length() > 0) {
						callUrl += "/" + part;
					}
				}

				method += "\t\tString __callUrl = " + callUrl + "\";\n";

				if (!this.httpCode.equalsIgnoreCase("get")) {
					boolean nullParam = false;
					for (Parameter parameter : this.getParams(withFacultative)) {
						if (parameter.getParamType().equalsIgnoreCase("body")) {
							if (parameter.getName() == null) {
								method += "\t\tString __data = new Gson().toJson(param0);\n"; // seems to work, might not be good on every method
								nullParam = true;
							}
						}
					}

					if (!nullParam) {
						method += "\t\tMap<Object, Object> __dataMap = new HashMap<>();\n";
						for (Parameter parameter : this.getParams(withFacultative)) {
							if (parameter.getParamType().equalsIgnoreCase("body")) {
								method += "\t\t__dataMap.put(\"" + parameter.getName() + "\", " + JavaReserved.check(parameter.getName()) + ");\n";
							}
						}
						method += "\t\tString __data = new Gson().toJson(__dataMap);\n";
					}
				} else {
					List<String> tmp = new ArrayList<>();
					int i = 0;
					for (Parameter parameter : getParams(withFacultative)) {
						TypeIdentifier identifier = AutomaticSourceGen.currentTypeIdentifier.get(parameter.getDataType());
						if (identifier == null) {
							continue;
						}

						String name = parameter.getName();
						if (name == null)
							name = "param" + (i++);
						if (name.contains(".")) {
							String[] k = name.split("\\.");
							name = k[0];
							for (String part : Arrays.copyOfRange(k, 1, k.length))
								name += StringUtils.capitalize(part);
						}
						if (!parameter.getParamType().equalsIgnoreCase("body")) {
							tmp.add("\t\t__data += \"" + parameter.getName() + "=\" + " + JavaReserved.check(name) + ";\n");
						}
					}

					if (tmp.size() > 0) {
						method += "\t\tString __data = \"?\";\n";
						method += StringUtils.join(tmp, "");
					} else {
						method += "\t\tString __data = \"\";\n";
					}
				}

				method += "\t\tOVHApiMethod __method = OVHApiMethod." + httpCode + ";\n";
				method += "\t\tURL __url = new URL(__callUrl);\n";
				method += "\t\t";
				if (!this.returnType.equalsIgnoreCase("void")) method += "return new Gson().fromJson(";
				method += "this.client.callRaw(__url, __method, __data, " + Boolean.toString(hasAuth) + ")";
				if (!this.returnType.equalsIgnoreCase("void")) {
					TypeIdentifier identifier = AutomaticSourceGen.currentTypeIdentifier.get(this.returnType);
					if (identifier == null) {
						System.out.println("*** Missing identifier for " + this.returnType + " while generating " + httpCode + " " + path);
						throw new Exception("Missing identifier.");
					}
					String returnType = identifier.getJavaFullName() == null ? identifier.getJavaName() : identifier.getJavaFullName();
					/*if (returnType.contains("net.zyuiop.ovhapi.api"))
						returnType = returnType.replace("net.zyuiop.ovhapi.api.", "net.zyuiop.ovhapi.impl.") + "Impl";
						Removed : no objects implementations anymore
						*/
					if (this.returnType.contains("[]") && !returnType.contains("[]"))
						returnType = returnType + "[]";

					method += ", " + returnType + ".class);\n";
				} else {
					method += ";\n";
				}

				method += "\t}\n";
			} catch (Exception e) {
				System.out.println("*** Method " + httpCode + " " + path + " was not written. " + e.getMessage());
				writer.write("\n\t/*" +
						"\n\t* Method creation failed." +
						"\n\t* Involved method : " + httpCode + " > " + path +
						"\n\t* Message : " + e.getMessage() +
						"\n\t*/\n\n");
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
