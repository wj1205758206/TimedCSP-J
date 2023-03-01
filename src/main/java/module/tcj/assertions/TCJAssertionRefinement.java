package module.tcj.assertions;

import common.ParsingException;
import common.classes.datastructure.Automata;
import common.classes.datastructure.DBM;
import common.classes.datastructure.DeterministicAutomata;
import common.classes.expressions.Valuation;
import common.classes.moduleinterface.ModelCheckingOptions;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.semanticmodels.lts.assertion.AssertionRefinement;
import module.tcj.lts.*;

import java.util.ArrayList;
import java.util.List;

public class TCJAssertionRefinement extends AssertionRefinement {

    private DefinitionRef ImplementationProcess;
    private DefinitionRef SpecificationProcess;
    private Valuation GlobalEnvImpl;
    private Valuation GlobalEnvSpec;

    public TCJAssertionRefinement(DefinitionRef processDef, DefinitionRef target) {
        super();
        ImplementationProcess = processDef;
        SpecificationProcess = target;
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        Specification specification = (Specification) spec;
        List<String> globalVariables = this.ImplementationProcess.GetGlobalVariables();
        globalVariables.addAll(this.SpecificationProcess.GetGlobalVariables());
        this.GlobalEnvImpl = specification.SpecValuation.getVariableChannelClone(globalVariables, specification.GetChannelNames(this.ImplementationProcess.GetChannels()));
        globalVariables = this.SpecificationProcess.GetGlobalVariables();
        this.GlobalEnvSpec = specification.SpecValuation.getVariableChannelClone(globalVariables, specification.GetChannelNames(this.SpecificationProcess.GetChannels()));
        this.mustAbstract = this.ImplementationProcess.MustBeAbstracted();
        if (this.SpecificationProcess.MustBeAbstracted()) {
            throw new ParsingException("Process " + this.SpecProcess() + " has infinite states and therefore can not be used as a property model for refinement checking!", this.assertToken);
        }
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("First Witness Trace with Zone Abstraction and Antichain");
        list.add("First Witness Trace with Digitization and Antichain");
        list.add("Shortest Witness Trace with Zone Abstraction and Antichain");
        list.add("Shortest Witness Trace with Digitization and Antichain");
        list.add("First Witness Trace with Zone Abstraction");
        list.add("First Witness Trace with Digitization");
        list.add("Shortest Witness Trace with Zone Abstraction");
        list.add("Shortest Witness Trace with Digitization");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    @Override
    public String startingProcess() {
        return ImplementationProcess.toString();
    }

    @Override
    public String SpecProcess() {
        return SpecificationProcess.toString();
    }

    @Override
    public void runVerification() throws Exception {
        String selectedEngineName;
        switch (selectedEngineName = this.selectedEngineName) {
            case "First Witness Trace with Zone Abstraction and Antichain": {
                this.initialStep = new ZoneConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, new DBM());
                ((ZoneConfiguration) this.initialStep).SetTimer();
                this.InitSpecStep = new ZoneConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, new DBM());
                ((ZoneConfiguration) this.InitSpecStep).SetTimer();
                Automata spec = AssertionRefinement.BuildAutomata(this.InitSpecStep);
                this.TraceInclusionCheckDFSAntiChain(spec);
                break;
            }
            case "First Witness Trace with Digitization and Antichain": {
                this.initialStep = new TickConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, false);
                this.InitSpecStep = new TickConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, false);
                Automata spec = AssertionRefinement.BuildAutomata(this.InitSpecStep);
                this.TraceInclusionCheckDFSAntiChain(spec);
                break;
            }
            case "Shortest Witness Trace with Zone Abstraction and Antichain": {
                this.initialStep = new ZoneConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, new DBM());
                ((ZoneConfiguration) this.initialStep).SetTimer();
                this.InitSpecStep = new ZoneConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, new DBM());
                ((ZoneConfiguration) this.InitSpecStep).SetTimer();
                Automata spec = AssertionRefinement.BuildAutomata(this.InitSpecStep);
                this.TraceInclusionCheckBFSAntiChain(spec);
                break;
            }
            case "Shortest Witness Trace with Digitization and Antichain": {
                this.initialStep = new TickConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, false);
                this.InitSpecStep = new TickConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, false);
                Automata spec = AssertionRefinement.BuildAutomata(this.InitSpecStep);
                this.TraceInclusionCheckBFSAntiChain(spec);
                break;
            }
            case "Shortest Witness Trace with Zone Abstraction": {
                this.initialStep = new ZoneConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, new DBM());
                ((ZoneConfiguration) this.initialStep).SetTimer();
                this.InitSpecStep = new ZoneConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, new DBM());
                ((ZoneConfiguration) this.InitSpecStep).SetTimer();
                DeterministicAutomata spec2 = AssertionRefinement.BuildDeterministicAutomata(this.InitSpecStep);
                this.TraceInclusionCheckBFS(spec2);
                break;
            }
            case "Shortest Witness Trace with Digitization": {
                this.initialStep = new TickConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, false);
                this.InitSpecStep = new TickConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, false);
                DeterministicAutomata spec2 = AssertionRefinement.BuildDeterministicAutomata(this.InitSpecStep);
                this.TraceInclusionCheckBFS(spec2);
                break;
            }
            case "First Witness Trace with Zone Abstraction": {
                this.initialStep = new ZoneConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, new DBM());
                ((ZoneConfiguration) this.initialStep).SetTimer();
                this.InitSpecStep = new ZoneConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, new DBM());
                ((ZoneConfiguration) this.InitSpecStep).SetTimer();
                DeterministicAutomata spec2 = AssertionRefinement.BuildDeterministicAutomata(this.InitSpecStep);
                this.TraceInclusionCheckDFS(spec2);
                break;
            }
            case "First Witness Trace with Digitization": {
                this.initialStep = new TickConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, false);
                this.InitSpecStep = new TickConfiguration(this.SpecificationProcess, "init", null, this.GlobalEnvSpec, false, false);
                DeterministicAutomata spec2 = AssertionRefinement.BuildDeterministicAutomata(this.InitSpecStep);
                this.TraceInclusionCheckDFS(spec2);
                break;
            }
        }
        if (!this.cancelRequested()) {
            TCJAssertionReachability.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.ImplementationProcess, "init", null, this.GlobalEnvImpl, false, false), this);
        }
    }
}
