package org.crowdsourcedinterlinking.model;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Time;

import com.google.common.io.Files;

public class MappingValidationWithFullContextJobMicrotaskImpl extends
		MappingValidationJobMicrotaskImpl {
	// en loadInfo hacer SI UNIT NO ES GOLDEN ENTONCES CARGAR LA INFO

	public MappingValidationWithFullContextJobMicrotaskImpl(boolean turned) {
		super(turned);
	}

	public void serialiseUnitsIntoCVSFile() {
		try {
			UUID id = UUID.randomUUID();
			this.pathOfCSVfile = ConfigurationManager.getInstance()
					.getCsvValidationContext() + id.toString() + ".csv";
			File csvFile = new File(pathOfCSVfile);
			System.out.println("file for CSV: " + csvFile.getAbsolutePath());

			Files.write(
					"A, B, Relation, DefinitionA, Definition B, SiblingsA, SiblingsB, SuperclassesA, SuperclassesB, SubclassesA, SubclassesB, InstancesA, InstancesB",
					csvFile, Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, csvFile, Charset.defaultCharset());

			MappingValidationWithFullContextUnitDataEntryImpl unit;

			for (UnitDataEntryImpl u : this.setOfUnits) {
				Files.append(ls, csvFile, Charset.defaultCharset());
				unit = (MappingValidationWithFullContextUnitDataEntryImpl) u;
				if (!unit.isGoldenUnit()) {

					Files.append(unit.getLabelA(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getLabelB(), csvFile,
							Charset.defaultCharset());
					Files.append(", ", csvFile, Charset.defaultCharset());
					Files.append(unit.getRelation(), csvFile,
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
					Files.append(unit.getRelation(), csvFile,
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
			File cmlFile;

			// validation full context
			if (!turned) {
				cmlFile = new File(ConfigurationManager.getInstance()
						.getMappingValidationFullContextFile());
			} else {
				cmlFile = new File(ConfigurationManager.getInstance()
						.getMappingValidationFullContextTurnedFile());
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
