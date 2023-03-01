package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StaticMethodCall extends Expression {
    public String methodName;


    public Expression[] arguments;

    public StaticMethodCall(String method, Expression[] args) {
        this.expressionType = ExpressionType.StaticMethodCall;
        this.methodName = method;
        this.arguments = args;
        StringBuilder stringBuilder = new StringBuilder("C@" + this.methodName + "(");
        for (Expression expression : this.arguments) {
            this.hasVar = (this.hasVar || expression.hasVar);
            stringBuilder.append(expression.expressionID + ",");
        }
        this.expressionID = stringBuilder.toString();
    }

    @Override
    public String toString() {
        if (this.arguments.length > 0) {
            StringBuilder stringBuilder = new StringBuilder("call(" + this.methodName + ",");
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
        return "call(" + this.methodName + ")";
    }

    @Override
    public List<String> getVars() {
        List<String> list = new ArrayList<>(16);
        for (Expression expression : this.arguments) {
            Ultility.addList(list, expression.getVars());
        }
        return list;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.arguments.length];
        for (int i = 0; i < this.arguments.length; i++) {
            array[i] = this.arguments[i].clearConstant(constMapping);
        }
        return new StaticMethodCall(this.methodName, array);
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return this.methodName != "cfull"
                && this.methodName != "cempty"
                && this.methodName != "ccount" && this.methodName != "csize" && this.methodName != "cpeek";
    }


}
