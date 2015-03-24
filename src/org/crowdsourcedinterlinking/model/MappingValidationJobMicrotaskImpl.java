package org.crowdsourcedinterlinking.model;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Time;

import com.google.common.io.Files;

public class MappingValidationJobMicrotaskImpl extends JobMicrotaskImpl {

	protected boolean turned;

	public MappingValidationJobMicrotaskImpl(boolean turned) {
		this.turned = turned;
	}

	// en loadInfo hacer SI UNIT NO ES GOLDEN ENTONCES CARGAR LA INFO

	public void serialiseUnitsIntoCVSFile() {

		try {
			UUID id = UUID.randomUUID();
			this.pathOfCSVfile = ConfigurationManager.getInstance()
					.getCsvValidation() + id.toString() + ".csv";
			File csvFile = new File(pathOfCSVfile);
			System.out.println("file for CSV: " + csvFile.getAbsolutePath());

			Files.write("A, B, Relation, DefinitionA, Definition B", csvFile,
					Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, csvFile, Charset.defaultCharset());

			MappingValidationUnitDataEntryImpl unit;

			for (UnitDataEntryImpl u : this.setOfUnits) {
				Files.append(ls, csvFile, Charset.defaultCharset());
				unit = (MappingValidationUnitDataEntryImpl) u;
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

			// validation
			if (!turned) {
				cmlFile = new File(ConfigurationManager.getInstance()
						.getMappingValidationFile());
			} else {
				cmlFile = new File(ConfigurationManager.getInstance()
						.getMappingValidationTurnedFile());
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
