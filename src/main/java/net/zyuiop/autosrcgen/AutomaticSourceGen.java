package net.zyuiop.autosrcgen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import net.zyuiop.autosrcgen.json.Api;
import net.zyuiop.autosrcgen.json.ApiDescriptorFile;
import net.zyuiop.autosrcgen.json.Model;
import net.zyuiop.autosrcgen.types.TypeMap;
import net.zyuiop.autosrcgen.writer.ApiObjectImplWriter;
import net.zyuiop.autosrcgen.writer.ApiObjectInterfaceWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author zyuiop
 */
public class AutomaticSourceGen {
	private static final Gson gson = new Gson();
	public static String target;
	public static TypeMap currentTypeIdentifier = new TypeMap();
	private static Queue<ApiDescriptorFile> descriptors = new ArrayDeque<>();

	public static void main(String[] args) throws IOException {
		String directory;
		Scanner s = new Scanner(System.in);
		if (args.length > 0)
			directory = args[0];
		else {
			System.out.print("Path ? ");
			directory = s.nextLine();
		}

		System.out.print("Target main/ directory ? ");
		target = s.nextLine();

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
						System.out.println("   |- Creating interface...");
						new ApiObjectInterfaceWriter(model).write();
						System.out.println("   |- Creating implementation...");
						new ApiObjectImplWriter(model).write();
						map.remove(modelName);
					}
				}

				if (copy.size() > 0 && copy.size() == map.size()) {
					System.out.println("*** An error occured ! Infinite loop interrupted in " + k + "th loop. ***");
					System.out.println("*** Remaining classes to proceed : " + map);
					break;
				}
			}

			descriptors.add(descriptorFile);
		}
	}

	private static void processApis() {
		System.out.println("- Processing APIs...");
		Set<String> paths = new HashSet<>();

		for (ApiDescriptorFile descriptorFile : descriptors) {

			for (Api api : descriptorFile.getApis()) {
				paths.add(descriptorFile.getResourcePath() + api.getPath());
			}
		}

		Multimap<String, String> created = HashMultimap.create();
		for (String path : paths) {
			String[] parts = path.split("/");
			String previousFound = null;
			for (int i = 0; i < parts.length; i++) {
				String testString = StringUtils.join(Arrays.copyOfRange(parts, 0, i), "/");
				if (created.containsKey(testString))
					previousFound = testString;
				else {
					if (previousFound != null)
						created.put(previousFound, parts[i]);
					previousFound = testString;
				}
			}
		}



		// EN GROS :
		// Si created[key + value] existe pas -> value est une méthode "directe" de key
		// Si created[key + value] existe -> value est une classe (et une méthode de key, retournant une instance de cette classe)
		// {truc} est toujours un paramètre. La méthode correspondante se nomme alors en fonction de l'opération (get/put/delete/post)

		System.out.println(created);
	}
}
