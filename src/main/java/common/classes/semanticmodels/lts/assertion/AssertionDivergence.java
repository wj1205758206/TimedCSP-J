package common.classes.semanticmodels.lts.assertion;

import common.ParsingException;
import common.classes.datastructure.StringHashTable;
import common.classes.moduleinterface.*;
import common.classes.ultility.Ultility;

import java.util.*;

public abstract class AssertionDivergence extends AssertionBase {
    protected static final int VISITED_NOPREORDER = -1;


    protected static final int SCC_FOUND = -2;


    private int LoopIndex = -1;


    private static HashSet<String> VisitedNonDivStates;

    @Override
    public String toString() {
        return this.startingProcess() + " divergencefree";
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("Strongly Connected Component Based Search");
        list.add("First Witness Trace using Depth First Search");
        list.add("Shortest Witness Trace using Breadth First Search");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    @Override
    public void runVerification() throws Exception {
        this.LoopIndex = -1;
        if (this.selectedEngineName == "First Witness Trace using Depth First Search") {
            this.DFSVerification();
            return;
        }
        if (this.selectedEngineName == "Shortest Witness Trace using Breadth First Search") {
            this.BFSVerification();
            return;
        }
        if (this.selectedEngineName == "Strongly Connected Component Based Search") {
            this.SCCVerification();
        }
    }

    public void DFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Stack<ConfigurationBase> stack = new Stack<>();
        AssertionDivergence.VisitedNonDivStates = new HashSet<>();
        Stack<Integer> stack2 = new Stack<>();
        stack2.push(0);
        List<Integer> list = new ArrayList<>(1000);
        ConfigurationBase configurationBase = this.initialStep;
        stack.push(configurationBase);
        stringHashTable.add(configurationBase.getID());
        while (stack.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            configurationBase = stack.pop();
            int num = stack2.pop();
            if (num > 0) {
                while (list.get(list.size() - 1) >= num) {
                    int index = list.size() - 1;
                    list.remove(index);
                    this.verificationOutput.counterExampleTrace.remove(index);
                }
            }
            this.verificationOutput.counterExampleTrace.add(configurationBase);
            list.add(num);
            if (!AssertionDivergence.VisitedNonDivStates.contains(configurationBase.getID()) && AssertionDivergence.IsDivergent(configurationBase)) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.LoopIndex = this.verificationOutput.counterExampleTrace.size();
                return;
            }
            ConfigurationBase[] array = configurationBase.makeOneMove().toArray(new ConfigurationBase[0]);
            this.verificationOutput.transitions += (long) array.length;
            for (int i = array.length - 1; i >= 0; i--) {
                ConfigurationBase configurationBase2 = array[i];
                String id = configurationBase2.getID();
                if (!stringHashTable.containsKey(id)) {
                    stringHashTable.add(id);
                    stack.push(configurationBase2);
                    stack2.push(num + 1);
                }
            }
        }
        this.verificationOutput.counterExampleTrace = null;
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    public void BFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        AssertionDivergence.VisitedNonDivStates = new HashSet<>();
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue2 = new ArrayDeque<>(1024);
        stringHashTable.add(this.initialStep.getID());
        queue.add(this.initialStep);
        queue2.add(Arrays.asList(this.initialStep));
        while (!this.cancelRequested()) {
            ConfigurationBase configurationBase = queue.poll();
            List<ConfigurationBase> list = queue2.poll();
            ConfigurationBase[] array = configurationBase.makeOneMove().toArray(new ConfigurationBase[0]);
            this.verificationOutput.transitions += (long) array.length;
            if (!AssertionDivergence.VisitedNonDivStates.contains(configurationBase.getID()) && AssertionDivergence.IsDivergent(configurationBase)) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.counterExampleTrace = list;
                this.LoopIndex = this.verificationOutput.counterExampleTrace.size();
                return;
            }
            for (int i = array.length - 1; i >= 0; i--) {
                ConfigurationBase configurationBase2 = array[i];
                String id = configurationBase2.getID();
                if (!stringHashTable.containsKey(id)) {
                    stringHashTable.add(id);
                    queue.add(configurationBase2);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase2);
                    queue2.add(temp);
                }
            }
            if (queue.size() <= 0) {
                this.verificationOutput.counterExampleTrace = null;
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    public void SCCVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        AssertionDivergence.VisitedNonDivStates = new HashSet<>();
        Stack<ConfigurationBase> stack = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();
        stack2.push(0);
        List<Integer> list = new ArrayList<>(1000);
        ConfigurationBase configurationBase = this.initialStep;
        stack.push(configurationBase);
        String id = configurationBase.getID();
        stringHashTable.add(id);
        while (stack.size() > 0) {
            if (this.cancelRequested()) {
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            configurationBase = stack.pop();
            id = configurationBase.getID();
            int num = stack2.pop();
            if (num > 0) {
                while (num > 0 && list.get(list.size() - 1) >= num) {
                    int index = list.size() - 1;
                    list.remove(index);
                    this.verificationOutput.counterExampleTrace.remove(index);
                }
            }
            this.verificationOutput.counterExampleTrace.add(configurationBase);
            list.add(num);
            if (!AssertionDivergence.VisitedNonDivStates.contains(id) && AssertionDivergence.IsDivergent(configurationBase)) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.LoopIndex = this.verificationOutput.counterExampleTrace.size();
                return;
            }
            ConfigurationBase[] array = configurationBase.makeOneMove().toArray(new ConfigurationBase[0]);
            this.verificationOutput.transitions += (long) array.length;
            for (int i = array.length - 1; i >= 0; i--) {
                ConfigurationBase configurationBase2 = array[i];
                String id2 = configurationBase2.getID();
                if (!stringHashTable.containsKey(id2)) {
                    stringHashTable.add(id2);
                    stack.push(configurationBase2);
                    stack2.push(num + 1);
                }
            }
        }
        this.verificationOutput.counterExampleTrace = null;
        this.verificationOutput.verificationResult = VerificationResultType.VALID;
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    private static boolean IsDivergent(ConfigurationBase States) throws ParsingException, CloneNotSupportedException {
        Map<String, int[]> dictionary = new HashMap<>();
        Map<String, List<String>> dictionary2 = new HashMap<>();
        List<String> list = new ArrayList<>();
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        stack.push(States);
        Map<String, int[]> dictionary3 = dictionary;
        String id = States.getID();
        int[] array = new int[2];
        array[0] = -1;
        dictionary3.put(id, array);
        dictionary2.put(States.getID(), new ArrayList<>());
        Stack<ConfigurationBase> stack2 = new Stack<ConfigurationBase>();
        int num = 0;
        Map<String, List<ConfigurationBase>> dictionary4 = new HashMap<>();
        for (; ; ) {
            ConfigurationBase configurationBase = stack.peek();
            String id2 = configurationBase.getID();
            List<String> list2 = dictionary2.get(id2);
            int[] array2 = dictionary.get(id2);
            if (array2[0] == -1) {
                array2[0] = num;
                num++;
            }
            boolean flag = true;
            if (dictionary4.containsKey(id2)) {
                List<ConfigurationBase> list3 = dictionary4.get(id2);
                if (list3.size() > 0) {
                    for (int i = list3.size() - 1; i >= 0; i--) {
                        ConfigurationBase configurationBase2 = list3.get(i);
                        String id3 = configurationBase2.getID();
                        if (dictionary.get(id3)[0] == -1) {
                            if (flag) {
                                stack.push(configurationBase2);
                                flag = false;
                                list3.remove(i);
                            }
                        } else {
                            list3.remove(i);
                        }
                    }
                }
            } else {
                List<ConfigurationBase> list4 = new ArrayList<>(configurationBase.makeOneMove("τ"));
                for (int j = list4.size() - 1; j >= 0; j--) {
                    ConfigurationBase configurationBase3 = list4.get(j);
                    String id4 = configurationBase3.getID();
                    int[] array3 = dictionary3.get(id4);
                    if (dictionary.containsKey(id4)) {
                        list2.add(id4);
                        if (array3[0] == -1) {
                            if (flag) {
                                stack.push(configurationBase3);
                                flag = false;
                                list4.remove(j);
                            } else {
                                list4.set(j, configurationBase3);
                            }
                        } else {
                            list4.remove(j);
                        }
                    } else {
                        Map<String, int[]> dictionary5 = dictionary;
                        String key = id4;
                        int[] array4 = new int[2];
                        array4[0] = -1;
                        dictionary5.put(key, array4);
                        dictionary2.put(id4, new ArrayList<>(8));
                        list2.add(id4);
                        if (flag) {
                            stack.push(configurationBase3);
                            flag = false;
                            list4.remove(j);
                        } else {
                            list4.set(j, configurationBase3);
                        }
                    }
                }
                dictionary4.put(id2, list4);
            }
            if (flag) {
                int num2 = array2[0];
                int num3 = num2;
                boolean flag2 = false;
                for (int k = 0; k < list2.size(); k++) {
                    String text = list2.get(k);
                    if (text == id2) {
                        flag2 = true;
                    }
                    int[] array5 = dictionary.get(text);
                    if (array5[0] != -2) {
                        if (array5[0] > num3) {
                            num2 = Math.min(num2, array5[1]);
                        } else {
                            num2 = Math.min(num2, array5[0]);
                        }
                    }
                }
                array2[1] = num2;
                stack.pop();
                if (num2 == num3) {
                    list.add(id2);
                    array2[0] = -2;
                    while (stack2.size() > 0 && dictionary.get(stack2.peek().getID())[0] > num3) {
                        ConfigurationBase configurationBase4 = stack2.pop();
                        String id5 = configurationBase4.getID();
                        list.add(id5);
                    }
                    if (list.size() > 1 || flag2) {
                        break;
                    }
                    for (String key2 : list) {
                        dictionary4.remove(key2);
                    }
                    list.clear();
                } else {
                    stack2.push(configurationBase);
                }
            }
            if (stack.size() <= 0) {
                //goto Block_21;
                for (String item : dictionary.keySet()) {
                    AssertionDivergence.VisitedNonDivStates.add(item);
                }
                return false;
            }
        }
        return true;
        /*Block_21:
        for(String item : dictionary.keySet())
        {
            AssertionDivergence.VisitedNonDivStates.add(item);
        }
        return false;*/
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
                List<ConfigurationBase> enumerable = configurationBase.makeOneMove("τ");
                for (ConfigurationBase configurationBase2 : enumerable) {
                    if (configurationBase2.getID() == list.get(this.LoopIndex).getID()) {
                        this.verificationOutput.counterExampleTrace = list;
                        return false;
                    }
                }
                if (stack.size() <= 0) {
                    break;
                }
                continue;

                /*using(IEnumerator < ConfigurationBase > enumerator = enumerable.GetEnumerator())
                {
                    while (enumerator.MoveNext()) {
                        ConfigurationBase configurationBase2 = enumerator.Current;
                        if (configurationBase2.GetID() == list[this.LoopIndex].GetID()) {
                            this.VerificationOutput.CounterExampleTrace = list;
                            return false;
                        }
                    }
						goto IL_1D1;
                }
					goto IL_13E;*/
            } else {
                ConfigurationBase configurationBase3 = this.verificationOutput.counterExampleTrace.get(num + 1);
                List<ConfigurationBase> enumerable2 = configurationBase.makeOneMove(configurationBase3.event);
                for (ConfigurationBase configurationBase4 : enumerable2) {
                    String key = num + 1 + "-" + configurationBase4.getID();
                    if (!stringHashTable.containsKey(key)) {
                        stack.push(configurationBase4);
                        stack2.push(num + 1);
                        stringHashTable.add(key);
                    }
                }
                if (stack.size() <= 0) {
                    break;
                }
                continue;
            }
			/*	goto IL_13E;
            IL_1D1:
            if (stack.size() <= 0) {
                break;
            }
            continue;
            IL_13E:
            ConfigurationBase configurationBase3 = this.verificationOutput.counterExampleTrace.get(num + 1);
            List<ConfigurationBase> enumerable2 = configurationBase.makeOneMove(configurationBase3.event);
            for(ConfigurationBase configurationBase4 : enumerable2)
            {
                String key = num + 1 + "-" + configurationBase4.getID();
                if (!stringHashTable.containsKey(key)) {
                    stack.push(configurationBase4);
                    stack2.push(num + 1);
                    stringHashTable.add(key);
                }
            }
				goto IL_1D1;*/
        }
        return true;
    }

    @Override
    public String getResultString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (this.verificationOutput.verificationResult == VerificationResultType.VALID) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is VALID." + "\n");
        } else {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NOT valid." + "\n");
            stringBuilder.append("A state reached via the following trace is divergent." + "\n");
            this.verificationOutput.getCounterxampleString(stringBuilder);
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
