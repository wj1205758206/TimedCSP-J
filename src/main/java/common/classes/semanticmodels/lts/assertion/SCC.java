package common.classes.semanticmodels.lts.assertion;

import common.classes.moduleinterface.ConfigurationBase;

import java.util.List;
import java.util.Map;

public final class SCC {
    public Map<String, LocalPair> component;


    public Map<String, List<String>> transitionTable;


    public List<ConfigurationBase> trace;


    public int loopIndex;

    public SCC(Map<String, LocalPair> component, Map<String, List<String>> transitionTable, List<ConfigurationBase> trace, int loopIndex) {
        this.component = component;
        this.transitionTable = transitionTable;
        this.trace = trace;
        this.loopIndex = loopIndex;
    }
}
