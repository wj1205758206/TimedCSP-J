package common.classes.semanticmodels.lts.assertion;

import common.ParsingException;
import common.classes.datastructure.*;
import common.classes.moduleinterface.*;
import common.classes.ultility.Ultility;

import java.util.*;

public abstract class AssertionRefinementFD extends AssertionRefinementF {
    @Override
    public String toString() {
        return this.startingProcess() + " refines <FD> " + this.SpecProcess();
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("On-the-fly Failures/Divergence Refinement Checking using DFS and Antichain");
        list.add("On-the-fly Failures/Divergence Refinement Checking using BFS and Antichain");
        list.add("On-the-fly Failures/Divergence Refinement Checking using Depth First Search");
        list.add("On-the-fly Failures/Divergence Refinement Checking using Breadth First Search");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    @Override
    public void runVerification() throws Exception {
        if (this.selectedEngineName == "On-the-fly Failures/Divergence Refinement Checking using DFS and Antichain") {
            Automata spec = AssertionRefinementFD.BuildAutomataWithRefusalsAndDiv(this.InitSpecStep);
            this.FailuresDivergenceInclusionCheckDFSAntichain(spec);
            return;
        }
        if (this.selectedEngineName == "On-the-fly Failures/Divergence Refinement Checking using BFS and Antichain") {
            Automata spec2 = AssertionRefinementFD.BuildAutomataWithRefusalsAndDiv(this.InitSpecStep);
            this.FailuresDivergenceInclusionCheckBFSAntichain(spec2);
            return;
        }
        DeterministicAutomata spec3 = AssertionRefinementFD.BuildDeterministicAutomataWithRefusalsAndDiv(this.InitSpecStep);
        if (this.selectedEngineName == "On-the-fly Failures/Divergence Refinement Checking using Depth First Search") {
            this.FailuresDivergenceInclusionCheckDFS(spec3);
            return;
        }
        this.FailuresDivergenceInclusionCheckBFS(spec3);
    }

    public static DeterministicAutomata BuildDeterministicAutomataWithRefusalsAndDiv(ConfigurationBase initStep) throws Exception {
        return AssertionRefinementFD.BuildAutomataWithRefusalsAndDiv(initStep).DeterminizeWithRefusalsAndDiv();
    }

    public static Automata BuildAutomataWithRefusalsAndDiv(ConfigurationBase InitSpecStep) throws Exception {
        Map<String, FAState> dictionary = new HashMap<>();
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        stack.push(InitSpecStep);
        Automata automata = new Automata();
        FAState fastate = automata.AddState();
        automata.SetInitialState(fastate);
        dictionary.put(InitSpecStep.getID(), fastate);
        do {
            ConfigurationBase configurationBase = stack.pop();
            FAState fastate2 = dictionary.get(configurationBase.getID());
            if (configurationBase.IsDivergent()) {
                fastate2.IsDiv = true;
            } else {
                List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
                List<String> list = new ArrayList<>();
                boolean flag = false;
                for (ConfigurationBase configurationBase2 : enumerable) {
                    if (configurationBase2.event == "τ") {
                        flag = true;
                    } else {
                        list.add(configurationBase2.event);
                    }
                    String id = configurationBase2.getID();
                    FAState fastate3;
                    if (dictionary.containsKey(id)) {
                        fastate3 = dictionary.get(id);
                    } else {
                        fastate3 = automata.AddState();
                        stack.push(configurationBase2);
                        dictionary.put(id, fastate3);
                    }
                    automata.AddTransition(fastate2, configurationBase2.event, fastate3);
                }
                if (flag) {
                    fastate2.NegatedRefusal = null;
                } else {
                    fastate2.NegatedRefusal = list;
                }
            }
        }
        while (stack.size() > 0);
        return automata;
    }

    private void FailuresDivergenceInclusionCheckDFSAntichain(Automata spec) throws Exception {
        List<ConfigurationBase> list = new ArrayList<>();
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        Stack<NormalizedFAState> stack2 = new Stack<NormalizedFAState>();
        Stack<Integer> stack3 = new Stack<Integer>();
        stack3.push(0);
        List<Integer> list2 = new ArrayList<>(1000);
        NormalizedFAState item = new NormalizedFAState(spec.InitialState).TauReachable();
        stack.push(this.initialStep);
        stack2.push(item);
        AntiChain antiChain = new AntiChain();
        antiChain.Add(this.initialStep.getID(), item.States);
        while (stack.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                return;
            }
            ConfigurationBase configurationBase = stack.pop();
            NormalizedFAState normalizedFAState = stack2.pop();
            int num = stack3.pop();
            if (num > 0) {
                while (list2.get(list2.size() - 1) >= num) {
                    int index = list2.size() - 1;
                    list2.remove(index);
                    list.remove(index);
                }
            }
            list.add(configurationBase);
            list2.add(num);
            if (normalizedFAState.IsDiv()) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.FailureType = RefinementCheckingResultType.Valid;
                return;
            }
            boolean flag = configurationBase.IsDivergent();
            if (flag) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                this.verificationOutput.counterExampleTrace = list;
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.FailureType = RefinementCheckingResultType.DivCheckingFailure;
                return;
            }
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list3 = new ArrayList<>();
            boolean flag2 = false;
            for (ConfigurationBase configurationBase2 : enumerable) {
                NormalizedFAState item2 = normalizedFAState;
                if (configurationBase2.event != "τ") {
                    list3.add(configurationBase2.event);
                    item2 = normalizedFAState.NextWithTauReachable(configurationBase2.event);
                    if (item2.States.size() == 0) {
                        list.add(configurationBase2);
                        this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                        this.verificationOutput.counterExampleTrace = list;
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.FailureType = RefinementCheckingResultType.TraceRefinementFailure;
                        return;
                    }
                } else {
                    flag2 = true;
                }
                if (!antiChain.Add(configurationBase2.getID(), item2.States)) {
                    stack.push(configurationBase2);
                    stack2.push(item2);
                    stack3.push(num + 1);
                }
            }
            if (flag2 || this.RefusalContainment(list3, normalizedFAState.GetFailuresNegate())) {
                continue;
            }
            this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
            this.verificationOutput.counterExampleTrace = list;
            this.verificationOutput.verificationResult = VerificationResultType.INVALID;
            this.FailureType = RefinementCheckingResultType.FailuresRefinementFailure;
            return;
        }
        this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.FailureType = RefinementCheckingResultType.Valid;
    }

    private void FailuresDivergenceInclusionCheckBFSAntichain(Automata spec) throws Exception {
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        Stack<NormalizedFAState> stack2 = new Stack<NormalizedFAState>();
        Queue<List<ConfigurationBase>> queue = new ArrayDeque<>(1024);
        NormalizedFAState item = new NormalizedFAState(spec.InitialState).TauReachable();
        stack.push(this.initialStep);
        stack2.push(item);
        queue.add(Arrays.asList(this.initialStep));
        AntiChain antiChain = new AntiChain();
        antiChain.Add(this.initialStep.getID(), item.States);
        while (stack.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                return;
            }
            ConfigurationBase configurationBase = stack.pop();
            NormalizedFAState normalizedFAState = stack2.pop();
            List<ConfigurationBase> list = queue.poll();
            if (normalizedFAState.IsDiv()) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.FailureType = RefinementCheckingResultType.Valid;
                return;
            }
            boolean flag = configurationBase.IsDivergent();
            if (flag) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.counterExampleTrace = list;
                this.FailureType = RefinementCheckingResultType.DivCheckingFailure;
                return;
            }
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list2 = new ArrayList<>();
            boolean flag2 = false;
            for (ConfigurationBase configurationBase2 : enumerable) {
                NormalizedFAState item2 = normalizedFAState;
                if (configurationBase2.event != "τ") {
                    list2.add(configurationBase2.event);
                    item2 = normalizedFAState.NextWithTauReachable(configurationBase2.event);
                    if (item2.States.size() == 0) {
                        this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                        this.verificationOutput.counterExampleTrace = list;
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.FailureType = RefinementCheckingResultType.TraceRefinementFailure;
                        return;
                    }
                } else {
                    flag2 = true;
                }
                if (!antiChain.Add(configurationBase2.getID(), item2.States)) {
                    stack.push(configurationBase2);
                    stack2.push(item2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase2);
                    queue.add(temp);
                }
            }
            if (flag2 || this.RefusalContainment(list2, normalizedFAState.GetFailuresNegate())) {
                continue;
            }
            this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
            this.verificationOutput.counterExampleTrace = list;
            this.verificationOutput.verificationResult = VerificationResultType.INVALID;
            this.FailureType = RefinementCheckingResultType.FailuresRefinementFailure;
            return;
        }
        this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.FailureType = RefinementCheckingResultType.Valid;
    }

    public void FailuresDivergenceInclusionCheckBFS(DeterministicAutomata spec) throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        Stack<DeterministicFAState> stack2 = new Stack<DeterministicFAState>();
        Queue<List<ConfigurationBase>> queue = new ArrayDeque<>(1024);
        stack.push(this.initialStep);
        stack2.push(spec.InitialState);
        String key = this.initialStep.getID() + "τ" + spec.InitialState.GetID();
        stringHashTable.add(key);
        queue.add(Arrays.asList(this.initialStep));
        while (stack.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            ConfigurationBase configurationBase = stack.pop();
            DeterministicFAState deterministicFAState = stack2.pop();
            List<ConfigurationBase> list = queue.poll();
            if (deterministicFAState.IsDivergent) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.FailureType = RefinementCheckingResultType.Valid;
                return;
            }
            boolean flag = configurationBase.IsDivergent();
            if (flag) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.counterExampleTrace = list;
                this.FailureType = RefinementCheckingResultType.DivCheckingFailure;
                return;
            }
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list2 = new ArrayList<>();
            boolean flag2 = false;
            for (ConfigurationBase configurationBase2 : enumerable) {
                DeterministicFAState deterministicFAState2 = deterministicFAState;
                if (configurationBase2.event != "τ") {
                    list2.add(configurationBase2.event);
                    deterministicFAState2 = deterministicFAState.Next(configurationBase2.event);
                    if (deterministicFAState2 == null) {
                        this.verificationOutput.noOfStates = (long) stringHashTable.count;
                        this.verificationOutput.counterExampleTrace = list;
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.FailureType = RefinementCheckingResultType.TraceRefinementFailure;
                        return;
                    }
                } else {
                    flag2 = true;
                }
                key = configurationBase2.getID() + "$" + deterministicFAState2.GetID();
                if (!stringHashTable.containsKey(key)) {
                    stringHashTable.add(key);
                    stack.push(configurationBase2);
                    stack2.push(deterministicFAState2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase2);
                    queue.add(temp);
                }
            }
            if (flag2 || this.RefusalContainment(list2, deterministicFAState.NegatedRefusals)) {
                continue;
            }
            this.verificationOutput.noOfStates = (long) stringHashTable.count;
            this.verificationOutput.counterExampleTrace = list;
            this.verificationOutput.verificationResult = VerificationResultType.INVALID;
            this.FailureType = RefinementCheckingResultType.FailuresRefinementFailure;
            return;
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.FailureType = RefinementCheckingResultType.Valid;
    }

    public void FailuresDivergenceInclusionCheckDFS(DeterministicAutomata spec) throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        List<ConfigurationBase> list = new ArrayList<>();
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        Stack<DeterministicFAState> stack2 = new Stack<DeterministicFAState>();
        Stack<Integer> stack3 = new Stack<Integer>();
        stack3.push(0);
        List<Integer> list2 = new ArrayList<>(1000);
        stack.push(this.initialStep);
        stack2.push(spec.InitialState);
        String key = this.initialStep.getID() + "τ" + spec.InitialState.GetID();
        stringHashTable.add(key);
        while (stack.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            ConfigurationBase configurationBase = stack.pop();
            DeterministicFAState deterministicFAState = stack2.pop();
            int num = stack3.pop();
            if (num > 0) {
                while (list2.get(list2.size() - 1) >= num) {
                    int index = list2.size() - 1;
                    list2.remove(index);
                    list.remove(index);
                }
            }
            list.add(configurationBase);
            list2.add(num);
            if (deterministicFAState.IsDivergent) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.FailureType = RefinementCheckingResultType.Valid;
                return;
            }
            boolean flag = configurationBase.IsDivergent();
            if (flag) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.counterExampleTrace = list;
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.FailureType = RefinementCheckingResultType.DivCheckingFailure;
                return;
            }
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list3 = new ArrayList<>();
            boolean flag2 = false;
            for (ConfigurationBase configurationBase2 : enumerable) {
                DeterministicFAState deterministicFAState2 = deterministicFAState;
                if (configurationBase2.event != "τ") {
                    list3.add(configurationBase2.event);
                    deterministicFAState2 = deterministicFAState.Next(configurationBase2.event);
                    if (deterministicFAState2 == null) {
                        list.add(configurationBase2);
                        this.verificationOutput.noOfStates = (long) stringHashTable.count;
                        this.verificationOutput.counterExampleTrace = list;
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.FailureType = RefinementCheckingResultType.TraceRefinementFailure;
                        return;
                    }
                } else {
                    flag2 = true;
                }
                key = configurationBase2.getID() + "$" + deterministicFAState2.GetID();
                if (!stringHashTable.containsKey(key)) {
                    stringHashTable.add(key);
                    stack.push(configurationBase2);
                    stack2.push(deterministicFAState2);
                    stack3.push(num + 1);
                }
            }
            if (flag2 || this.RefusalContainment(list3, deterministicFAState.NegatedRefusals)) {
                continue;
            }
            this.verificationOutput.noOfStates = (long) stringHashTable.count;
            this.verificationOutput.counterExampleTrace = list;
            this.verificationOutput.verificationResult = VerificationResultType.INVALID;
            this.FailureType = RefinementCheckingResultType.FailuresRefinementFailure;
            return;
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.FailureType = RefinementCheckingResultType.Valid;
    }

    @Override
    public String getResultString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (this.verificationOutput.verificationResult == VerificationResultType.INVALID) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NOT valid." + "\n");
            if (this.FailureType == RefinementCheckingResultType.DivCheckingFailure) {
                stringBuilder.append(
                        "The following trace leads divergence in "
                                + this.startingProcess()
                                + ", but not in "
                                + this.SpecProcess()
                                + "."
                                + "\n"
                );
            } else if (this.FailureType == RefinementCheckingResultType.TraceRefinementFailure) {
                stringBuilder.append(
                        "The following trace is allowed in "
                                + this.startingProcess()
                                + ", but not in "
                                + this.SpecProcess()
                                + "."
                                + "\n"
                );
            } else {
                stringBuilder.append("After the following trace: failures refinement checking failed." + "\n");
            }
            this.verificationOutput.getCounterxampleString(stringBuilder);
        } else {
            stringBuilder.append("The Assertion (" + this.toString() + ") is VALID." + "\n");
        }
        stringBuilder.append("\n");
        stringBuilder.append("********Verification Setting********" + "\n");
        stringBuilder.append("Admissible Behavior: " + this.selectedBahaviorName + "\n");
        stringBuilder.append("Search Engine: " + this.selectedEngineName + "\n");
        stringBuilder.append("System Abstraction: " + this.mustAbstract + "\n");
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
