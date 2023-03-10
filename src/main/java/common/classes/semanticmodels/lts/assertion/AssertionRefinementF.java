package common.classes.semanticmodels.lts.assertion;

import common.ParsingException;
import common.classes.datastructure.*;
import common.classes.moduleinterface.*;
import common.classes.ultility.Ultility;

import java.util.*;

public abstract class AssertionRefinementF extends AssertionRefinement {
    public RefinementCheckingResultType FailureType;

    @Override
    public String toString() {
        return this.startingProcess() + " refines <F> " + this.SpecProcess();
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("On-the-fly Failures Refinement Checking using DFS and Antichain");
        list.add("On-the-fly Failures Refinement Checking using BFS and Antichain");
        list.add("On-the-fly Failures Refinement Checking using Depth First Search");
        list.add("On-the-fly Failures Refinement Checking using Breadth First Search");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    public static Automata BuildAutomataWithRefusals(ConfigurationBase InitSpecStep) throws ParsingException, CloneNotSupportedException {
        Map<String, FAState> dictionary = new HashMap<>();
        Stack<ConfigurationBase> stack = new Stack<>();
        stack.push(InitSpecStep);
        Automata automata = new Automata();
        FAState fastate = automata.AddState();
        automata.SetInitialState(fastate);
        dictionary.put(InitSpecStep.getID(), fastate);
        do {
            ConfigurationBase configurationBase = stack.pop();
            FAState fastate2 = dictionary.get(configurationBase.getID());
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
        while (stack.size() > 0);
        return automata;
    }

    @Override
    public void runVerification() throws Exception {
        if (this.selectedEngineName == "On-the-fly Failures Refinement Checking using DFS and Antichain") {
            Automata spec = AssertionRefinementF.BuildAutomataWithRefusals(this.InitSpecStep);
            this.FailuresInclusionCheckDFSAntiChain(spec);
            return;
        }
        if (this.selectedEngineName == "On-the-fly Failures Refinement Checking using BFS and Antichain") {
            Automata spec2 = AssertionRefinementF.BuildAutomataWithRefusals(this.InitSpecStep);
            this.FailuresInclusionCheckBFSAntiChain(spec2);
            return;
        }
        DeterministicAutomata spec3 = AssertionRefinementF.BuildDeterministicAutomataWithRefusals(this.InitSpecStep);
        if (this.selectedEngineName == "On-the-fly Failures Refinement Checking using Depth First Search") {
            this.FailuresInclusionCheckDFS(spec3);
            return;
        }
        this.FailuresInclusionCheckBFS(spec3);
    }

    private void FailuresInclusionCheckDFSAntiChain(Automata spec) throws ParsingException, CloneNotSupportedException {
        Stack<ConfigurationBase> stack = new Stack<>();
        Stack<NormalizedFAState> stack2 = new Stack<NormalizedFAState>();
        List<ConfigurationBase> list = new ArrayList<>();
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
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list3 = new ArrayList<>();
            boolean flag = false;
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
                    flag = true;
                }
                if (!antiChain.Add(configurationBase2.getID(), item2.States)) {
                    stack.push(configurationBase2);
                    stack2.push(item2);
                    stack3.push(num + 1);
                }
            }
            if (flag || this.RefusalContainment(list3, normalizedFAState.GetFailuresNegate())) {
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

    private void FailuresInclusionCheckBFSAntiChain(Automata spec) throws ParsingException, CloneNotSupportedException {
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1000);
        Queue<NormalizedFAState> queue2 = new ArrayDeque<>(1000);
        Queue<List<ConfigurationBase>> queue3 = new ArrayDeque<>(1024);
        NormalizedFAState item = new NormalizedFAState(spec.InitialState).TauReachable();
        queue.add(this.initialStep);
        queue2.add(item);
        queue3.add(Arrays.asList(this.initialStep));
        AntiChain antiChain = new AntiChain();
        antiChain.Add(this.initialStep.getID(), item.States);
        while (queue.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                return;
            }
            ConfigurationBase configurationBase = queue.poll();
            NormalizedFAState normalizedFAState = queue2.poll();
            List<ConfigurationBase> list = queue3.poll();
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list2 = new ArrayList<>();
            boolean flag = false;
            for (ConfigurationBase configurationBase2 : enumerable) {
                NormalizedFAState item2 = normalizedFAState;
                if (configurationBase2.event != "τ") {
                    list2.add(configurationBase2.event);
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
                    flag = true;
                }
                if (!antiChain.Add(configurationBase2.getID(), item2.States)) {
                    queue.add(configurationBase2);
                    queue2.add(item2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase2);
                    queue3.add(temp);
                }
            }
            if (flag || this.RefusalContainment(list2, normalizedFAState.GetFailuresNegate())) {
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

    public static DeterministicAutomata BuildDeterministicAutomataWithRefusals(ConfigurationBase initStep) throws ParsingException, CloneNotSupportedException {
        return AssertionRefinementF.BuildAutomataWithRefusals(initStep).DeterminizeWithRefusals();
    }

    public void FailuresInclusionCheckDFS(DeterministicAutomata spec) throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Stack<ConfigurationBase> stack = new Stack<>();
        Stack<DeterministicFAState> stack2 = new Stack<>();
        List<ConfigurationBase> list = new ArrayList<>();
        Stack<Integer> stack3 = new Stack<>();
        stack3.push(0);
        List<Integer> list2 = new ArrayList<>(1000);
        stack.push(this.initialStep);
        stack2.push(spec.InitialState);
        String key = this.initialStep.getID() + "$" + spec.InitialState.GetID();
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
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list3 = new ArrayList<>();
            boolean flag = false;
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
                    flag = true;
                }
                key = configurationBase2.getID() + "$" + deterministicFAState2.GetID();
                if (!stringHashTable.containsKey(key)) {
                    stringHashTable.add(key);
                    stack.push(configurationBase2);
                    stack2.push(deterministicFAState2);
                    stack3.push(num + 1);
                }
            }
            if (flag || this.RefusalContainment(list3, deterministicFAState.NegatedRefusals)) {
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

    public void FailuresInclusionCheckBFS(DeterministicAutomata spec) throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1000);
        Queue<DeterministicFAState> queue2 = new ArrayDeque<>(1000);
        Queue<List<ConfigurationBase>> queue3 = new ArrayDeque<>(1024);
        queue.add(this.initialStep);
        queue2.add(spec.InitialState);
        String key = this.initialStep.getID() + "$" + spec.InitialState.GetID();
        stringHashTable.add(key);
        queue3.add(Arrays.asList(this.initialStep));
        while (queue.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            ConfigurationBase configurationBase = queue.poll();
            DeterministicFAState deterministicFAState = queue2.poll();
            List<ConfigurationBase> list = queue3.poll();
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            List<String> list2 = new ArrayList<>();
            boolean flag = false;
            for (ConfigurationBase configurationBase2 : enumerable) {
                DeterministicFAState deterministicFAState2 = deterministicFAState;
                if (configurationBase2.event != "τ") {
                    list2.add(configurationBase2.event);
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
                    flag = true;
                }
                key = configurationBase2.getID() + "$" + deterministicFAState2.GetID();
                if (!stringHashTable.containsKey(key)) {
                    stringHashTable.add(key);
                    queue.add(configurationBase2);
                    queue2.add(deterministicFAState2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase2);
                    queue3.add(temp);
                }
            }
            if (flag || this.RefusalContainment(list2, deterministicFAState.NegatedRefusals)) {
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

    protected boolean RefusalContainment(List<String> enabledEvents, List<List<String>> enabledS) {
        for (List<String> list : enabledS) {
            boolean flag = true;
            for (String item : list) {
                if (!enabledEvents.contains(item)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getResultString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (this.verificationOutput.verificationResult == VerificationResultType.INVALID) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NOT valid." + "\n");
            if (this.FailureType != RefinementCheckingResultType.FailuresRefinementFailure) {
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
        stringBuilder.append("Admissible Behavior: " + this.selectedBahaviorName);
        stringBuilder.append("Search Engine: " + this.selectedEngineName);
        stringBuilder.append("System Abstraction: " + this.mustAbstract);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
