package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.List;
import java.util.Map;

public final class ClassPropertyAssignment extends Expression {
    public ClassProperty classProperty;


    public Expression rightHandExpression;

    public ClassPropertyAssignment(ClassProperty property, Expression rhs) {
        this.classProperty = property;
        this.rightHandExpression = rhs;
        this.expressionType = ExpressionType.ClassPropertyAssignment;
        this.hasVar = (this.classProperty.hasVar || rhs.hasVar);
        this.expressionID = this.classProperty.expressionID + "=" + this.rightHandExpression.expressionID;
    }

    @Override
    public String toString() {
        return this.classProperty + "=" + this.rightHandExpression + ";";
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.classProperty.getVars();
        Ultility.addList(vars, this.rightHandExpression.getVars());
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new ClassPropertyAssignment(
                (ClassProperty) this.classProperty.clearConstant(constMapping),
                this.rightHandExpression.clearConstant(constMapping));
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return true;
    }
}
