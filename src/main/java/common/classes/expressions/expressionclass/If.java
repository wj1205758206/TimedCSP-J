package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.List;
import java.util.Map;

public final class If extends Expression {
    public Expression condition;


    public Expression thenPart;


    public Expression elsePart;

    public If(Expression c, Expression t, Expression e) {
        this.condition = c;
        this.thenPart = t;
        this.elsePart = e;
        if (e != null) {
            this.hasVar = (c.hasVar || t.hasVar || e.hasVar);
            this.expressionID = "I@" + this.condition.expressionID + "{" + this.thenPart.expressionID + "^" + this.elsePart.expressionID + "}";
        } else {
            this.hasVar = (c.hasVar || t.hasVar);
            this.expressionID = "I@" + this.condition.expressionID + "{" + this.thenPart.expressionID + "}";

        }
        this.expressionType = ExpressionType.If;
    }

    @Override
    public String toString() {
        if (this.elsePart != null) {
            return " if ("
                    + this.condition
                    + ") {"
                    + this.thenPart
                    + "} else {"
                    + this.elsePart
                    + "}";
        }
        return " if ("
                + this.condition
                + ") {"
                + this.thenPart
                + "}";
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.condition.getVars();
        Ultility.addList(vars, this.thenPart.getVars());
        if (this.elsePart != null) {
            Ultility.addList(vars, this.elsePart.getVars());
        }
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new If(
                this.condition.clearConstant(constMapping),
                this.thenPart.clearConstant(constMapping),
                (this.elsePart == null) ? null : this.elsePart.clearConstant(constMapping));
    }

    @Override
    public boolean hasExternalLibraryCall() {
        if (this.elsePart != null) {
            return this.condition.hasExternalLibraryCall() || this.thenPart.hasExternalLibraryCall() || this.elsePart.hasExternalLibraryCall();
        }
        return this.condition.hasExternalLibraryCall() || this.thenPart.hasExternalLibraryCall();
    }
}
