package net.zyuiop.autosrcgen.writer;

import net.zyuiop.autosrcgen.AutomaticSourceGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author zyuiop
 */
public abstract class ClassWriter {
	protected final String targetPackage;
	protected final String targetClassName;

	public ClassWriter(String targetPackage, String targetClassName) {
		this.targetPackage = targetPackage;
		this.targetClassName = targetClassName;
	}

	protected FileWriter newFileWriter() throws IOException {
		String path = AutomaticSourceGen.target + "/java/net/zyuiop/ovhapi/" + targetPackage.replaceAll("\\.", "/") + "/";
		String fileName = path + targetClassName + ".java";

		File pathFile = new File(path);
		pathFile.mkdirs();

		File file = new File(fileName);
		file.createNewFile();

		FileWriter writer = new FileWriter(file);
		writer.write("package net.zyuiop.ovhapi." + targetPackage + ";\n\n");
		return writer;
	}

	public abstract void write() throws IOException;
}
