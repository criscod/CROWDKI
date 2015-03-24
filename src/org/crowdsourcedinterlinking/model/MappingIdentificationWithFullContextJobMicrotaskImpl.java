package org.crowdsourcedinterlinking.model;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Time;

import com.google.common.io.Files;

public class MappingIdentificationWithFullContextJobMicrotaskImpl extends
		MappingIdentificationJobMicrotaskImpl {

	public MappingIdentificationWithFullContextJobMicrotaskImpl(
			boolean extended, boolean turned) {
		super(extended, turned);

	}

	// en loadInfo hacer SI UNIT NO ES GOLDEN ENTONCES CARGAR LA INFO

	public void serialiseUnitsIntoCVSFile() {
		try {
			UUID id = UUID.randomUUID();
			this.pathOfCSVfile = ConfigurationManager.getInstance()
					.getCsvIdentificationContext() + id.toString() + ".csv";
			File csvFile = new File(pathOfCSVfile);
			System.out.println("file for CSV: " + csvFile.getAbsolutePath());

			Files.write(
					"A, B, DefinitionA, Definition B, SiblingsA, SiblingsB, SuperclassesA, SuperclassesB, SubclassesA, SubclassesB, InstancesA, InstancesB",
					csvFile, Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, csvFile, Charset.defaultCharset());

			MappingIdentificationWithFullContextUnitDataEntryImpl unit;

			for (UnitDataEntryImpl u : this.setOfUnits) {
				Files.append(ls, csvFile, Charset.defaultCharset());
				unit = (MappingIdentificationWithFullContextUnitDataEntryImpl) u;
				if (!unit.isGoldenUnit()) {
					Files.append(unit.getLabelA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getLabelB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());

					Files.append(unit.getCommentA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getCommentB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringSiblingsA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringSiblingsB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringSuperClassesA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringSuperClassesB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringSubClassesA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringSubClassesB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringInstancesA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getStringInstancesB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());

				} else // it is golden
				{
					Files.append(unit.getElementA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getElementB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());

					// golden --> no comments
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					// golden --> no siblings, superClass, subClasses or
					// instances
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(" ", csvFile, Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createUI() {

		try {
			String cmlCode = new String();
			File cmlFile = null;

			if (this.extended) {
				if (!turned) {
					// identification A full context
					cmlFile = new File(ConfigurationManager.getInstance()
							.getMappingIdentificationAFullContextFile());
				} else {
					cmlFile = new File(ConfigurationManager.getInstance()
							.getMappingIdentificationAFullContextTurnedFile());
				}
			} else {

				// identification B full context
				// cmlFile = new
				// File("/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/CML/identificationBfullcontext.txt");
				if (!turned) {
					cmlFile = new File(ConfigurationManager.getInstance()
							.getMappingIdentificationBFullContextFile());
				} else {
					cmlFile = new File(ConfigurationManager.getInstance()
							.getMappingIdentificationBFullContextTurnedFile());

				}

			}
			List<String> lines = Files.readLines(cmlFile,
					Charset.defaultCharset());
			for (String s : lines) {
				cmlCode = cmlCode + s;

			}
			this.setCml(cmlCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
