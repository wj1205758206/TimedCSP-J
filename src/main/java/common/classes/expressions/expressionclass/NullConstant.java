package common.classes.expressions.expressionclass;

public final class NullConstant extends ExpressionValue {
    public NullConstant() {
        this.expressionID = "null";
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public ExpressionValue getClone() {
        return this;
    }
}
