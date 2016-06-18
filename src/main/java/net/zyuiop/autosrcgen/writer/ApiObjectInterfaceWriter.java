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
		super("api.objects." + model.getNamespace().toLowerCase(), StringUtils.capitalize(model.getId()));
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
				String name;

				if (model.isGeneric(property.getFullType()))
					name = property.getFullType();
				else {
					TypeIdentifier typeIdentifier = AutomaticSourceGen.currentTypeIdentifier.get(property.getFullType());
					name = typeIdentifier.getJavaFullName();
					if (name == null)
						name = typeIdentifier.getJavaName();

					if (property.getFullType().contains("[]") && !name.contains("[]"))
						name = name + "[]";
				}

				propertyId = propertyId.replaceAll("-", "");

				methods += "	/**\n";
				methods += "	 * @return " + property.getDescription() + "\n";
				methods += "	 */\n";

				methods += "	" + name + " get" + StringUtils.capitalize(propertyId) + "(); \n\n";
			}

			writer.write("/**\n");
			writer.write(" * " + model.getDescription() + "\n");
			writer.write(" */\n\n");
			String generics = "";
			if (model.getGenerics() != null)
				generics = "<" + org.apache.commons.lang3.StringUtils.join(model.getGenerics(), ", ") + ">";
			writer.write("public interface " + targetClassName + generics + " { \n\n" + methods);

		}

		AutomaticSourceGen.currentTypeIdentifier.register(new TypeIdentifier(model.getNamespace().toLowerCase() + "." + model.getId(), targetClassName, "net.zyuiop.ovhapi." + targetPackage + "." + targetClassName));

		writer.write("}\n");

		writer.flush();
	}
}
