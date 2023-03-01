package common.classes.expressions.expressionclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Variable extends Expression {
    public Variable(String name) {
        this.hasVar = true;
        this.expressionType = ExpressionType.Variable;
        this.expressionID = name;
    }

    @Override
    public String toString() {
        return this.expressionID;
    }

    @Override
    public List<String> getVars() {
        return new ArrayList<String>(Arrays.asList(this.expressionID));
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        if (constMapping.containsKey(this.expressionID)) {
            return constMapping.get(this.expressionID);
        }
        return new Variable(this.expressionID);
    }
}
