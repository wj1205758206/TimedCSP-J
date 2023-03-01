package module.tcj.assertions;

import common.ParsingException;
import common.classes.datastructure.DBM;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.ModelCheckingOptions;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.semanticmodels.lts.assertion.AssertionReachabilityWith;
import common.classes.ultility.QueryConstraintType;
import module.tcj.lts.*;
import module.tcj.ultility.ParsingUltility;
import org.antlr.v4.runtime.CommonToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TCJAssertionReachabilityWith extends AssertionReachabilityWith {

    private DefinitionRef Process;
    private Valuation GlobalEnv;

    public TCJAssertionReachabilityWith(DefinitionRef processDef, String reachableState, QueryConstraintType cont, Expression constraintCondition) {
        super(reachableState, cont, constraintCondition);
        Process = processDef;
    }

    @Override
    public String startingProcess() {
        return Process.toString();
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        Specification specification = (Specification) spec;
        this.reachableStateCondition = specification.declarationDatabase.get(this.reachableStateLabel);
        ParsingUltility.TestIsBooleanExpression(this.reachableStateCondition,
                new CommonToken(null, -1, -1, -1, -1),
                " used in the condition \"" + this.reachableStateLabel + "\" of reachablity assertion \"" + this.toString() + "\"",
                specification.SpecValuation, new HashMap<String, Expression>());
        List<String> globalVariables = this.Process.GetGlobalVariables();
        globalVariables.addAll(this.reachableStateCondition.getVars());
        this.GlobalEnv = specification.SpecValuation.getVariableChannelClone(globalVariables, specification.GetChannelNames(this.Process.GetChannels()));
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
    public void runVerification() throws Exception {
        String selectedEngineName;
        if ((selectedEngineName = this.selectedEngineName) != null)
        {
            if (!(selectedEngineName == "Shortest Witness Trace with Zone Abstraction"))
            {
                if (!(selectedEngineName == "Shortest Witness Trace with Digitization"))
                {
                    if (!(selectedEngineName == "First Witness Trace with Zone Abstraction"))
                    {
                        if (selectedEngineName == "First Witness Trace with Digitization")
                        {
                            this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                            this.DFSVerification();
                        }
                    }
                    else
                    {
                        this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                        ((ZoneConfiguration) this.initialStep).SetTimer();
                        this.DFSVerification();
                    }
                }
                else
                {
                    this.initialStep = new TickConfiguration(this.Process, "init", null, this.GlobalEnv, false, false);
                    this.BFSVerification();
                }
            }
            else
            {
                this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                ((ZoneConfiguration) this.initialStep).SetTimer();
                this.BFSVerification();
            }
        }
        if (!this.cancelRequested())
        {
            TCJAssertionReachability.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this);
        }
    }
}
