package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NewObjectCreation extends Expression {
    public String className;


    public Expression[] arguments;

    public NewObjectCreation(String method, Expression[] args) {
        this.expressionType = ExpressionType.NewObjectCreation;
        this.className = method;
        this.arguments = args;
        this.hasVar = true;
        StringBuilder stringBuilder = new StringBuilder("N@" + this.className + "(");
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
        StringBuilder stringBuilder = new StringBuilder("new " + this.className + "(");
        for (int i = 0; i < this.arguments.length; i++) {
            if (i == this.arguments.length - 1) {
                stringBuilder.append(this.arguments[i].toString());
            } else {
                stringBuilder.append(this.arguments[i] + ", ");
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
        return list;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.arguments.length];
        for (int i = 0; i < this.arguments.length; i++) {
            array[i] = this.arguments[i].clearConstant(constMapping);
        }
        return new NewObjectCreation(this.className, array);
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return true;
    }
}
