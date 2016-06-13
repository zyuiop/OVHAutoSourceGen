package net.zyuiop.autosrcgen.writer;

import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * @author zyuiop
 */
public class MainClassWriter extends ClassWriter {
	private final Set<PathDescriptor> descriptorSet;

	public MainClassWriter(Set<PathDescriptor> descriptorSet) {
		super("impl", "OVHClientImpl");
		this.descriptorSet = descriptorSet;
	}

	@Override
	public void write() throws IOException {
		FileWriter writer = newFileWriter();

		writer.write("import net.zyuiop.ovhapi.api.OVHClient;\n\n");

		writer.write(
				"public class OVHClientImpl extends OVHRawCalls implements OVHClient {\n" +
						"\tpublic OVHClientImpl(String appKey, String appSecret, String consumerKey) {\n" +
						"\t\tsuper(appKey, appSecret, consumerKey);\n" +
						"\t}\n" +
						"\n" +
						"\tpublic OVHClientImpl(String appKey, String appSecret) {\n" +
						"\t\tsuper(appKey, appSecret);\n" +
						"\t}\n"	);

		for (PathDescriptor descriptor : descriptorSet) {
			String apiName = "";
			for (String part : descriptor.getDescriptorFile().getResourcePath().split("/"))
				apiName += StringUtils.capitalize(part);
			apiName = StringUtils.uncapitalize(apiName);
			writer.write("\tpublic " + descriptor.getFullInterfaceName() + " " + apiName + "() {\n");
			writer.write("\treturn new " + descriptor.getFullClassName() + "(this);\n");
			writer.write("\t}\n\n");
		}

		writer.write("}\n");
		writer.flush();
	}
}
