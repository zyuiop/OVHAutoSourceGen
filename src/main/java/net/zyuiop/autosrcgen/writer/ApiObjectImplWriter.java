package net.zyuiop.autosrcgen.writer;

import com.sun.xml.internal.ws.util.StringUtils;
import net.zyuiop.autosrcgen.AutomaticSourceGen;
import net.zyuiop.autosrcgen.json.Model;
import net.zyuiop.autosrcgen.json.Property;
import net.zyuiop.autosrcgen.types.JavaReserved;
import net.zyuiop.autosrcgen.types.TypeIdentifier;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zyuiop
 */
public class ApiObjectImplWriter extends ClassWriter {
	private final Model model;

	public ApiObjectImplWriter(Model model) {
		super("impl.objects." + model.getNamespace(), StringUtils.capitalize(model.getId()) + "Impl");
		this.model = model;
	}


	@Override
	public void write() throws IOException {
		FileWriter writer = newFileWriter();

		if (model.getEnumType() != null) {
			System.out.println("Aborting enum write.");
		} else {
			// Imports parent interface
			writer.write("import net.zyuiop.ovhapi.api.objects." + model.getNamespace() + "." + StringUtils.capitalize(model.getId()) + ";\n");

			String methods = "";
			String fields = "";
			//List<String> constructor = new ArrayList<>();

			for (String propertyId : model.getProperties().keySet()) {
				Property property = model.getProperties().get(propertyId);

				TypeIdentifier typeIdentifier = AutomaticSourceGen.currentTypeIdentifier.get(property.getFullType());
				if (typeIdentifier.getJavaFullName() != null)
					writer.write("import " + typeIdentifier.getJavaFullName() + ";\n");

				String fName = JavaReserved.check(propertyId);

				fields +=  "	private " + typeIdentifier.getJavaName() + " " + fName + ";\n";
				//constructor.add(typeIdentifier.getJavaName() + " " + fName);

				methods += "	public " + typeIdentifier.getJavaName() + " get" + StringUtils.capitalize(propertyId) + "() { \n";
				methods += "		return this." + fName + ";\n";
				methods += "	} \n\n";

				methods += "	public void set" + StringUtils.capitalize(propertyId) + "(" + typeIdentifier.getJavaName() + " " + fName + ") { \n";
				methods += "		this." + fName + " = " + fName + ";\n";
				methods += "	} \n\n";

				// Flow
				methods += "	public " + targetClassName + " " + fName + "(" + typeIdentifier.getJavaName() + " " + fName + ") { \n";
				methods += "		this." + fName + " = " + fName + ";\n";
				methods += "		return this;\n";
				methods += "	} \n\n";
			}

			writer.write("/**\n");
			writer.write(" * " + model.getDescription() + "\n");
			writer.write(" */\n\n");
			writer.write("public class " + targetClassName + " implements " + StringUtils.capitalize(model.getId()) + " { \n\n");
			writer.write(fields + "\n");
			writer.write("\tpublic " + targetClassName + "() {\n\t}\n\n");
			writer.write(methods);
		}


		writer.write("}\n");

		writer.flush();
	}
}
