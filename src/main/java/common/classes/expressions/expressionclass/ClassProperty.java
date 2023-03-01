package common.classes.expressions.expressionclass;

import java.util.List;
import java.util.Map;

public final class ClassProperty extends Expression {
    public Expression variable;

    // Token: 0x04000492 RID: 1170
    public String propertyName;

    public ClassProperty(Expression var, String property) {
        this.variable = var;
        this.expressionType = ExpressionType.ClassProperty;
        this.propertyName = property;
        this.hasVar = true;
        this.expressionID = this.variable + "." + this.propertyName;
    }

    @Override
    public String toString() {
        return this.variable + "." + this.propertyName;
    }

    @Override
    public List<String> getVars() {
        return this.variable.getVars();
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        return new ClassProperty(this.variable.clearConstant(constMapping), this.propertyName);
    }

    @Override
    public boolean hasExternalLibraryCall() {
        return true;
    }
}
