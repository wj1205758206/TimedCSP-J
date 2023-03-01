package common.classes.expressions.expressionclass;

import java.util.List;
import java.util.Map;

public final class Assignment extends Expression {
    public String leftHandSide;

    public Expression rightHandSide;

    public Assignment(String l, Expression r) {
        this.leftHandSide = l;
        this.rightHandSide = r;
        this.expressionType = ExpressionType.Assignment;
        this.hasVar = true;
        this.expressionID = this.leftHandSide + "=" + this.rightHandSide.expressionID;
    }

    @Override
    public String toString() {
        return this.leftHandSide + " = " + this.rightHandSide.toString() + ";";
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.rightHandSide.getVars();
        if (!vars.contains(this.leftHandSide)) {
            vars.add(this.leftHandSide);
        }
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new Assignment(constMapping.containsKey(this.leftHandSide)
                ? constMapping.get(this.leftHandSide).expressionID
                : this.leftHandSide
                , this.rightHandSide.clearConstant(constMapping));
    }

    public static boolean isComplexUpdate(List<Integer> updatedVariablesBefore, List<Integer> updatedVariablesInNewUpdate, List<Integer> usedVariableInNewUpdate) {
        return updatedVariablesInNewUpdate.size() > 0
                && usedVariableInNewUpdate.contains(updatedVariablesInNewUpdate.get(0))
                && updatedVariablesBefore.contains(updatedVariablesInNewUpdate.get(0));
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return this.rightHandSide.hasExternalLibraryCall();
    }
}
