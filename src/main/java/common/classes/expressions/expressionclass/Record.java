package common.classes.expressions.expressionclass;

import common.classes.ultility.Ultility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Record extends Expression {
    public Expression[] associations;

    public Record(Expression[] ass) {
        this.associations = ass;
        StringBuilder stringBuilder = new StringBuilder("[");
        for (Expression expression : this.associations) {
            this.hasVar = (this.hasVar || expression.hasVar);
            stringBuilder.append(expression.expressionID + ",");
        }
        this.expressionType = ExpressionType.Record;
        this.expressionID = stringBuilder.toString();
    }

    public Record(int size) {
        this.associations = new Expression[size];
        for (int i = 0; i < size; i++) {
            this.associations[i] = new IntConstant(0);
        }
        this.expressionType = ExpressionType.Record;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < this.associations.length; i++) {
            if (i != this.associations.length - 1) {
                stringBuilder.append(this.associations[i].toString() + ", ");
            } else {
                stringBuilder.append(this.associations[i].toString());
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public List<String> getVars() {
        List<String> list = new ArrayList<>(16);
        for (Expression expression : this.associations) {
            Ultility.addList(list, expression.getVars());
        }
        return list;
    }

    @Override
    public Expression clearConstant(Map<String, Expression> constMapping) {
        Expression[] array = new Expression[this.associations.length];
        for (int i = 0; i < this.associations.length; i++) {
            array[i] = this.associations[i].clearConstant(constMapping);
        }
        return new Record(array);
    }

    @Override
    public boolean hasExternalLibraryCall() {
        for (int i = 0; i < this.associations.length; i++) {
            if (this.associations[i].hasExternalLibraryCall()) {
                return true;
            }
        }
        return false;
    }
}
