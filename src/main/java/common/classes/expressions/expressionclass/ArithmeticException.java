package common.classes.expressions.expressionclass;

public final class ArithmeticException extends RuntimeException {
    public ArithmeticException(String msg) {
        super("ArithmeticException: " + msg);
    }
}
