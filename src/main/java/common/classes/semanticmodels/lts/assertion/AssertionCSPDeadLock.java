package common.classes.semanticmodels.lts.assertion;

import common.classes.datastructure.StringHashTable;
import common.classes.moduleinterface.AssertionBase;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.moduleinterface.VerificationResultType;
import common.classes.ultility.Ultility;

import java.util.*;


public abstract class AssertionCSPDeadLock extends AssertionBase {
    protected boolean isNotTerminationTesting;

    protected AssertionCSPDeadLock() {
    }

    protected AssertionCSPDeadLock(boolean isNontermination) {
        this.isNotTerminationTesting = isNontermination;
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
            //IEnumerable<ConfigurationBase> enumerable = configurationBase.makeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            if (configurationBase.isDeadLock) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
            list.add(num);
            for (ConfigurationBase configurationBase2 : enumerable) {
                String id = configurationBase2.getID();
                if (configurationBase2.event == "terminate") {
                    if (this.isNotTerminationTesting) {
                        this.verificationOutput.counterExampleTrace.add(configurationBase2);
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.verificationOutput.noOfStates = (long) stringHashTable.count;
                        return;
                    }
                } else if (!stringHashTable.containsKey(id)) {
                    stringHashTable.add(id);
                    stack.push(configurationBase2);
                    stack2.push(num + 1);
                }
            }
            if (stack.size() > 0) {
                continue;
            }
            this.verificationOutput.counterExampleTrace = null;
            if (this.mustAbstract) {
                this.verificationOutput.verificationResult = VerificationResultType.UNKNOWN;
            } else {
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
            }
            this.verificationOutput.noOfStates = (long) stringHashTable.count;
            return;
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
            //IEnumerable<ConfigurationBase> enumerable = configurationBase.MakeOneMove();
            this.verificationOutput.transitions += (long) enumerable.size();
            if (configurationBase.isDeadLock) {
                this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                this.verificationOutput.counterExampleTrace = list;
                return;
            }
            for (ConfigurationBase configurationBase2 : enumerable) {
                String id = configurationBase2.getID();
                if (configurationBase2.event == "terminate") {
                    if (this.isNotTerminationTesting) {
                        this.verificationOutput.counterExampleTrace.add(configurationBase2);
                        this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                        this.verificationOutput.noOfStates = (long) stringHashTable.count;
                        return;
                    }
                } else if (!stringHashTable.containsKey(id)) {
                    stringHashTable.add(id);
                    queue.add(configurationBase2);
                    queue2.add(Arrays.asList(configurationBase2));
                }
            }
            if (queue.size() > 0) {
                continue;
            }
            this.verificationOutput.counterExampleTrace = null;
            if (this.mustAbstract) {
                this.verificationOutput.verificationResult = VerificationResultType.UNKNOWN;
            } else {
                this.verificationOutput.verificationResult = VerificationResultType.VALID;
            }
            this.verificationOutput.noOfStates = (long) stringHashTable.count;
            return;
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    @Override
    public String toString() {

        if (this.isNotTerminationTesting) {
            return this.startingProcess() + " nonterminating";
        }
        return this.startingProcess() + " deadlockfree";
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
            if (this.isNotTerminationTesting) {
                stringBuilder.append("The following trace leads to a terminating situation." + "\n");
            } else {
                stringBuilder.append("The following trace leads to a deadlock situation." + "\n");
            }
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
