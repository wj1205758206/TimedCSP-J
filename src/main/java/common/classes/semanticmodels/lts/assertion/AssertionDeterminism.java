package common.classes.semanticmodels.lts.assertion;

import common.classes.datastructure.StringHashTable;
import common.classes.moduleinterface.AssertionBase;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.moduleinterface.VerificationResultType;
import common.classes.ultility.Ultility;

import java.util.*;

public abstract class AssertionDeterminism extends AssertionBase {
    private String Event = "";

    @Override
    public String toString() {
        return this.startingProcess() + " deterministic";
    }

    @Override
    public void runVerification() throws Exception {
        if (this.selectedEngineName == "First Witness Trace using Depth First Search") {
            this.DFSVerification();
            return;
        }
        this.BFSVerification();
    }

    public void DFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Stack<ConfigurationBase> stack = new Stack<>();
        stringHashTable.add(this.initialStep.getID());
        stack.push(this.initialStep);
        Stack<Integer> stack2 = new Stack<>();
        stack2.push(0);
        List<Integer> list = new ArrayList<>(1024);
        while (!this.cancelRequested()) {
            ConfigurationBase configurationBase = stack.pop();
            int num = stack2.pop();
            if (num > 0) {
                while (list.get(list.size() - 1) >= num) {
                    int index = list.size() - 1;
                    list.remove(index);
                    this.verificationOutput.counterExampleTrace.remove(index);
                }
            }
            this.verificationOutput.counterExampleTrace.add(configurationBase);
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            int num2 = enumerable.size();
            this.verificationOutput.transitions += (long) num2;
            boolean flag = true;
            Map<String, String> dictionary = new HashMap<>(num2);
            for (ConfigurationBase configurationBase2 : enumerable) {
                String id = configurationBase2.getID();
                String a = dictionary.get(configurationBase2.event);
                if (dictionary.containsKey(configurationBase2.event)) {
                    if (a != id) {
                        flag = false;
                        this.Event = configurationBase2.event;
                        break;
                    }
                } else {
                    dictionary.put(configurationBase2.event, id);
                }
            }
            if (!flag) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            list.add(num);
            for (ConfigurationBase configurationBase3 : enumerable) {
                String id2 = configurationBase3.getID();
                if (!stringHashTable.containsKey(id2)) {
                    stringHashTable.add(id2);
                    stack.push(configurationBase3);
                    stack2.push(num + 1);
                }
            }
            if (stack.size() <= 0) {
                this.verificationOutput.counterExampleTrace = null;
                if (this.mustAbstract) {
                    this.verificationOutput.verificationResult = VerificationResultType.UNKNOWN;
                } else {
                    this.verificationOutput.verificationResult = VerificationResultType.VALID;
                }
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    public void BFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue2 = new ArrayDeque<>(1024);
        stringHashTable.add(this.initialStep.getID());
        queue.add(this.initialStep);
        queue2.add(Arrays.asList(this.initialStep));
        while (!this.cancelRequested()) {
            ConfigurationBase configurationBase = queue.poll();
            List<ConfigurationBase> list = queue2.poll();
            List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            int num = enumerable.size();
            this.verificationOutput.transitions += (long) num;
            boolean flag = true;
            Map<String, String> dictionary = new HashMap<>(num);
            for (ConfigurationBase configurationBase2 : enumerable) {
                String id = configurationBase2.getID();
                String a = dictionary.get(configurationBase2.event);
                if (dictionary.containsKey(configurationBase2.event)) {
                    if (a != id) {
                        flag = false;
                        this.Event = configurationBase2.event;
                        break;
                    }
                } else {
                    dictionary.put(configurationBase2.event, id);
                }
            }
            if (!flag) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.counterExampleTrace = list;
                return;
            }
            for (ConfigurationBase configurationBase3 : enumerable) {
                String id2 = configurationBase3.getID();
                if (!stringHashTable.containsKey(id2)) {
                    stringHashTable.add(id2);
                    queue.add(configurationBase3);
                    List<ConfigurationBase> temp = new ArrayList<>(list);
                    temp.add(configurationBase3);
                    queue2.add(temp);
                }
            }
            if (queue.size() <= 0) {
                if (this.mustAbstract) {
                    this.verificationOutput.verificationResult = VerificationResultType.UNKNOWN;
                } else {
                    this.verificationOutput.verificationResult = VerificationResultType.VALID;
                }
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    @Override
    public String getResultString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (this.verificationOutput.verificationResult == VerificationResultType.VALID) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is VALID." + "\n");
        } else if (this.verificationOutput.verificationResult == VerificationResultType.UNKNOWN) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NEITHER PROVED NOR DISPROVED." + "\n");
        } else {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NOT valid." + "\n");
            stringBuilder.append("The following trace leads to a situation where engaging in event " + this.Event + " leads to different system configurations." + "\n");
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

    @Override
    protected boolean isCounterExampleSpurious() throws Exception {
        Stack<ConfigurationBase> stack = new Stack<>();
        List<ConfigurationBase> list = new ArrayList<>(64);
        stack.push(this.initialStep);
        Stack<Integer> stack2 = new Stack<Integer>();
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
                List<ConfigurationBase> source = configurationBase.makeOneMove(this.Event);
                if (source.size() > 1) {
                    break;
                }
            } else {
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
            }
            if (stack.size() <= 0) {
                return true;
            }
        }
        this.verificationOutput.counterExampleTrace = list;
        return false;
    }
}
