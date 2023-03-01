package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ClassMethodCall extends Expression {

    public String variable;


    public String methodName;

    public Expression[] arguments;

    public ClassMethodCall(String var, String method, Expression[] args) {
        this.variable = var;
        this.expressionType = ExpressionType.ClassMethodCall;
        this.methodName = method;
        this.arguments = args;
        this.hasVar = true;
        StringBuilder stringBuilder = new StringBuilder(this.variable + "." + this.methodName + "(");
        for (int i = 0; i < this.arguments.length; i++) {
            if (i == this.arguments.length - 1) {
                stringBuilder.append(this.arguments[i].expressionID);
            } else {
                stringBuilder.append(this.arguments[i].expressionID + ",");
            }
        }
        stringBuilder.append(")");
        this.expressionID = stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.variable + "." + this.methodName + "(");
        for (int i = 0; i < this.arguments.length; i++) {
            if (i == this.arguments.length - 1) {
                stringBuilder.append(this.arguments[i].toString());
            } else {
                stringBuilder.append(this.arguments[i].toString() + ", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public List<String> getVars() {
        List<String> list = new ArrayList<>(16);
        for (Expression expression : this.arguments) {
            Ultility.addList(list, expression.getVars());
        }
        list.add(this.variable);
        return list;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.arguments.length];
        for (int i = 0; i < this.arguments.length; i++) {
            array[i] = this.arguments[i].clearConstant(constMapping);
        }
        if (constMapping.containsKey(this.variable)) {
            return new ClassMethodCallInstance((ExpressionValue) constMapping.get(this.variable), this.methodName, array);
        }
        return new ClassMethodCall(this.variable, this.methodName, array);
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return true;
    }
}
