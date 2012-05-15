package org.drools.benchmark.benchmarks;

import org.drools.KnowledgeBase;
import org.drools.benchmark.BenchmarkDefinition;
import org.drools.benchmark.model.DummyBean;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class TmsBenchmark extends AbstractBenchmark {

    private String drlFile;

    private StatefulKnowledgeSession ksession;

    public TmsBenchmark(String drlFile) {
        this.drlFile = drlFile;
    }

    @Override
    public void init(BenchmarkDefinition definition) {
        KnowledgeBase kbase = createKnowledgeBase(createKnowledgeBuilder(drlFile));
        ksession = kbase.newStatefulKnowledgeSession();
    }

    public void execute(int repNr) {
        FactHandle fact = ksession.insert(new Integer(0));
        ksession.fireAllRules();
        ksession.retract(fact);
        ksession.fireAllRules();
    }

    @Override
    public void terminate() {
        if (ksession.getFactCount() > 0L) {
            throw new RuntimeException("Still " + ksession.getFactCount() + " facts");
        }
        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }
}
