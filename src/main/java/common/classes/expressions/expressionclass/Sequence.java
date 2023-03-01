package common.classes.expressions.expressionclass;


import common.classes.ultility.Ultility;

import java.util.List;
import java.util.Map;

public final class Sequence extends Expression {

    public Expression firstPart;


    public Expression secondPart;

    public Sequence(Expression f, Expression s) {
        this.firstPart = f;
        this.secondPart = s;
        this.hasVar = (f.hasVar || s.hasVar);
        this.expressionType = ExpressionType.Sequence;
        this.expressionID = this.firstPart.expressionID + ";" + this.secondPart.expressionID;
    }


    @Override
    public String toString() {
        return this.firstPart + " " + this.secondPart;
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.firstPart.getVars();
        Ultility.addList(vars, this.secondPart.getVars());
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new Sequence(this.firstPart.clearConstant(constMapping), this.secondPart.clearConstant(constMapping));
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return this.firstPart.hasExternalLibraryCall() || this.secondPart.hasExternalLibraryCall();
    }
}
