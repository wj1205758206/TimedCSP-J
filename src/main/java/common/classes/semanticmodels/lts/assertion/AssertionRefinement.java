package common.classes.semanticmodels.lts.assertion;

import common.ParsingException;
import common.classes.datastructure.*;
import common.classes.moduleinterface.*;
import common.classes.ultility.Ultility;

import java.util.*;

public abstract class AssertionRefinement extends AssertionBase {
    public ConfigurationBase InitSpecStep;

    @Override
    public String toString() {
        return this.startingProcess() + " refines " + this.SpecProcess();
    }

    public abstract String SpecProcess();

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("On-the-fly Trace Refinement Checking using DFS and Antichain");
        list.add("On-the-fly Trace Refinement Checking using BFS and Antichain");
        list.add("On-the-fly Trace Refinement Checking using Depth First Search");
        list.add("On-the-fly Trace Refinement Checking using Breadth First Search");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    @Override
    public void runVerification() throws Exception {
        if (this.selectedEngineName == "On-the-fly Trace Refinement Checking using DFS and Antichain") {
            Automata spec = AssertionRefinement.BuildAutomata(this.InitSpecStep);
            this.TraceInclusionCheckDFSAntiChain(spec);
            return;
        }
        if (this.selectedEngineName == "On-the-fly Trace Refinement Checking using BFS and Antichain") {
            Automata spec2 = AssertionRefinement.BuildAutomata(this.InitSpecStep);
            this.TraceInclusionCheckBFSAntiChain(spec2);
            return;
        }
        DeterministicAutomata spec3 = AssertionRefinement.BuildDeterministicAutomata(this.InitSpecStep);
        if (this.selectedEngineName == "On-the-fly Trace Refinement Checking using Depth First Search") {
            this.TraceInclusionCheckDFS(spec3);
            return;
        }
        this.TraceInclusionCheckBFS(spec3);
    }

    public static DeterministicAutomata BuildDeterministicAutomata(ConfigurationBase initStep) throws ParsingException, CloneNotSupportedException {
        return AssertionRefinement.BuildAutomata(initStep).Determinize();
    }

    public static DeterministicAutomata_Subset BuildDeterministicAutomata_Subset(ConfigurationBase initStep) throws ParsingException, CloneNotSupportedException {
        return AssertionRefinement.BuildAutomata(initStep).DeterminizeSubset();
    }

    public static Automata BuildAutomata(ConfigurationBase initStep) throws ParsingException, CloneNotSupportedException {
        Map<String, FAState> dictionary = new HashMap<>();
        Stack<ConfigurationBase> stack = new Stack<>();
        stack.push(initStep);
        Automata automata = new Automata();
        FAState fastate = automata.AddState();
        automata.SetInitialState(fastate);
        dictionary.put(initStep.getID(), fastate);
        do {
            ConfigurationBase configurationBase = stack.pop();
            FAState source = dictionary.get(configurationBase.getID());
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            for (ConfigurationBase configurationBase2 : enumerable) {
                String id = configurationBase2.getID();
                FAState fastate2;
                if (dictionary.containsKey(id)) {
                    fastate2 = dictionary.get(id);
                } else {
                    fastate2 = automata.AddState();
                    stack.push(configurationBase2);
                    dictionary.put(id, fastate2);
                }
                automata.AddTransition(source, configurationBase2.event, fastate2);
            }
        }
        while (stack.size() > 0);
        return automata;
    }

    public void TraceInclusionCheckDFS(DeterministicAutomata spec) throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        List<ConfigurationBase> list = new ArrayList<>();
        Stack<ConfigurationBase> stack = new Stack<>();
        Stack<DeterministicFAState> stack2 = new Stack<>();
        Stack<Integer> stack3 = new Stack<>();
        stack3.push(0);
        List<Integer> list2 = new ArrayList<>(1024);
        stack.push(this.initialStep);
        stack2.push(spec.InitialState);
        stringHashTable.add(this.initialStep.getID() + "$" + spec.InitialState.GetID());
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
            for (ConfigurationBase configurationBase2 : enumerable) {
                DeterministicFAState deterministicFAState2 = deterministicFAState;
                if (configurationBase2.event != "τ") {
                    deterministicFAState2 = deterministicFAState.Next(configurationBase2.event);
                    if (deterministicFAState2 == null) {
                        list.add(configurationBase2);
                        this.verificationOutput.noOfStates = (long) stringHashTable.count;
                        this.verificationOutput.counterExampleTrace = list;
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        return;
                    }
                }
                String key = configurationBase2.getID() + "$" + deterministicFAState2.GetID();
                if (!stringHashTable.containsKey(key)) {
                    stack.push(configurationBase2);
                    stack2.push(deterministicFAState2);
                    stack3.push(num + 1);
                    stringHashTable.add(key);
                }
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
    }

    public void TraceInclusionCheckBFS(DeterministicAutomata spec) throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1024);
        Queue<DeterministicFAState> queue2 = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue3 = new ArrayDeque<>(1024);
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        queue.add(this.initialStep);
        queue2.add(spec.InitialState);
        String key = spec.InitialState.GetID() + "$" + this.initialStep.getID();
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
            for (ConfigurationBase configurationBase2 : enumerable) {
                DeterministicFAState deterministicFAState2 = deterministicFAState;
                if (configurationBase2.event != "τ") {
                    deterministicFAState2 = deterministicFAState.Next(configurationBase2.event);
                    if (deterministicFAState2 == null) {
                        list.add(configurationBase2);
                        this.verificationOutput.noOfStates = (long) stringHashTable.count;
                        this.verificationOutput.counterExampleTrace = list;
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        return;
                    }
                }
                key = deterministicFAState2.GetID() + "$" + configurationBase2.getID();
                if (!stringHashTable.containsKey(key)) {
                    stringHashTable.add(key);
                    queue.add(configurationBase2);
                    queue2.add(deterministicFAState2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase2);
                    queue3.add(temp);
                }
            }
        }
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    public void TraceInclusionCheckDFSAntiChain(Automata spec) throws ParsingException, CloneNotSupportedException {
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        List<Integer> list = new ArrayList<>(1024);
        List<ConfigurationBase> list2 = new ArrayList<>();
        Stack<ConfigurationBase> stack2 = new Stack<>();
        Stack<NormalizedFAState> stack3 = new Stack<>();
        NormalizedFAState item = new NormalizedFAState(spec.InitialState).TauReachable();
        stack2.push(this.initialStep);
        stack3.push(item);
        AntiChain antiChain = new AntiChain();
        antiChain.Add(this.initialStep.getID(), item.States);
        while (stack2.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                return;
            }
            ConfigurationBase configurationBase = stack2.pop();
            NormalizedFAState normalizedFAState = stack3.pop();
            int num = stack.pop();
            if (num > 0) {
                while (list.get(list.size() - 1) >= num) {
                    int index = list.size() - 1;
                    list.remove(index);
                    list2.remove(index);
                }
            }
            list2.add(configurationBase);
            list.add(num);
            if (normalizedFAState.States.size() == 0) {
                this.verificationOutput.noOfStates += (long) antiChain.GetNoOfStates();
                this.verificationOutput.counterExampleTrace = list2;
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                return;
            }
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            for (ConfigurationBase configurationBase2 : enumerable) {
                NormalizedFAState item2 = normalizedFAState;
                if (configurationBase2.event != "τ") {
                    item2 = item2.NextWithTauReachable(configurationBase2.event);
                }
                if (!antiChain.Add(configurationBase2.getID(), item2.States)) {
                    stack2.push(configurationBase2);
                    stack3.push(item2);
                    stack.push(num + 1);
                }
            }
        }
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
    }

    public void TraceInclusionCheckBFSAntiChain(Automata spec) throws ParsingException, CloneNotSupportedException {
        AntiChain antiChain = new AntiChain();
        Queue<ConfigurationBase> queue = new ArrayDeque<>();
        Queue<NormalizedFAState> queue2 = new ArrayDeque<>();
        Queue<List<ConfigurationBase>> queue3 = new ArrayDeque<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        queue.add(this.initialStep);
        NormalizedFAState item = new NormalizedFAState(spec.InitialState).TauReachable();
        queue2.add(item);
        antiChain.Add(this.initialStep.getID(), item.States);
        queue3.add(Arrays.asList(this.initialStep));
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
            for (ConfigurationBase configurationBase2 : enumerable) {
                NormalizedFAState item2 = normalizedFAState;
                if (configurationBase2.event != "τ") {
                    item2 = normalizedFAState.NextWithTauReachable(configurationBase2.event);
                    if (item2.States.size() == 0) {
                        list.add(configurationBase2);
                        this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
                        this.verificationOutput.counterExampleTrace = list;
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        return;
                    }
                }
                if (!antiChain.Add(configurationBase2.getID(), item2.States)) {
                    queue.add(configurationBase2);
                    queue2.add(item2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase2);
                    queue3.add(temp);
                }
            }
        }
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.verificationOutput.noOfStates = (long) antiChain.GetNoOfStates();
    }

    @Override
    protected boolean isCounterExampleSpurious() throws Exception {
        Stack<ConfigurationBase> stack = new Stack<>();
        List<ConfigurationBase> list = new ArrayList<>(64);
        stack.push(this.initialStep);
        Stack<Integer> stack2 = new Stack<>();
        stack2.push(0);
        List<Integer> list2 = new ArrayList<>(1024);
        StringHashTable stringHashTable = new StringHashTable(1024);
        stringHashTable.add("0-" + this.initialStep.getID());
        for (; ; ) {
            ConfigurationBase configurationBase = stack.pop();
            int num = stack2.pop();
            if (num > 0) {
                while (list2.get(list2.size() - 1) >= num) {
                    int index = list2.size() - 1;
                    list2.remove(index);
                    list.remove(index);
                }
            }
            list.add(configurationBase);
            list2.add(num);
            if (list.size() == this.verificationOutput.counterExampleTrace.size()) {
                break;
            }
            ConfigurationBase configurationBase2 = this.verificationOutput.counterExampleTrace.get(num + 1);
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove(configurationBase2.event);
            for (ConfigurationBase configurationBase3 : enumerable) {
                String key = num + 1 + "-" + configurationBase3.getID();
                if (!stringHashTable.containsKey(key)) {
                    stack.push(configurationBase3);
                    stack2.push(num + 1);
                    stringHashTable.add(key);
                }
            }
            if (stack.size() <= 0) {
                return true;
            }
        }
        this.verificationOutput.counterExampleTrace = list;
        return false;
    }

    @Override
    public String getResultString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (this.verificationOutput.verificationResult == VerificationResultType.INVALID) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NOT valid." + "\n");
            stringBuilder.append(
                    "The following trace is allowed in "
                            + this.startingProcess()
                            + ", but not in "
                            + this.SpecProcess()
                            + "\n");
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
