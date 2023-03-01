package common.classes.expressions.expressionclass;

public final class VariableValueOutOfRangeException extends RuntimeException {
    public VariableValueOutOfRangeException(String message) {
        super("VariableValueOutOfRangeException: " + message);
    }
}
