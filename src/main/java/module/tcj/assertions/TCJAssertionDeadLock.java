package module.tcj.assertions;

import common.classes.datastructure.DBM;
import common.classes.expressions.Valuation;
import common.classes.moduleinterface.ModelCheckingOptions;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.semanticmodels.lts.assertion.AssertionCSPDeadLock;
import module.tcj.lts.*;

import java.util.ArrayList;
import java.util.List;

public class TCJAssertionDeadLock extends AssertionCSPDeadLock {
    private DefinitionRef Process;
    private Valuation GlobalEnv;

    public TCJAssertionDeadLock(DefinitionRef processDef) {
        super();
        Process = processDef;
    }


    public TCJAssertionDeadLock(DefinitionRef processDef, boolean isNontermination) {
        super(isNontermination);
        Process = processDef;
    }

    @Override
    public void initialize(SpecificationBase spec) {
        Specification specification = (Specification) spec;
        this.GlobalEnv = specification.SpecValuation.getVariableChannelClone(this.Process.GetGlobalVariables(), specification.GetChannelNames(this.Process.GetChannels()));
        this.mustAbstract = this.Process.MustBeAbstracted();
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("First Witness Trace with Zone Abstraction");
        list.add("First Witness Trace with Digitization");
        list.add("Shortest Witness Trace with Zone Abstraction");
        list.add("Shortest Witness Trace with Digitization");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    @Override
    public String startingProcess() {
        return Process.toString();
    }

    @Override
    public void runVerification() throws Exception {
        String selectedEngineName;
        if ((selectedEngineName = this.selectedEngineName) != null) {
            if (!(selectedEngineName == "Shortest Witness Trace with Zone Abstraction")) {
                if (!(selectedEngineName == "Shortest Witness Trace with Digitization")) {
                    if (!(selectedEngineName == "First Witness Trace with Zone Abstraction")) {
                        if (selectedEngineName == "First Witness Trace with Digitization") {
                            this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                            this.DFSVerification();
                        }
                    } else {
                        this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                        ((ZoneConfiguration) this.initialStep).SetTimer();
                        this.DFSVerification();
                    }
                } else {
                    this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                    this.BFSVerification();
                }
            } else {
                this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                ((ZoneConfiguration) this.initialStep).SetTimer();
                this.BFSVerification();
            }
        }
        if (!this.cancelRequested()) {
            TCJAssertionReachability.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this);
        }
    }
}
