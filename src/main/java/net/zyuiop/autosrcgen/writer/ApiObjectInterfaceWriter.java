package net.zyuiop.autosrcgen.writer;

import com.sun.xml.internal.ws.util.StringUtils;
import net.zyuiop.autosrcgen.AutomaticSourceGen;
import net.zyuiop.autosrcgen.json.Model;
import net.zyuiop.autosrcgen.json.Property;
import net.zyuiop.autosrcgen.types.TypeIdentifier;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author zyuiop
 */
public class ApiObjectInterfaceWriter extends ClassWriter {
	private final Model model;

	public ApiObjectInterfaceWriter(Model model) {
		super("api.objects." + model.getNamespace(), StringUtils.capitalize(model.getId()));
		this.model = model;
	}


	@Override
	public void write() throws IOException {
		FileWriter writer = newFileWriter();


		if (model.getEnumType() != null) {
			System.out.println("Aborting enum write.");
		} else {
			String methods = "";
			for (String propertyId : model.getProperties().keySet()) {
				Property property = model.getProperties().get(propertyId);

				TypeIdentifier typeIdentifier = AutomaticSourceGen.currentTypeIdentifier.get(property.getFullType());
				if (typeIdentifier.getJavaFullName() != null)
					writer.write("import " + typeIdentifier.getJavaFullName() + ";\n");

				methods += "	/**\n";
				methods += "	 * @return " + property.getDescription() + "\n";
				methods += "	 */\n";

				methods += "	" + typeIdentifier.getJavaName() + " get" + StringUtils.capitalize(propertyId) + "(); \n\n";
			}

			writer.write("/**\n");
			writer.write(" * " + model.getDescription() + "\n");
			writer.write(" */\n\n");
			writer.write("public interface " + targetClassName + " { \n\n" + methods);

		}

		AutomaticSourceGen.currentTypeIdentifier.register(new TypeIdentifier(model.getNamespace() + "." + model.getId(), targetClassName, "net.zyuiop.ovhapi." + targetPackage + "." + targetClassName));

		writer.write("}\n");

		writer.flush();
	}
}
