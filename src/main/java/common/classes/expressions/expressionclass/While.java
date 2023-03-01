package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.List;
import java.util.Map;

public final class While extends Expression {
    public Expression test;
    public Expression body;

    public While(Expression t, Expression b) {
        this.test = t;
        this.body = b;
        this.hasVar = (t.hasVar || b.hasVar);
        this.expressionType = ExpressionType.While;
        this.expressionID = "W@" + this.test.expressionID + "{" + this.body.expressionID + "}";
    }

    @Override
    public String toString() {
        return "while (" + this.test.toString() + ") {" + this.body.toString() + "}";
    }

    @Override
    public List<String> getVars() {
        List<String> vars = this.test.getVars();
        Ultility.addList(vars, this.body.getVars());
        return vars;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new While(this.test.clearConstant(constMapping), this.body.clearConstant(constMapping));
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return this.test.hasExternalLibraryCall() || this.body.hasExternalLibraryCall();
    }
}
