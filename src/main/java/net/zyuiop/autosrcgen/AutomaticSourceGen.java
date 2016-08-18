package net.zyuiop.autosrcgen;

import com.google.gson.Gson;
import net.zyuiop.autosrcgen.json.Api;
import net.zyuiop.autosrcgen.json.ApiDescriptorFile;
import net.zyuiop.autosrcgen.json.Model;
import net.zyuiop.autosrcgen.json.Operation;
import net.zyuiop.autosrcgen.types.TypeMap;
import net.zyuiop.autosrcgen.writer.ApiObjectImplWriter;
import net.zyuiop.autosrcgen.writer.MainClassWriter;
import net.zyuiop.autosrcgen.writer.MainInterfaceWriter;
import net.zyuiop.autosrcgen.writer.PathDescriptor;
import net.zyuiop.autosrcgen.writer.methods.ApiMethod;
import net.zyuiop.autosrcgen.writer.methods.PathWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.security.CodeSource;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author zyuiop
 */
public class AutomaticSourceGen {
	private static final Gson gson = new Gson();
	public static String target;
	public static TypeMap currentTypeIdentifier = new TypeMap();
	private static Queue<ApiDescriptorFile> descriptors = new ArrayDeque<>();
	private static List<Model> notWritten = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		String directory;
		Scanner s = new Scanner(System.in);
		if (args.length > 0)
			directory = args[0];
		else {
			System.out.print("Path ? ");
			directory = s.nextLine();
		}

		if (args.length > 1)
			target = args[1];
		else {
			System.out.print("Target main/ directory ? ");
			target = s.nextLine();
		}

		File dirFile = new File(directory);
		treat(dirFile);

		processApis();
	}

	private static void treat(File file) throws IOException {
		if (file == null)
			return;
		if (file.isDirectory())
			for (File f : file.listFiles())
				treat(f);
		else {
			FileReader reader = new FileReader(file);
			System.out.println("Reading " + file.getPath());
			ApiDescriptorFile descriptorFile = gson.fromJson(reader, ApiDescriptorFile.class);

			System.out.println("Loaded descriptor for " + descriptorFile.getResourcePath());
			Map<String, Model> map = new HashMap<>(descriptorFile.getModels());

			System.out.println("- Processing models...");

			int k = 0;
			while (!map.isEmpty()) {
				k++;
				Map<String, Model> copy = new HashMap<>(map);

				for (String modelName : copy.keySet()) {
					Model model = copy.get(modelName);
					if (model.canWrite()) {
						System.out.println(" |- Processing model " + model.getNamespace() + " / " + model.getId());

						if (model.getEnumType() != null) {
							map.remove(modelName);
							continue;
						}

						// Création de l'interface
						/*System.out.println("   |- Creating interface...");
						new ApiObjectInterfaceWriter(model).write();*/
						System.out.println("   |- Creating implementation...");
						new ApiObjectImplWriter(model).write();
						map.remove(modelName);
					}
				}

				if (copy.size() > 0 && copy.size() == map.size()) {
					System.out.println("*** An error occured ! Infinite loop interrupted in " + k + "th loop. ***");
					System.out.println("*** Remaining classes to proceed : " + map);
					notWritten.addAll(map.values());
					break;
				}
			}

			descriptors.add(descriptorFile);
		}

		System.out.println("Last chance write : " + notWritten.size() + " remaining entries.");

		int k = 0;
		while (!notWritten.isEmpty()) {
			k++;
			Iterator<Model> modelIterator = notWritten.iterator();
			int size = notWritten.size();
			while (modelIterator.hasNext()) {
				Model model = modelIterator.next();
				if (model.canWrite()) {
					System.out.println(" |- Processing model " + model.getNamespace() + " / " + model.getId());

					if (model.getEnumType() != null) {
						modelIterator.remove();
						continue;
					}

					// Création de l'interface
					//System.out.println("   |- Creating interface...");
					//new ApiObjectInterfaceWriter(model).write();
					System.out.println("   |- Creating implementation...");
					new ApiObjectImplWriter(model).write();
					modelIterator.remove();
				}
			}

			if (size == notWritten.size()) {
				System.out.println("*** An error occured ! Infinite loop interrupted in " + k + "th loop. ***");
				System.out.println("*** Remaining classes to proceed : " + modelIterator);
				break;
			}
		}
	}

	private static void processApis() {
		System.out.println("- Processing APIs...");
		Set<String> paths = new HashSet<>();

		Set<PathDescriptor> descriptorSet = new HashSet<>();
		for (ApiDescriptorFile descriptorFile : descriptors) {
			Set<ApiMethod> methods = new HashSet<>();
			for (Api api : descriptorFile.getApis()) {
				paths.add(descriptorFile.getResourcePath() + api.getPath());

				String path = api.getPath();
				String[] parts = path.split("/");
				int i = 0;
				for (String part : parts) {
					if (part.startsWith("{") && part.endsWith("}")) {
						parts[i] = "$" + part.substring(1, part.length() - 1);
					}
					i++;
				}

				for (Operation operation : api.getOperations()) {
					methods.add(new ApiMethod(!operation.isNoAuthentication(), descriptorFile.getBasePath(), descriptorFile.getResourcePath() + "/", operation.getHttpMethod(), StringUtils.join(parts, "/"), operation.getDescription(), operation.getResponseType(), operation.getParameters()));
				}
			}

			String[] parts = descriptorFile.getResourcePath().split("/");
			String className = "";
			String packageName = "api.methods";
			String altPackageName = "impl.methods";
			int i = 0;
			for (String part : parts) {
				if (part.length() < 1) {
					i++;
					continue;
				}

				if (i == parts.length - 1) {
					className = StringUtils.capitalize(part);
				} else {
					packageName += "." + part.toLowerCase();
					altPackageName += "." + part.toLowerCase();
				}
				i++;
			}

			PathWriter intWriter = new PathWriter(packageName, className, true, descriptorFile);
			PathWriter clazzWriter = new PathWriter(altPackageName, className + "Impl", false, descriptorFile);
			try {
				intWriter.setMethodList(methods);
				intWriter.write();
				clazzWriter.setMethodList(methods);
				clazzWriter.write();
			} catch (IOException e) {
				e.printStackTrace();
			}

			descriptorSet.add(new PathDescriptor(packageName, className, altPackageName, className + "Impl", descriptorFile));
		}

		System.out.println("- Processing main class...");
		try {
			new MainClassWriter(descriptorSet).write();
			new MainInterfaceWriter(descriptorSet).write();
		} catch (IOException e) {
			e.printStackTrace();
		}

		CodeSource src = AutomaticSourceGen.class.getProtectionDomain().getCodeSource();
		if (src != null) {
			URL jar = src.getLocation();
			try {
				URI uri = URI.create("jar:file:" + jar.toURI().getPath());

				FileSystem system;
				try {
					system = FileSystems.newFileSystem(uri, new HashMap<>());
					Stream<Path> stream = Files.walk(system.getPath("/raw/"));

					stream.forEach(path -> {
						if (Files.isDirectory(path))
							return;

						Path target = Paths.get(AutomaticSourceGen.target + "/java/net/zyuiop/ovhapi/", path.toString().replace("/raw/", ""));
						try {
							System.out.println("- Copying " + path + " to " + target);
							Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
}
