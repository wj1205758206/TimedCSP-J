package common.classes.expressions.expressionclass;

import java.io.*;

public abstract class ExpressionValue extends Expression implements Serializable {
    public ExpressionValue getClone() {
        ExpressionValue result;
        try {
            if (!(this instanceof Serializable)) {
                throw new IllegalArgumentException("The class " + this.getClass().getName() + " must be serializable or override the clone method manually.");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            ExpressionValue expressionValue = (ExpressionValue) ois.readObject();
            ois.close();
            result = expressionValue;

        } catch (Exception ex) {
            throw new RuntimeException("Runtime exception when cloning the object " + this.getClass().getName() + "Please check the serialization implementation of the class.");
        }
        return result;
    }
}
