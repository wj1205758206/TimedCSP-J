package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.List;
import java.util.Map;

public final class ClassMethodCallInstance extends Expression {

    public Expression variable;

    public String methodName;


    public Expression[] arguments;

    public ClassMethodCallInstance(Expression var, String method, Expression[] args) {
        this.variable = var;
        this.expressionType = ExpressionType.ClassMethodCallInstance;
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
                stringBuilder.append(this.arguments[i] + ", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.variable.getVars();
        for (Expression expression : this.arguments) {
            Ultility.addList(vars, expression.getVars());
        }
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.arguments.length];
        for (int i = 0; i < this.arguments.length; i++) {
            array[i] = this.arguments[i].clearConstant(constMapping);
        }
        return new ClassMethodCallInstance(this.variable.clearConstant(constMapping), this.methodName, array);
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return true;
    }
}
