package common.classes.expressions.expressionclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Expression implements Serializable {
    public ExpressionType expressionType;

    public String expressionID;

    public boolean hasVar;

    public List<String> getVars() {
        return new ArrayList<>(0);
    }

    public Expression clearConstant(Map<String, Expression> constMapping) {
        return this;
    }

    public boolean hasExternalLibraryCall() {
        return false;
    }

    public static Expression combineGuard(Expression guard1, Expression guard2) {
        if (guard1 != null && guard2 != null) {
            return Expression.AND(new Expression[]
                    {
                            guard1,
                            guard2
                    });
        }
        if (guard1 == null) {
            return guard2;
        }
        return guard1;
    }

    public static Expression combineProgramBlock(Expression block1, Expression block2) {
        if (block1 != null && block2 != null) {
            return new Sequence(block1, block2);
        }
        if (block1 == null) {
            return block2;
        }
        return block1;
    }

    public static Expression AND(Expression... exps) {
        Expression expression = exps[0];
        for (int i = 1; i < exps.length; i++) {
            expression = new PrimitiveApplication("&&", expression, exps[i]);
        }
        return expression;
    }

    public static Expression OR(Expression... exps) {
        Expression expression = exps[0];
        for (int i = 1; i < exps.length; i++) {
            expression = new PrimitiveApplication("||", expression, exps[i]);
        }
        return expression;
    }

    public static Expression NOT(Expression exp1) {
        return new PrimitiveApplication("!", exp1);
    }


    public static Expression PLUS(Expression... exps) {
        Expression expression = exps[0];
        for (int i = 1; i < exps.length; i++) {
            expression = new PrimitiveApplication("+", expression, exps[i]);
        }
        return expression;
    }

    public static Expression MINUS(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("-", exp1, exp2);
    }


    public static Expression TIMES(Expression... exps) {
        Expression expression = exps[0];
        for (int i = 1; i < exps.length; i++) {
            expression = new PrimitiveApplication("*", expression, exps[i]);
        }
        return expression;
    }

    public static Expression DIVIDE(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("/", exp1, exp2);
    }


    public static Expression MOD(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("mod", exp1, exp2);
    }


    public static Expression NEG(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("~", exp1, exp2);
    }


    public static Expression EQ(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("==", exp1, exp2);
    }


    public static Expression NE(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("!=", exp1, exp2);
    }


    public static Expression GT(Expression exp1, Expression exp2) {
        return new PrimitiveApplication(">", exp1, exp2);
    }


    public static Expression GE(Expression exp1, Expression exp2) {
        return new PrimitiveApplication(">=", exp1, exp2);
    }


    public static Expression LT(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("<", exp1, exp2);
    }


    public static Expression LE(Expression exp1, Expression exp2) {
        return new PrimitiveApplication("<=", exp1, exp2);
    }

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public String getExpressionID() {
        return expressionID;
    }

    public void setExpressionID(String expressionID) {
        this.expressionID = expressionID;
    }

    public boolean isHasVar() {
        return hasVar;
    }

    @Override
    public String toString() {
        return "Expression{" +
                "expressionType=" + expressionType +
                ", expressionID='" + expressionID + '\'' +
                ", hasVar=" + hasVar +
                '}';
    }

    public void setHasVar(boolean hasVar) {
        this.hasVar = hasVar;
    }

}
