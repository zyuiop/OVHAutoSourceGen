package net.zyuiop.autosrcgen.writer;

import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * @author zyuiop
 */
public class MainInterfaceWriter extends ClassWriter {
	private final Set<PathDescriptor> descriptorSet;

	public MainInterfaceWriter(Set<PathDescriptor> descriptorSet) {
		super("api", "OVHClient");
		this.descriptorSet = descriptorSet;
	}

	@Override
	public void write() throws IOException {
		FileWriter writer = newFileWriter();

		writer.write(
				"public interface OVHClient {\n" + "\n" +
				"\tvoid authenticate(String customerKey);\n\n");

		writer.write("" +
				"\tString callRaw(URL url, OVHApiMethod method, String data) throws IOException;\n\n" +
				"\tString callRaw(URL url, OVHApiMethod method, String data, boolean hasAuth) throws IOException, IllegalStateException;\n\n");

		for (PathDescriptor descriptor : descriptorSet) {
			String apiName = "";
			for (String part : descriptor.getDescriptorFile().getResourcePath().split("/"))
				apiName += StringUtils.capitalize(part);
			apiName = StringUtils.uncapitalize(apiName);
			writer.write("\t" + descriptor.getFullInterfaceName() + " " + apiName + "();\n\n");
		}

		writer.write("}\n");
		writer.flush();
	}
}
