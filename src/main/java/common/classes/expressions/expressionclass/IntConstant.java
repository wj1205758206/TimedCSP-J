package common.classes.expressions.expressionclass;

public final class IntConstant extends ExpressionValue {
    public int _value;
    public String _const;

    public IntConstant(int v) {
        this._value = v;
        this.expressionID = String.valueOf(this._value);
    }

    public IntConstant(int v, String constName) {
        this._value = v;
        this._const = constName;
        this.expressionID = String.valueOf(this._value);
    }

    @Override
    public String toString() {
        if (this._const == null) {
            return String.valueOf(this._value);
        }
        return this._const;
    }

    @Override
    public ExpressionValue getClone() {
        return this;
    }
}
