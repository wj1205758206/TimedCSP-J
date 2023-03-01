package common.classes.semanticmodels.lts.assertion;

import common.ParsingException;
import common.classes.datastructure.StringHashTable;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.IntConstant;
import common.classes.moduleinterface.ConfigurationBase;
import common.classes.moduleinterface.ModelCheckingOptions;
import common.classes.moduleinterface.SpecificationBase;
import common.classes.moduleinterface.VerificationResultType;
import common.classes.ultility.QueryConstraintType;
import common.classes.ultility.Ultility;

import java.util.*;

public abstract class AssertionReachabilityWith extends AssertionReachability {

    public Expression ConstraintCondition;


    public QueryConstraintType Contraint;


    protected Integer ExtremValue;


    protected int ReachableCount;


    public boolean IsMonoIncreasing = true;

    protected AssertionReachabilityWith(String reachableState, QueryConstraintType cont, Expression constraintCondition) {
        super(reachableState);
        this.Contraint = cont;
        this.ConstraintCondition = constraintCondition;
    }

    @Override
    public String toString() {
        return this.startingProcess()
                + " reaches"
                + this.reachableStateLabel
                + " with "
                + this.Contraint.toString().toLowerCase()
                + "("
                + this.ConstraintCondition
                + ")";
    }

    @Override
    public void initialize(SpecificationBase spec) throws ParsingException, NoSuchMethodException {
        this.modelCheckingOptions = new ModelCheckingOptions();
        List<String> list = new ArrayList<>();
        list.add("First Witness Trace using Depth First Search");
        list.add("Shortest Witness Trace using Breadth First Search");
        list.add("Shortest Witness Trace using Breadth First Search with Monotonically With Value");
        this.modelCheckingOptions.addAddimissibleBehavior("All", list);
    }

    @Override
    public void runVerification() throws Exception {
        if (this.selectedEngineName == "First Witness Trace using Depth First Search") {
            this.DFSVerification();
            return;
        }
        if (this.selectedEngineName == "Shortest Witness Trace using Breadth First Search") {
            this.BFSVerification();
            return;
        }
        this.MonoBFSVerification();
    }

    public void BFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Expression reachableStateCondition = this.reachableStateCondition;
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue2 = new ArrayDeque<>(1024);
        stringHashTable.add(this.initialStep.getID());
        queue.add(this.initialStep);
        queue2.add(Arrays.asList(this.initialStep));
        this.ExtremValue = null;
        this.ReachableCount = 0;
        while (!this.cancelRequested()) {
            ConfigurationBase configurationBase = queue.poll();
            List<ConfigurationBase> collection = queue2.poll();
            if (configurationBase.implyCondition(reachableStateCondition)) {
                this.ReachableCount++;
                Expression expression = configurationBase.evaluateExpression(this.ConstraintCondition);
                if (expression instanceof IntConstant) {
                    int value = ((IntConstant) expression)._value;
                    if (this.ExtremValue == null) {
                        //this.ExtremValue = new int ? (value);
                        this.ExtremValue = Integer.valueOf(value);
                        this.verificationOutput.counterExampleTrace = new ArrayList<>(collection);
                    } else {
                        switch (this.Contraint) {
                            case MIN:
                                if (value < this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    //this.ExtremValue = new int ? (value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(collection);
                                }
                                break;
                            case MAX:
                                if (value > this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(collection);
                                }
                                break;
                        }
                    }
                }
            }
            //ConfigurationBase[] array = configurationBase.MakeOneMove().ToArray < ConfigurationBase > ();
            ConfigurationBase[] array = configurationBase.makeOneMove().toArray(new ConfigurationBase[0]);
            this.verificationOutput.transitions += (long) array.length;
            for (int i = array.length - 1; i >= 0; i--) {
                ConfigurationBase configurationBase2 = array[i];
                String id = configurationBase2.getID();
                if (!stringHashTable.containsKey(id)) {
                    stringHashTable.add(id);
                    queue.add(configurationBase2);
                    List<ConfigurationBase> temp = new ArrayList<>(collection);
                    temp.add(configurationBase2);
                    queue2.add(temp);
                }
            }
            if (queue.size() <= 0) {
                if (this.ExtremValue == null) {
                    this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                    this.verificationOutput.counterExampleTrace = null;
                } else {
                    this.verificationOutput.verificationResult = VerificationResultType.VALID;
                }
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    public void DFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Expression reachableStateCondition = this.reachableStateCondition;
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        stringHashTable.add(this.initialStep.getID());
        stack.push(this.initialStep);
        Stack<Integer> stack2 = new Stack<Integer>();
        stack2.push(0);
        List<Integer> list = new ArrayList<>(1024);
        this.ExtremValue = null;
        this.ReachableCount = 0;
        List<ConfigurationBase> list2 = new ArrayList<>();
        while (!this.cancelRequested()) {
            ConfigurationBase configurationBase = stack.pop();
            int num = stack2.pop();
            if (num > 0) {
                while (list.get(list.size() - 1) >= num) {
                    int index = list.size() - 1;
                    list.remove(index);
                    list2.remove(index);
                }
            }
            list2.add(configurationBase);
            if (configurationBase.implyCondition(reachableStateCondition)) {
                this.ReachableCount++;
                Expression expression = configurationBase.evaluateExpression(this.ConstraintCondition);
                if (expression instanceof IntConstant) {
                    int value = ((IntConstant) expression)._value;
                    if (this.ExtremValue == null) {
                        this.ExtremValue = Integer.valueOf(value);
                        this.verificationOutput.counterExampleTrace = new ArrayList<>(list2);
                    } else {
                        switch (this.Contraint) {
                            case MIN:
                                if (value < this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(list2);
                                }
                                break;
                            case MAX:
                                if (value > this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(list2);
                                }
                                break;
                        }
                    }
                }
            }
            list.add(num);
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
            if (stack.size() <= 0) {
                if (this.ExtremValue == null) {
                    this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                    this.verificationOutput.counterExampleTrace = null;
                } else {
                    this.verificationOutput.verificationResult = VerificationResultType.VALID;
                }
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    public void MonoDFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Expression reachableStateCondition = this.reachableStateCondition;
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        stringHashTable.add(this.initialStep.getID());
        stack.push(this.initialStep);
        Stack<Integer> stack2 = new Stack<Integer>();
        stack2.push(0);
        List<Integer> list = new ArrayList<>(1024);
        this.ExtremValue = null;
        this.ReachableCount = 0;
        List<ConfigurationBase> list2 = new ArrayList<>();
        while (!this.cancelRequested()) {
            ConfigurationBase configurationBase = stack.pop();
            int num = stack2.pop();
            if (num > 0) {
                while (list.get(list.size() - 1) >= num) {
                    int index = list.size() - 1;
                    list.remove(index);
                    list2.remove(index);
                }
            }
            list2.add(configurationBase);
            Expression expression = configurationBase.evaluateExpression(this.ConstraintCondition);
            if (expression instanceof IntConstant) {
                int value = ((IntConstant) expression)._value;
                if (configurationBase.implyCondition(reachableStateCondition)) {
                    this.ReachableCount++;
                    if (this.ExtremValue == null) {
                        this.ExtremValue = Integer.valueOf(value);
                        this.verificationOutput.counterExampleTrace = new ArrayList<>(list2);
                    } else {
                        switch (this.Contraint) {
                            case MIN:
                                if (value < this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(list2);
                                }
                                break;
                            case MAX:
                                if (value > this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(list2);
                                }
                                break;
                        }
                    }
                } else {
                    boolean flag = false;
                    if (this.ExtremValue != null) {
                        switch (this.Contraint) {
                            case MIN:
                                if (value > this.ExtremValue.intValue()) {
                                    flag = true;
                                }
                                break;
                            case MAX:
                                if (value < this.ExtremValue.intValue()) {
                                    flag = true;
                                }
                                break;
                        }
                    }
                    if (!flag) {
                        list.add(num);
                        List<ConfigurationBase> enumerable = configurationBase.makeOneMove();
                        this.verificationOutput.transitions += (long) enumerable.size();
                        for (ConfigurationBase configurationBase2 : enumerable) {
                            String id = configurationBase2.getID();
                            if (!stringHashTable.containsKey(id)) {
                                stringHashTable.add(id);
                                stack.push(configurationBase2);
                                stack2.push(num + 1);
                            }
                        }
                    }
                }
            }
            if (stack.size() <= 0) {
                if (this.ExtremValue == null) {
                    this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                    this.verificationOutput.counterExampleTrace = null;
                } else {
                    this.verificationOutput.verificationResult = VerificationResultType.VALID;
                }
                this.verificationOutput.noOfStates = (long) stringHashTable.count;
                return;
            }
        }
        this.verificationOutput.noOfStates = (long) stringHashTable.count;
    }

    private void MonoBFSVerification() throws Exception {
        StringHashTable stringHashTable = new StringHashTable(Ultility.MC_INITIAL_SIZE);
        Expression reachableStateCondition = this.reachableStateCondition;
        Queue<ConfigurationBase> queue = new ArrayDeque<>(1024);
        Queue<List<ConfigurationBase>> queue2 = new ArrayDeque<>(1024);
        stringHashTable.add(this.initialStep.getID());
        queue.add(this.initialStep);
        queue2.add(Arrays.asList(this.initialStep));
        this.ExtremValue = null;
        this.ReachableCount = 0;
        while (!this.cancelRequested()) {
            ConfigurationBase configurationBase = queue.poll();
            List<ConfigurationBase> collection = queue2.poll();
            Expression expression = configurationBase.evaluateExpression(this.ConstraintCondition);
            if (expression instanceof IntConstant) {
                int value = ((IntConstant) expression)._value;
                if (configurationBase.implyCondition(reachableStateCondition)) {
                    this.ReachableCount++;
                    if (this.ExtremValue == null) {
                        this.ExtremValue = Integer.valueOf(value);
                        this.verificationOutput.counterExampleTrace = new ArrayList<>(collection);
                    } else {
                        switch (this.Contraint) {
                            case MIN:
                                if (value < this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(collection);
                                }
                                break;
                            case MAX:
                                if (value > this.ExtremValue.intValue()) {
                                    this.ExtremValue = Integer.valueOf(value);
                                    this.verificationOutput.counterExampleTrace = new ArrayList<>(collection);
                                }
                                break;
                        }
                    }
                } else {
                    boolean flag = false;
                    if (this.ExtremValue != null) {
                        switch (this.Contraint) {
                            case MIN:
                                if (value > this.ExtremValue.intValue()) {
                                    flag = true;
                                }
                                break;
                            case MAX:
                                if (value < this.ExtremValue.intValue()) {
                                    flag = true;
                                }
                                break;
                        }
                    }
                    if (!flag) {
                        ConfigurationBase[] array = configurationBase.makeOneMove().toArray(new ConfigurationBase[0]);
                        this.verificationOutput.transitions += (long) array.length;
                        for (int i = array.length - 1; i >= 0; i--) {
                            ConfigurationBase configurationBase2 = array[i];
                            String id = configurationBase2.getID();
                            if (!stringHashTable.containsKey(id)) {
                                stringHashTable.add(id);
                                queue.add(configurationBase2);
                                List<ConfigurationBase> temp = new ArrayList<>(collection);
                                temp.add(configurationBase2);
                                queue2.add(temp);
                            }
                        }
                    }
                }
            }
            if (queue.size() <= 0) {
                if (this.ExtremValue == null) {
                    this.verificationOutput.verificationResult = VerificationResultType.INVALID;
                    this.verificationOutput.counterExampleTrace = null;
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
            stringBuilder.append(
                    "The Assertion ("
                            + this.toString()
                            + ") is VALID and "
                            + this.Contraint.toString().toLowerCase()
                            + "("
                            + this.ConstraintCondition
                            + ") = "
                            + this.ExtremValue
                            + " among "
                            + this.ReachableCount
                            + " reachable states." + "\n");
            stringBuilder.append("The following trace leads to a state where the condition is satisfied with the above value." + "\n");
            this.verificationOutput.getCounterxampleString(stringBuilder);
        } else if (this.verificationOutput.verificationResult == VerificationResultType.UNKNOWN) {
            stringBuilder.append("The Assertion (" + this.toString() + ") is NEITHER PROVED NOR DISPROVED." + "\n");
        } else {
            stringBuilder.append("The Assertion is NOT valid." + "\n");
        }
        if (this.selectedEngineName == "Shortest Witness Trace using Breadth First Search with Monotonically With Value") {
            stringBuilder.append("Warning: the correctness of the verification result requires the value of "
                    + this.ConstraintCondition + " changes monotonically during the system execution. " + "\n");
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
    public String getResultStringForUnfinishedSearching(Exception ex) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("********Verification Result********" + "\n");
        if (ex != null) {
            stringBuilder.append("Exception_happened_during_the_verification" + "\n");
            String str = "";
            if (ex.getMessage().contains("trace")) {
                str = "\n" + "Trace leads to exception:" + "\n" + ex.getMessage();
            }
            stringBuilder.append(ex.getMessage() + str + "\n");
        } else {
            stringBuilder.append("Verification cancelled" + "\n");
        }
        if (this.ExtremValue != null) {
            stringBuilder.append("During the incomplete search:" + "\n");
            stringBuilder.append(
                    "The Assertion ("
                            + this.toString()
                            + ") is VALID and "
                            + this.Contraint.toString().toLowerCase()
                            + "("
                            + this.ConstraintCondition
                            + ") = "
                            + this.ExtremValue
                            + " among "
                            + this.ReachableCount
                            + " reachable states."
                            + "\n");
            stringBuilder.append("The following trace leads to a state where the condition is satisfied with the above value." + "\n");
            this.verificationOutput.getCounterxampleString(stringBuilder);
            if (this.selectedEngineName == "Shortest Witness Trace using Breadth First Search with Monotonically With Value") {
                stringBuilder.append("Warning: the correctness of the verification result requires the value of "
                        + this.ConstraintCondition + " changes monotonically during the system execution. " + "\n");
            }
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
