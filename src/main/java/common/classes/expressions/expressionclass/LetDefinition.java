package common.classes.expressions.expressionclass;

import java.util.List;
import java.util.Map;

public final class LetDefinition extends Expression {
    public String variable;


    public Expression rightHandExpression;

    public LetDefinition(String v, Expression rhe) {
        this.variable = v;
        this.rightHandExpression = rhe;
        this.hasVar = true;
        this.expressionType = ExpressionType.Let;
        this.expressionID = this.variable + "=" + this.rightHandExpression.expressionID;
    }

    @Override
    public String toString() {
        return "var " + this.variable + " = " + this.rightHandExpression;
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.rightHandExpression.getVars();
        if (!vars.contains(this.variable)) {
            vars.add(this.variable);
        }
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new LetDefinition(this.variable, this.rightHandExpression.clearConstant(constMapping));
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return this.rightHandExpression.hasExternalLibraryCall();
    }
}
