package it.unive.scsr;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.dataflow.DefiniteForwardDataflowDomain;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.outputs.JsonReport;
import it.unive.lisa.outputs.compare.JsonReportComparer;
import it.unive.lisa.program.Program;

public class AvailableExpressionsTaskEvaluation {

	@Test
	public void testAvailableExpressions() throws ParsingException, AnalysisException {
		Program program = IMPFrontend.processFile("inputs/ae-eval.imp");

		LiSAConfiguration conf = new LiSAConfiguration();
		conf.setJsonOutput(true);
		conf.setDumpAnalysis(true);
		conf.setWorkdir("outputs/ae");
		conf.setAbstractState(
				new SimpleAbstractState<>(
						new MonolithicHeap(),
						new DefiniteForwardDataflowDomain<>(new AvailableExpressionsSolution()),
						new TypeEnvironment<>(new InferredTypes())));

		LiSA lisa = new LiSA(conf);
		lisa.run(program);

		Path expectedPath = Paths.get("expected", "available-expressions");
		Path actualPath = Paths.get("outputs", "ae");

		File expFile = Paths.get(expectedPath.toString(), "report.json").toFile();
		File actFile = Paths.get(actualPath.toString(), "report.json").toFile();
		try {
			JsonReport expected = JsonReport.read(new FileReader(expFile));
			JsonReport actual = JsonReport.read(new FileReader(actFile));
			assertTrue("Results are different",
					JsonReportComparer.compare(expected, actual, expectedPath.toFile(), actualPath.toFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);
			fail("Unable to find report file");
		} catch (IOException e) {
			e.printStackTrace(System.err);
			fail("Unable to compare reports");
		}
	}
}
