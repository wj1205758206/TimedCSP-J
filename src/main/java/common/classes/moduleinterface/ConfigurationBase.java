package common.classes.moduleinterface;

import common.ParsingException;
import common.classes.datastructure.StringHashTable;
import common.classes.expressions.EvaluatorDenotational;
import common.classes.expressions.Valuation;
import common.classes.expressions.expressionclass.BoolConstant;
import common.classes.expressions.expressionclass.Expression;
import common.classes.expressions.expressionclass.ExpressionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class ConfigurationBase {
    public String event;


    public String displayName;


    public Valuation globalEnv;


    public boolean isDeadLock;


    public boolean isAtomic;


    public boolean isDataOperation;


    public String[] participatingProcesses;

    public abstract List<ConfigurationBase> makeOneMove() throws ParsingException, CloneNotSupportedException;

    public List<ConfigurationBase> makeOneMove(String evt) throws ParsingException, CloneNotSupportedException {
        List<ConfigurationBase> list = new ArrayList<ConfigurationBase>();
        List<ConfigurationBase> enumerable = this.makeOneMove();
        for (ConfigurationBase configurationBase : enumerable) {
            if (configurationBase.event == evt) {
                list.add(configurationBase);
            }
        }
        return list;
    }

    public boolean implyCondition(Expression expression) {
        ExpressionValue expressionValue = EvaluatorDenotational.evaluate(expression, this.globalEnv);
        if (expressionValue instanceof BoolConstant) {
            BoolConstant boolConstant = (BoolConstant) expressionValue;
            return boolConstant._value;
        }
        return false;
    }

    public Expression evaluateExpression(Expression expression) {
        return EvaluatorDenotational.evaluate(expression, this.globalEnv);
    }

    public boolean equalsV(ConfigurationBase input) {
        return this.globalEnv.getID() == input.globalEnv.getID();
    }

    public String getDisplayEvent() {
        if (this.displayName == null || this.displayName.isEmpty()) {
            return this.event;
        }
        return this.displayName;
    }

    public abstract String getID();

    public String getIDWithEvent() {
        return this.getID() + "$" + this.event;
    }

    public boolean IsDivergent() throws Exception {
        Stack<ConfigurationBase> stack = new Stack<ConfigurationBase>();
        List<String> list = new ArrayList<>(100);
        StringHashTable stringHashTable = new StringHashTable(100);
        Stack<Integer> stack2 = new Stack<Integer>();
        stack2.push(0);
        List<Integer> list2 = new ArrayList<>(1024);
        stack.push(this);
        stringHashTable.add(this.getID());
        while (stack.size() > 0) {
            ConfigurationBase configurationBase = stack.pop();
            Iterable<ConfigurationBase> enumerable = configurationBase.makeOneMove("τ");
            int num = stack2.pop();
            if (num > 0) {
                while (list2.get(list2.size() - 1) >= num) {
                    int index = list2.size() - 1;
                    list2.remove(index);
                    list.remove(index);
                }
            }
            list.add(configurationBase.getID());
            list2.add(num);
            if (enumerable != null) {
                for (ConfigurationBase configurationBase2 : enumerable) {
                    String id = configurationBase2.getID();
                    if (list.contains(id)) {
                        return true;
                    }
                    if (!stringHashTable.containsKey(id)) {
                        stringHashTable.add(id);
                        stack.push(configurationBase2);
                        stack2.push(num + 1);
                    }
                }
                continue;
            }
        }
        return false;
    }
}
