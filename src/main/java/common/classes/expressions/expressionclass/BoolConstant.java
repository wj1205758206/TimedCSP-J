package common.classes.expressions.expressionclass;

public final class BoolConstant extends ExpressionValue {
    public boolean _value;

    public String _const;

    public BoolConstant(boolean v) {
        this._value = v;
        this.expressionID = (this._value ? "T" : "F");
    }

    public BoolConstant(boolean v, String constName) {
        this._value = v;
        this._const = constName;
        this.expressionID = (this._value ? "T" : "F");
    }

    @Override
    public String toString() {
        if (this._const != null) {
            return this._const;
        }
        if (!this._value) {
            return "false";
        }
        return "true";
    }

    @Override
    public ExpressionValue getClone() {
        return this;
    }
}
