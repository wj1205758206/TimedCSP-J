package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.List;
import java.util.Map;

public final class PropertyAssignment extends Expression {

    public Expression recordExpression;


    public Expression propertyExpression;


    public Expression rightHandExpression;

    public PropertyAssignment(Expression rec, Expression p, Expression rhs) {
        this.recordExpression = rec;
        this.propertyExpression = p;
        this.rightHandExpression = rhs;
        this.expressionType = ExpressionType.PropertyAssignment;
        this.hasVar = (this.recordExpression.hasVar || this.propertyExpression.hasVar || rhs.hasVar);
        this.expressionID = this.recordExpression.expressionID
                + "["
                + this.propertyExpression.expressionID
                + "]="
                + this.rightHandExpression.expressionID;

    }

    @Override
    public String toString() {
        return this.recordExpression + "[" + this.propertyExpression + "]=" + this.rightHandExpression;
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.recordExpression.getVars();
        Ultility.addList(vars, this.propertyExpression.getVars());
        Ultility.addList(vars, this.rightHandExpression.getVars());
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new PropertyAssignment(this.recordExpression.clearConstant(constMapping),
                this.propertyExpression.clearConstant(constMapping),
                this.rightHandExpression.clearConstant(constMapping));
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return this.recordExpression.hasExternalLibraryCall()
                || this.propertyExpression.hasExternalLibraryCall()
                || this.rightHandExpression.hasExternalLibraryCall();
    }
}
