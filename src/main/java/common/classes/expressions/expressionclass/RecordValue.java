package common.classes.expressions.expressionclass;

public final class RecordValue extends ExpressionValue {
    public ExpressionValue[] associations;

    public RecordValue(ExpressionValue[] ass) {
        this.associations = ass;
        this.getID();
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

    public void getID() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.associations.length; i++) {
            if (i != this.associations.length - 1) {
                stringBuilder.append(this.associations[i].expressionID + ",");
            } else {
                stringBuilder.append(this.associations[i].expressionID);
            }
        }
        this.expressionID = stringBuilder.toString();
    }

    @Override
    public ExpressionValue getClone() {
        ExpressionValue[] array = new ExpressionValue[this.associations.length];
        for (int i = 0; i < this.associations.length; i++) {
            array[i] = this.associations[i].getClone();
        }
        return new RecordValue(array);
    }
}
