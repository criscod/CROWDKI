package org.crowdsourcedinterlinking.evaluation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Properties;

import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Time;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.Evaluator;

import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.eval.DiffEvaluator;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class ResultEvaluatorImpl implements ResultEvaluator {

	private File alignment1;
	private File alignment2;

	// private MINE Alignment crowdAlignment;
	// private MINE Alignment referenceAlignment;

	// public ResultEvaluator (Alignment cAlign, Alignment rAlign)
	private PRecEvaluator evaluator;

	private double precision;
	private double recall;

	public ResultEvaluatorImpl(File fAlig1, File fAlig2) {
		try {
			this.alignment1 = fAlig1;
			this.alignment2 = fAlig2;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void evaluateResultsFromCrowdPR() {
		try {

			// Load the reference alignment
			AlignmentParser aparser = new AlignmentParser(0);

			Alignment crowd = aparser.parse(this.alignment1.toURI());
			Alignment reference = aparser.parse(this.alignment2.toURI());

			// this.evaluator = new DiffEvaluator(crowd, reference);
			Properties p = new BasicParameters();
			this.evaluator = new PRecEvaluator(reference, crowd);
			this.evaluator.eval(p);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	



	public DiffMappings evaluateDiff() {
		DiffMappings dM = null;
		try {

			// Load the reference alignment
			AlignmentParser aparser = new AlignmentParser(0);

			Alignment align1 = aparser.parse(this.alignment1.toURI());
			Alignment align2 = aparser.parse(this.alignment2.toURI());

			// this.evaluator = new DiffEvaluator(crowd, reference);
			Properties p = new BasicParameters();
			DiffEvaluator diffEval = new DiffEvaluator(align1, align2);
			diffEval.eval(p);
			dM = new DiffMappings(diffEval.getFalseNegative(),
					diffEval.getFalsePositive());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dM;

	}

	public void printResultsInConsole() {
		// They can also be stored in a file if necessary
		try {

			/*
			 * PrintWriter printer = new PrintWriter(System.out, true);
			 * evaluator.write(printer);
			 */

			System.out.println("PRECISION: " + this.evaluator.getPrecision());
			System.out.println("RECALL: " + this.evaluator.getRecall());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printResultsInFile() {
		// crowd alignment1, ref alignment2
		try {
			int i = ConfigurationManager.getInstance().getCrowdBaseFileName()
					.length();

			String crowdId = this.alignment1.getName().substring(i,
					this.alignment1.getName().length());

			if (crowdId.endsWith(".rdf")) {
				crowdId = crowdId.substring(0, crowdId.length() - 4);

			}

			File fEval = new File(ConfigurationManager.getInstance()
					.getEvaluationsDirectory()
					+ ConfigurationManager.getInstance()
							.getEvaluationBaseFileName() + crowdId + ".rdf");
			Files.write(
					"EVALUATION RESULTS for " + crowdId + ":  "
							+ Time.currentTime(), fEval,
					Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, fEval, Charset.defaultCharset());
			Files.append("PRECISION: " + this.evaluator.getPrecision(), fEval,
					Charset.defaultCharset());
			Files.append(ls, fEval, Charset.defaultCharset());
			Files.append("RECALL: " + this.evaluator.getRecall(), fEval,
					Charset.defaultCharset());
			Files.append(ls, fEval, Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
