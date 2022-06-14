package it.unive.scsr;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAConfiguration;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.program.Program;
import it.unive.scsr.final_project.ExtSignDomain;
import org.junit.Test;

public class ExtSignsTest {

    @Test
    public void testExtSigns() throws ParsingException, AnalysisException {
        Program program = IMPFrontend.processFile("inputs/extSigns.imp");

        LiSAConfiguration conf = new LiSAConfiguration();
        conf.setJsonOutput(true);
        conf.setDumpAnalysis(true);
        conf.setWorkdir("outputs");
        conf.setAbstractState(
                new SimpleAbstractState<>(
                        new MonolithicHeap(), // THIS IS THE HEAP DOMAIN
                        new ValueEnvironment<>(new ExtSignDomain()), // THIS IS THE VALUE DOMAIN
                        new TypeEnvironment<>(new InferredTypes())) // DOMAIN FOR TYPE ANALYSIS
        );

        LiSA lisa = new LiSA(conf);
        lisa.run(program);
    }

}