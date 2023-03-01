package common.classes.expressions.expressionclass;

public final class EvaluatingException extends RuntimeException{
    public EvaluatingException(String message){
        super("EvaluatingException: " + message);
    }
}
