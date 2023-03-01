package module.tcj.assertions;

import common.ParsingException;
import common.classes.datastructure.DBM;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.Expression;
import common.classes.moduleinterface.*;
import common.classes.semanticmodels.lts.assertion.AssertionReachability;
import module.tcj.lts.*;
import module.tcj.ultility.ParsingUltility;
import org.antlr.v4.runtime.CommonToken;

import java.util.*;

public class TCJAssertionReachability extends AssertionReachability {
    private DefinitionRef Process;
    private Valuation GlobalEnv;

    public TCJAssertionReachability(DefinitionRef processDef, String reachableState) {
        super(reachableState);
        this.Process = processDef;
    }

    @Override
    public String startingProcess() {
        return Process.toString();
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        Specification specification = (Specification) spec;
        this.reachableStateCondition = specification.declarationDatabase.get(this.reachableStateLabel);
        ParsingUltility.TestIsBooleanExpression(this.reachableStateCondition, new CommonToken(null, -1, -1, -1, -1),
                " used in the condition \""
                        + this.reachableStateLabel
                        + "\" of reachablity assertion \""
                        + this.toString()
                        + "\""
                , specification.SpecValuation, new HashMap<String, Expression>());
        List<String> globalVariables = this.Process.GetGlobalVariables();
        globalVariables.addAll(this.reachableStateCondition.getVars());
        this.GlobalEnv = specification.SpecValuation.getVariableChannelClone(globalVariables, specification.GetChannelNames(this.Process.GetChannels()));
        this.mustAbstract = this.Process.MustBeAbstracted();
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("First Witness Trace with Zone Abstraction");
        list.add("First Witness Trace with Zone Abstraction, and LU Simulation");
        list.add("First Witness Trace with Digitization");
        list.add("Shortest Witness Trace with Zone Abstraction");
        list.add("Shortest Witness Trace with Zone Abstraction, and LU Simulation");
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
                if (!(selectedEngineName == "Shortest Witness Trace with Zone Abstraction, and LU Simulation"))
                {
                    if (!(selectedEngineName == "Shortest Witness Trace with Digitization"))
                    {
                        if (!(selectedEngineName == "First Witness Trace with Zone Abstraction"))
                        {
                            if (!(selectedEngineName == "First Witness Trace with Zone Abstraction, and LU Simulation"))
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
                                ((ZoneConfiguration)this.initialStep).SetTimer();
                                new LUSimulation(this).DFSVerification();
                            }
                        }
                        else
                        {
                            this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                            ((ZoneConfiguration)this.initialStep).SetTimer();
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
                    ((ZoneConfiguration)this.initialStep).SetTimer();
                    new LUSimulation(this).BFSVerification();
                }
            }
            else
            {
                this.initialStep = new ZoneConfiguration(this.Process, "init", null, this.GlobalEnv, false, new DBM());
                ((ZoneConfiguration)this.initialStep).SetTimer();
                this.BFSVerification();
            }
        }
        if (!this.cancelRequested())
        {
            TCJAssertionReachability.GetCounterExampleWithTocks(new TickSimulationConfiguration(this.Process, "init", null, this.GlobalEnv, false, false), this);
        }
    }

    public static void GetCounterExampleWithTocks(TickSimulationConfiguration init, AssertionBase assertion) throws ParsingException, CloneNotSupportedException {
        VerificationOutput verificationOutput = assertion.verificationOutput;
        if (verificationOutput.counterExampleTrace == null || verificationOutput.counterExampleTrace.size() == 0) {
            return;
        }
        Queue<TickSimulationConfiguration> queue = new ArrayDeque<>(1024);
        Queue<Integer> queue2 = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue3 = new ArrayDeque<>(1024);
        Map<String, Boolean> dictionary = new HashMap<>(1024);
        dictionary.put(init.getID() + "$" + 0, false);
        queue.add(init);
        queue2.add(0);
        queue3.add(Arrays.asList(init));
        while (!assertion.cancelRequested()) {
            TickSimulationConfiguration tickSimulationConfiguration = queue.poll();
            List<ConfigurationBase> list = queue3.poll();
            int num = queue2.poll();
            if (num == verificationOutput.counterExampleTrace.size() - 1) {
                verificationOutput.counterExampleTrace = list;
                return;
            }
            ConfigurationBase configurationBase = verificationOutput.counterExampleTrace.get(num + 1);
            ConfigurationBase[] array = tickSimulationConfiguration.makeOneMove().toArray(new ConfigurationBase[0]);
            //ConfigurationBase[] array = tickSimulationConfiguration.MakeOneMove().ToArray<ConfigurationBase>();
            for (int i = array.length - 1; i >= 0; i--) {
                TickSimulationConfiguration tickSimulationConfiguration2 = (TickSimulationConfiguration) array[i];
                if (!tickSimulationConfiguration2.IsTock()) {
                    if (configurationBase.event == tickSimulationConfiguration2.event
                            && configurationBase.globalEnv.getID() == tickSimulationConfiguration2.globalEnv.getID()) {
                        String key = tickSimulationConfiguration2.getID() + "$" + (num + 1);
                        if (!dictionary.containsKey(key)) {
                            dictionary.put(key, false);
                            queue.add(tickSimulationConfiguration2);
                            queue2.add(num + 1);
                            List<ConfigurationBase> temp = new ArrayList<>(list);
                            temp.add(tickSimulationConfiguration2);
                            queue3.add(temp);
                        }
                    }
                } else {
                    String key2 = tickSimulationConfiguration2.getID() + "$" + num;
                    if (!dictionary.containsKey(key2)) {
                        dictionary.put(key2, false);
                        queue.add(tickSimulationConfiguration2);
                        queue2.add(num);
                        List<ConfigurationBase> temp = new ArrayList<>(list);
                        temp.add(tickSimulationConfiguration2);
                        queue3.add(temp);
                    }
                }
            }
            if (queue.size() <= 0) {
                return;
            }
        }
    }
}
